package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

class HoloEverywherePostPlugin implements Plugin<Project> {
    private static final String APPCOMPAT_V7_GROUP = 'com.android.support'
    private static final String APPCOMPAT_V7_NAME = 'appcompat-v4'

    @Override
    void apply(Project project) {
        final HoloEverywhereExtension holoeverywhere = project.holoeverywhere;

        // HoloEverywhere repository
        if (holoeverywhere.repository.include) {
            project.repositories.maven {
                name 'holoeverywhere'
                url holoeverywhere.repository.url
            }
        }

        // Android plugin
        if (!project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            project.plugins.apply('android')
        }

        Configuration configuration = project.configurations.getByName(holoeverywhere.configuration);
        if (configuration == null) {
            throw new RuntimeException("Couldn't find configuration for HoloEverywhere library: " + holoeverywhere.defaultConfiguration)
        }

        // Support library v4
        if (holoeverywhere.supportV4.include && !configuration.dependencies.any { Dependency i -> i.group == holoeverywhere.supportV4.group && i.name == holoeverywhere.supportV4.name }) {
            project.dependencies.add(holoeverywhere.configuration, "${holoeverywhere.supportV4.group}:${holoeverywhere.supportV4.name}:${holoeverywhere.supportV4.version}@jar")
        }

        // HoloEverywhere's AAR
        Dependency holoeverywhereDependency = configuration.dependencies.find { Dependency i -> i.group == holoeverywhere.library.group && i.name == holoeverywhere.library.name }
        if (holoeverywhere.library.include && holoeverywhereDependency == null) {
            holoeverywhereDependency = project.dependencies.add(holoeverywhere.configuration, "${holoeverywhere.library.group}:${holoeverywhere.library.name}:${holoeverywhere.library.version}@aar")
        }

        // Addons
        holoeverywhere.addons.each { HoloEverywhereExtension.Addon addon ->
            project.dependencies.add(holoeverywhere.configuration, "${addon.group}:${addon.name}:${holoeverywhereDependency.version}@aar")
        }

        project.afterEvaluate {
            // Checkout that pure AppCompat was not added to dependencies
            if (configuration.dependencies.any { Dependency i -> i.group == APPCOMPAT_V7_GROUP && i.name == APPCOMPAT_V7_NAME }) {
                project.logger.warn("AppCompat found in dependencies. HoloEverywhere already contains built-in AppCompat variation, so you not need in any other versions")
            }
        }
    }
}
