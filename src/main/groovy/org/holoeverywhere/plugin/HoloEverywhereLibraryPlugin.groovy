package org.holoeverywhere.plugin

import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.plugin.task.AndroidClassesJar
import org.holoeverywhere.plugin.task.AndroidJavadoc
import org.holoeverywhere.plugin.task.AndroidJavadocJar
import org.holoeverywhere.plugin.task.AndroidSourceJar

import javax.inject.Inject

class HoloEverywhereLibraryPlugin extends HoloEverywhereBasePlugin {
    public static final String GENERATE_JAVADOC_TASK_NAME = 'javadoc'
    public static final String JAVADOC_JAR_TASK_NAME = 'javadocJar'
    public static final String SOURCES_JAR_TASK_NAME = 'sourcesJar'
    public static final String CLASSES_JAR_TASK_NAME = 'classesJar'

    private HoloEverywhereExtension extension

    @Inject
    HoloEverywhereLibraryPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        super(instantiator, registry)
    }

    @Override
    void apply(Project project) {
        extension = extension(project)

        project.plugins.apply(LibraryPlugin)

        final List<Jar> artifacts = new ArrayList<>()
        if (extension.library.javadoc) artifacts.add(configureJavadoc(project))
        if (extension.library.sources) artifacts.add(configureSources(project))
        if (extension.library.classes) artifacts.add(configureClasses(project))
        artifacts.each { Jar packageTask -> project.artifacts.add('archives', packageTask) }
    }

    static Jar configureJavadoc(Project project) {
        AndroidJavadoc generateJavadocTask = project.tasks.create(GENERATE_JAVADOC_TASK_NAME, AndroidJavadoc)
        generateJavadocTask.configure {
            description = "Generates Javadoc API documentation for the main source code."
            configuration = JavaPlugin.COMPILE_CONFIGURATION_NAME
            sourceSet = SourceSet.MAIN_SOURCE_SET_NAME
        }

        AndroidJavadocJar packageJavadocTask = project.tasks.create(JAVADOC_JAR_TASK_NAME, AndroidJavadocJar)
        packageJavadocTask.configure {
            description = "Package Javadoc API documentation into jar archive."
            extendsFrom generateJavadocTask
        }

        return packageJavadocTask
    }

    static Jar configureSources(Project project) {
        AndroidSourceJar packageSourcesTask = project.tasks.create(SOURCES_JAR_TASK_NAME, AndroidSourceJar)
        packageSourcesTask.configure {
            description = "Package main sources into jar archive."
            sourceSet = SourceSet.MAIN_SOURCE_SET_NAME
        }

        return packageSourcesTask
    }

    static Jar configureClasses(Project project) {
        AndroidClassesJar packageClassesTask = project.tasks.create(CLASSES_JAR_TASK_NAME, AndroidClassesJar)
        packageClassesTask.configure {
            description = "Package compiled classes into jar archive."
        }

        return packageClassesTask
    }
}
