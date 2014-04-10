package org.holoeverywhere.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.extension.fakeandroid.FakeAndroidExtension

import javax.inject.Inject

public class HoloEverywhereJarLibraryPlugin extends HoloEverywhereAbstractPlugin implements HoloEverywherePublishPlugin.PublishInjector {
    private HoloEverywhereExtension extension
    private FakeAndroidExtension androidExtension
    private Project project
    private Task taskJavadocJar, taskSourcesJar

    @Inject
    public HoloEverywhereJarLibraryPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        super(instantiator, repositoryFactory)
    }

    @Override
    void apply(Project project) {
        this.project = project

        extension = extension(project)
        extension.forceJarInsteadAar = true
        extension.resbuilder.enable = false
        extension.publish.packaging = 'jar'

        androidExtension = project.extensions.create('android', FakeAndroidExtension, project)

        project.plugins.apply(JavaPlugin)
        checkPluginOrder(project)
        loadCorePlugin(project)

        configureJavadoc(project)
        configureSources(project)

        project.afterEvaluate { afterEvaluate(project) }
    }

    def Task configureJavadoc(Project project) {
        Javadoc javadocTask = project.tasks.getByName(HoloEverywhereLibraryPlugin.GENERATE_JAVADOC_TASK_NAME) as Javadoc

        Jar javadocJarTask = project.tasks.create(HoloEverywhereLibraryPlugin.JAVADOC_JAR_TASK_NAME, Jar)
        javadocJarTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            classifier = 'javadoc'

            dependsOn javadocTask
            from javadocTask.destinationDir
        }
        return taskJavadocJar = javadocJarTask;
    }

    def Task configureSources(Project project) {
        JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
        SourceSet sourceSet = convention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        Jar sourcesJarTask = project.tasks.create(HoloEverywhereLibraryPlugin.SOURCES_JAR_TASK_NAME, Jar)
        sourcesJarTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            classifier = 'source'

            from sourceSet.allSource
        }
        return taskSourcesJar = sourcesJarTask
    }

    void afterEvaluate(Project project) {
        if (!artifactsPrepared) {
            prepareArtifactsForPublication()
        }
    }

    private boolean artifactsPrepared = false

    @Override
    void prepareArtifactsForPublication() {
        if (artifactsPrepared) {
            throw new RuntimeException("Artifacts already prepared for publication")
        }
        artifactsPrepared = true

        project.dependencies.add(HoloEverywhereCorePlugin.LIBRARIES_CONFIGURATION, androidExtension.compileApi())

        if (extension.library.classes) {
            publish(extension, project.tasks.getByName('jar'))
        }
        publish(extension, taskJavadocJar, extension.library.javadoc)
        publish(extension, taskSourcesJar, extension.library.sources)
    }
}
