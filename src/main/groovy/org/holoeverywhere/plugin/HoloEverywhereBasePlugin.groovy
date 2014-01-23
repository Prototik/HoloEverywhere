package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject


abstract class HoloEverywhereBasePlugin implements Plugin<Project> {
    protected final Instantiator instantiator

    @Inject
    public HoloEverywhereBasePlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        this.instantiator = instantiator
    }

    public HoloEverywhereExtension extension(Project project) {
        return HoloEverywhereExtension.getOrCreateExtension(project, instantiator)
    }

    def void loadRepoPlugin(Project project) {
        project.plugins.apply(HoloEverywhereRepoPlugin)
    }

    def void loadMainPlugin(Project project) {
        project.plugins.apply(HoloEverywhereMainPlugin)
    }

    def void checkPluginOrder(Project project) {
        if (project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            throw new IllegalStateException("HoloEverywhere plugin should be applied before any android plugin, please correct your build.gradle")
        }
    }
}
