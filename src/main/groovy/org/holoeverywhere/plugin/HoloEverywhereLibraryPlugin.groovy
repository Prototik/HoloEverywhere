package org.holoeverywhere.plugin

import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.task.AndroidClassesJar
import org.holoeverywhere.plugin.task.AndroidJavadoc
import org.holoeverywhere.plugin.task.AndroidJavadocJar
import org.holoeverywhere.plugin.task.AndroidSourceJar

import javax.inject.Inject

class HoloEverywhereLibraryPlugin extends HoloEverywhereAbstractPlugin {
    public static final String EXTERNAL_APKLIB_ASSEMBLE_TASK_NAME = 'mavenBuild'
    public static final String EXTERNAL_APKLIB_TASK_NAME = 'apklib'
    public static final String GENERATE_JAVADOC_TASK_NAME = 'javadoc'
    public static final String JAVADOC_JAR_TASK_NAME = 'javadocJar'
    public static final String SOURCES_JAR_TASK_NAME = 'sourcesJar'
    public static final String CLASSES_JAR_TASK_NAME = 'classesJar'

    private HoloEverywhereExtension extension
    private Jar taskSources, taskJavadoc, taskClasses
    private Task taskApklib

    @Inject
    HoloEverywhereLibraryPlugin(Instantiator instantiator) {
        super(instantiator)
    }

    @Override
    void apply(Project project) {
        checkPluginOrder(project)
        loadCorePlugin(project)

        extension = extension(project)
        extension.publish.packaging = 'aar'
        extension.publish.artifact(configureSources(project))
        extension.publish.artifact(configureJavadoc(project))
        extension.publish.artifact(configureClasses(project))
        extension.publish.artifact(configureExternalApklib(project))

        project.afterEvaluate { afterEvaluate(project) }

        project.plugins.apply(LibraryPlugin)
    }

    def void afterEvaluate(Project project) {
        taskSources.enabled = extension.library.sources
        taskJavadoc.enabled = extension.library.javadoc
        taskClasses.enabled = extension.library.classes
        if (extension.library.apklibExternalCreation) {
            taskApklib.enabled = true
        }
    }

    Jar configureJavadoc(Project project) {
        AndroidJavadoc generateJavadocTask = project.tasks.create(GENERATE_JAVADOC_TASK_NAME, AndroidJavadoc)
        generateJavadocTask.configure {
            description = "Generates Javadoc API documentation for the main source code."
            configuration = project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
            sourceSet = SourceSet.MAIN_SOURCE_SET_NAME
        }

        AndroidJavadocJar packageJavadocTask = project.tasks.create(JAVADOC_JAR_TASK_NAME, AndroidJavadocJar)
        packageJavadocTask.configure {
            description = "Package Javadoc API documentation into jar archive."
            extendsFrom generateJavadocTask
        }

        return taskJavadoc = packageJavadocTask
    }

    Jar configureSources(Project project) {
        AndroidSourceJar packageSourcesTask = project.tasks.create(SOURCES_JAR_TASK_NAME, AndroidSourceJar)
        packageSourcesTask.configure {
            description = "Package main sources into jar archive."
            sourceSet = SourceSet.MAIN_SOURCE_SET_NAME
        }
        return taskSources = packageSourcesTask
    }

    Jar configureClasses(Project project) {
        AndroidClassesJar packageClassesTask = project.tasks.create(CLASSES_JAR_TASK_NAME, AndroidClassesJar)
        packageClassesTask.configure {
            description = "Package compiled classes into jar archive."
        }
        return taskClasses = packageClassesTask
    }

    DefaultPublishArtifact configureExternalApklib(Project project) {
        Exec apklibAssembleTask = project.rootProject.tasks.findByName(EXTERNAL_APKLIB_ASSEMBLE_TASK_NAME) as Exec
        if (apklibAssembleTask == null) {
            apklibAssembleTask = project.rootProject.tasks.create(EXTERNAL_APKLIB_ASSEMBLE_TASK_NAME, Exec)
            apklibAssembleTask.configure {
                enabled = false
                executable = 'mvn'
                args = ['--batch-mode', '--quiet', 'clean', 'package']
                workingDir = project.rootProject.projectDir

                group = BasePlugin.BUILD_GROUP
                description = 'Assemble apklib artifacts with Maven'
            }
        }
        taskApklib = apklibAssembleTask

        final String finalFilename = "${project.name}-${project.version}.apklib"

        Copy apklibTask = project.tasks.create(EXTERNAL_APKLIB_TASK_NAME, Copy)
        apklibTask.configure {
            dependsOn apklibAssembleTask

            from project.fileTree("${project.projectDir}/target") { include "*.apklib" }
            into project.file("${project.buildDir}/libs")
            rename '(.*)', finalFilename
        }

        return new DefaultPublishArtifact(project.name, 'apklib', 'apklib', '', new Date(), project.file(finalFilename), apklibTask)
    }
}
