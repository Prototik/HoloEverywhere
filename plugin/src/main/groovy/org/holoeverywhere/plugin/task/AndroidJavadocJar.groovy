package org.holoeverywhere.plugin.task

import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.bundling.Jar

public class AndroidJavadocJar extends Jar {
    AndroidJavadocJar() {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        classifier = 'javadoc'
    }

    public void extendsFrom(AndroidJavadoc javadocTask) {
        dependsOn(javadocTask)
        from(javadocTask.destinationDir)
    }
}
