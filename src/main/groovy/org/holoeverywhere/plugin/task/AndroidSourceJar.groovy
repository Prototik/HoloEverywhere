package org.holoeverywhere.plugin.task

import com.android.build.gradle.LibraryExtension
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar

public class AndroidSourceJar extends Jar {
    AndroidSourceJar() {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        classifier = 'source'
    }

    def String sourceSet

    @Override
    @TaskAction
    protected void copy() {
        if (sourceSet != null) {
            from(project.extensions.getByType(LibraryExtension).sourceSets.getByName(sourceSet).allSource)
        }
        super.copy()
    }
}
