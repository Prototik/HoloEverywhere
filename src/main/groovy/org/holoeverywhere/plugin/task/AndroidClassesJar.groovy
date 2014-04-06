package org.holoeverywhere.plugin.task

import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.bundling.Jar

public class AndroidClassesJar extends Jar {
    AndroidClassesJar() {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        from("${project.buildDir}/classes/release")
        // exclude '**/R.class'
        // exclude '**/R$*.class'
        exclude '**/BuildConfig.class'
    }
}
