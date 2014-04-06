package org.holoeverywhere.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.builder.model.SigningConfig
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject

class HoloEverywhereAppPlugin extends HoloEverywhereAbstractPlugin {
    private HoloEverywhereExtension extension
    private AppExtension androidExtension

    private Task taskApk
    private DefaultPublishArtifact artifactApk

    @Inject
    HoloEverywhereAppPlugin(Instantiator instantiator) {
        super(instantiator)
    }

    @Override
    void apply(Project project) {
        checkPluginOrder(project)
        loadCorePlugin(project)

        extension = extension(project)
        extension.publish.packaging = 'apk'
        extension.publish.artifact(configureApk(project, 'release'))

        project.afterEvaluate { afterEvaluate(project) }

        androidExtension = project.plugins.apply(AppPlugin).extension as AppExtension
    }

    def void afterEvaluate(Project project) {
        taskApk = project.tasks.getByName('assembleRelease')
        taskApk.enabled = extension.app.attachReleaseApk && extension.signing.release.valid()
        artifactApk.builtBy(taskApk)

        if (extension.signing.enable) {
            if (extension.signing.release.valid()) {
                createSigningConfig(extension.signing.release.obtainConfig('release'), 'release')
            }
            createSigningConfig(extension.signing.debug.obtainMaybeDebugConfig('debug'), 'debug')
        }

    }

    PublishArtifact configureApk(Project project, String type) {
        return artifactApk = new DefaultPublishArtifact(project.name, 'apk', 'apk',
                '', new Date(), project.file("${project.buildDir}/apk/${project.name}-${type}.apk"))
    }

    def void createSigningConfig(SigningConfig signingConfig, String name) {
        androidExtension.signingConfigs.add(signingConfig)
        androidExtension.buildTypes.getByName(name).setSigningConfig(signingConfig)
    }
}
