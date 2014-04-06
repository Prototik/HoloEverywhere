package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.holoeverywhere.resbuilder.tasks.ResbuilderFormatTask
import org.holoeverywhere.resbuilder.tasks.ResbuilderGrabTask
import org.holoeverywhere.resbuilder.tasks.ResbuilderProcesserTask

import javax.inject.Inject

class HoloEverywhereCorePlugin extends HoloEverywhereAbstractPlugin {
    private static final String APPCOMPAT_V7_GROUP = 'com.android.support'
    private static final String APPCOMPAT_V7_NAME = 'appcompat-v4'

    private HoloEverywhereExtension holoeverywhere

    @Inject
    HoloEverywhereCorePlugin(Instantiator instantiator) {
        super(instantiator)
    }

    @Override
    void apply(Project project) {
        checkPluginOrder(project)
        loadRepoPlugin(project)
        holoeverywhere = extension(project)
        project.afterEvaluate { afterEvaluate(project) }
    }

    void afterEvaluate(Project project) {
        // Android plugin
        if (!project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            project.plugins.apply(HoloEverywhereAppPlugin)
        }

        Configuration configuration = project.configurations.getByName(holoeverywhere.configuration);
        if (configuration == null) {
            throw new RuntimeException("Couldn't find configuration for HoloEverywhere library: " + holoeverywhere.defaultConfiguration)
        }

        // Support library v4
        if (holoeverywhere.supportV4.include() && holoeverywhere.supportV4.artifactOverride()) {
            project.dependencies.add(holoeverywhere.configuration, holoeverywhere.supportV4.resolveArtifactName())
        }

        // HoloEverywhere's AAR
        Dependency holoeverywhereDependency = configuration.dependencies.find { Dependency i -> i.group == holoeverywhere.library.group && i.name == holoeverywhere.library.name }
        if (holoeverywhere.library.include() && holoeverywhereDependency == null) {
            holoeverywhereDependency = holoeverywhere.aar("${holoeverywhere.library.group}:${holoeverywhere.library.name}:${holoeverywhere.library.resolveVersion(project)}")
        }

        // Addons
        holoeverywhere.addons.each { HoloEverywhereExtension.Addon addon ->
            holoeverywhere.aar("${addon.group}:${addon.name}:${addon.version ?: holoeverywhereDependency.version}")
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
                task.group = JavaBasePlugin.VERIFICATION_GROUP
                task.description = 'Reformat all resbuilder layouts for clean view'

                task = project.tasks.create("resbuilderFormatCheck", ResbuilderFormatTask)
                task.check = true
                task.source = holoeverywhere.resbuilder.sourceSets as Set
                task.group = JavaBasePlugin.VERIFICATION_GROUP
                task.description = 'Check out format of all resbuilder layouts'
            }

            // Resbuilder processer tasks
            holoeverywhere.resbuilder.sourceSets.asMap.each { String name, ResbuilderSourceSet sourceSet ->
                char[] chars = name.chars
                chars[0] = chars[0].toUpperCase()
                name = new String(chars)

                ResbuilderProcesserTask processTask = project.tasks.create("resbuilder${name}", ResbuilderProcesserTask)
                processTask.source = [sourceSet] as Set
                processTask.group = org.gradle.api.plugins.BasePlugin.BUILD_GROUP
                processTask.description = "Build resbuilder layouts in \"${name}\" source set"
                project.tasks.getByName('preBuild').dependsOn processTask

                ResbuilderGrabTask grabTask = project.tasks.create("resbuilder${name}Grab", ResbuilderGrabTask)
                grabTask.source = [sourceSet] as Set
                grabTask.group = org.gradle.api.plugins.BasePlugin.BUILD_GROUP
                grabTask.description = "Grab android resources in \"${name}\" source set"
            }
        }
    }
}
