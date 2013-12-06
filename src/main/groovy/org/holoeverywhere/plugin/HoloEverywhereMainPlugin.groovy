package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.holoeverywhere.resbuilder.tasks.ResbuilderFormatTask
import org.holoeverywhere.resbuilder.tasks.ResbuilderProcesserTask

import javax.inject.Inject

class HoloEverywhereMainPlugin implements Plugin<Project> {
    private static final String APPCOMPAT_V7_GROUP = 'com.android.support'
    private static final String APPCOMPAT_V7_NAME = 'appcompat-v4'

    private final Instantiator instantiator
    private HoloEverywhereExtension holoeverywhere

    @Inject
    public HoloEverywhereMainPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        holoeverywhere = HoloEverywhereExtension.getOrCreateExtension(project, instantiator)
        project.afterEvaluate { afterEvaluate(project) }
    }

    void afterEvaluate(Project project) {
        project.plugins.apply(HoloEverywhereRepoPlugin)

        // Android plugin
        if (!project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            project.plugins.apply('android')
        }

        Configuration configuration = project.configurations.getByName(holoeverywhere.configuration);
        if (configuration == null) {
            throw new RuntimeException("Couldn't find configuration for HoloEverywhere library: " + holoeverywhere.defaultConfiguration)
        }

        // Support library v4
        if (holoeverywhere.supportV4.include() && !configuration.dependencies.any { Dependency i -> i.group == holoeverywhere.supportV4.group && i.name == holoeverywhere.supportV4.name }) {
            project.dependencies.add(holoeverywhere.configuration, "${holoeverywhere.supportV4.group}:${holoeverywhere.supportV4.name}:${holoeverywhere.supportV4.version}@jar")
        }

        // HoloEverywhere's AAR
        Dependency holoeverywhereDependency = configuration.dependencies.find { Dependency i -> i.group == holoeverywhere.library.group && i.name == holoeverywhere.library.name }
        if (holoeverywhere.library.include() && holoeverywhereDependency == null) {
            holoeverywhereDependency = project.dependencies.add(holoeverywhere.configuration, "${holoeverywhere.library.group}:${holoeverywhere.library.name}:${holoeverywhere.library.version}@aar")
        }

        // Addons
        holoeverywhere.addons.each { HoloEverywhereExtension.Addon addon ->
            project.dependencies.add(holoeverywhere.configuration, "${addon.group}:${addon.name}:${addon.version || holoeverywhereDependency.version}@aar")
        }

        // Checkout that pure AppCompat was not added to dependencies
        if (configuration.dependencies.any { Dependency i -> i.group == APPCOMPAT_V7_GROUP && i.name == APPCOMPAT_V7_NAME }) {
            project.logger.warn("AppCompat found in dependencies. HoloEverywhere already contains built-in AppCompat variation, so you not need in any other versions")
        }


        if (holoeverywhere.resbuilder.enable) {
            // Resbuilder format tasks
            if (holoeverywhere.resbuilder.formatTask) {
                ResbuilderFormatTask task = project.tasks.create("resbuilderFormat", ResbuilderFormatTask)
                task.source = holoeverywhere.resbuilder.sourceSets as Set

                task = project.tasks.create("resbuilderFormatCheck", ResbuilderFormatTask)
                task.check = true
                task.source = holoeverywhere.resbuilder.sourceSets as Set
            }

            // Resbuilder processer tasks
            holoeverywhere.resbuilder.sourceSets.asMap.each { String name, ResbuilderSourceSet sourceSet ->
                char[] chars = name.chars
                chars[0] = chars[0].toUpperCase()
                ResbuilderProcesserTask task = project.tasks.create("resbuilder${new String(chars)}", ResbuilderProcesserTask)
                task.source = [sourceSet] as Set
                project.tasks.getByName('preBuild').dependsOn task
            }
        }
    }
}
