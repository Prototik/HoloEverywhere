package org.holoeverywhere.plugin

import com.android.build.gradle.LibraryPlugin
import com.android.sdklib.IAndroidTarget
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.UnknownPluginException
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject

class HoloEverywhereLibraryPlugin extends HoloEverywhereBasePlugin {
    public static final String GENERATE_JAVADOC_TASK_NAME = 'generateJavadoc'
    public static final String JAVADOC_JAR_TASK_NAME = 'javadoc'
    public static final String SOURCES_JAR_TASK_NAME = 'sources'

    private HoloEverywhereExtension extension

    @Inject
    HoloEverywhereLibraryPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        super(instantiator, registry)
    }

    @Override
    void apply(Project project) {
        extension = extension(project)

        LibraryPlugin plugin
        try {
            plugin = project.plugins.getPlugin(LibraryPlugin)
        } catch (UnknownPluginException e) {
            plugin = project.plugins.apply(LibraryPlugin)
        }

        def List<Jar> artifacts = new ArrayList<>()
        if (extension.library.javadoc) artifacts.add(configureJavadoc(project, plugin))
        if (extension.library.sources) artifacts.add(configureSources(project, plugin))
        if (extension.library.attachArchives(project)) {
            artifacts.each { Jar packageTask -> project.artifacts.add('archives', packageTask) }
        }
    }

    static Jar configureJavadoc(Project project, LibraryPlugin plugin) {
        Javadoc generateJavadocTask = project.tasks.create(GENERATE_JAVADOC_TASK_NAME, Javadoc)
        generateJavadocTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Generates Javadoc API documentation for the main source code."

            source = plugin.extension.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            classpath = project.files(
                    // Compiled source code
                    "${project.buildDir}/classes/release",

                    // JAR dependencies
                    project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME),

                    // AAR dependencies
                    project.fileTree("${project.buildDir}/exploded-bundles") { include '*/classes.jar' },

                    // Android framework
                    plugin.loadedSdkParser.target.getPath(IAndroidTarget.ANDROID_JAR)
            )
        }

        Jar packageJavadocTask = project.tasks.create(JAVADOC_JAR_TASK_NAME, Jar)
        packageJavadocTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Package Javadoc API documentation into jar archive."
            classifier = 'javadoc'

            from generateJavadocTask.destinationDir
        }

        return packageJavadocTask
    }

    static Jar configureSources(Project project, LibraryPlugin plugin) {
        Jar packageSourcesTask = project.tasks.create(SOURCES_JAR_TASK_NAME, Jar)
        packageSourcesTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Package main sources into jar archive."
            classifier = 'sources'

            from plugin.extension.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).allSource
        }
        return packageSourcesTask
    }
}
