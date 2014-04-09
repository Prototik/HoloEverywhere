package org.holoeverywhere.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.builder.model.SigningConfig
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject

public class HoloEverywhereAppPlugin extends HoloEverywhereAbstractPlugin implements HoloEverywherePublishPlugin.PublishInjector {
    private HoloEverywhereExtension extension
    private AppExtension androidExtension

    private Task taskApk
    private DefaultPublishArtifact artifactApk

    @Inject
    HoloEverywhereAppPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        super(instantiator, repositoryFactory)
    }

    @Override
    void apply(Project project) {
        checkPluginOrder(project)
        loadCorePlugin(project)

        project.afterEvaluate { afterEvaluate(project) }
        androidExtension = project.plugins.apply(AppPlugin).extension as AppExtension

        extension = extension(project)
        extension.publish.packaging = 'apk'
        configureApk(project, 'release')
    }

    def void afterEvaluate(Project project) {
        if (!artifactsPrepared) {
            prepareArtifactsForPublication()
        }

        if (extension.signing.enable) {
            if (extension.signing.release.valid()) {
                createSigningConfig(extension.signing.release.obtainConfig('release'), 'release')
            }
            createSigningConfig(extension.signing.debug.obtainMaybeDebugConfig('debug'), 'debug')
        }
    }

    PublishArtifact configureApk(Project project, String type) {
        taskApk = project.tasks.getByName('assembleRelease')
        return artifactApk = new DefaultPublishArtifact(project.name, 'apk', 'apk',
                '', new Date(), project.file("${project.buildDir}/apk/${project.name}-${type}.apk"), taskApk)
    }

    def void createSigningConfig(SigningConfig signingConfig, String name) {
        androidExtension.signingConfigs.add(signingConfig)
        androidExtension.buildTypes.getByName(name).setSigningConfig(signingConfig)
    }

    private boolean artifactsPrepared = false

    @Override
    void prepareArtifactsForPublication() {
        if (artifactsPrepared) {
            throw new RuntimeException("Artifacts already prepared for publication")
        }
        artifactsPrepared = true

        publish(extension, taskApk, extension.app.attachReleaseApk && extension.signing.release.valid())
    }
}
