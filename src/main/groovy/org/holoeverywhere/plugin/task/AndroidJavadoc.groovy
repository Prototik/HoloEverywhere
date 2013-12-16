package org.holoeverywhere.plugin.task

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.sdklib.IAndroidTarget
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.javadoc.Javadoc

public class AndroidJavadoc extends Javadoc {
    AndroidJavadoc() {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
    }

    def String configuration
    def String sourceSet

    @Override
    @TaskAction
    protected void generate() {
        if (sourceSet != null) {
            SourceDirectorySet androidSource = project.extensions.getByType(LibraryExtension).sourceSets.getByName(sourceSet).allJava
            if (source != null) {
                source = project.files(source, androidSource)
            } else {
                source = androidSource
            }
        }
        if (configuration != null) {
            def FileCollection androidClasspath = project.files(
                    // Compiled source code
                    "${project.buildDir}/classes/release",

                    // JAR dependencies
                    project.configurations.getByName(configuration),

                    // AAR dependencies
                    project.fileTree("${project.buildDir}/exploded-bundles") { include '*/classes.jar' },

                    // Android framework
                    project.plugins.getPlugin(LibraryPlugin).loadedSdkParser.target.getPath(IAndroidTarget.ANDROID_JAR)
            )
            if (classpath != null) {
                classpath = project.files(classpath, androidClasspath)
            } else {
                classpath = androidClasspath
            }
        }
        super.generate()
    }
}
