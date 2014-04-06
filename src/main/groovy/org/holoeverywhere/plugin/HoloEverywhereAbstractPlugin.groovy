package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

abstract class HoloEverywhereAbstractPlugin implements Plugin<Project> {
    protected final Instantiator instantiator

    public HoloEverywhereAbstractPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    public HoloEverywhereExtension extension(Project project) {
        return HoloEverywhereExtension.getOrCreateExtension(project, instantiator)
    }

    def void loadRepoPlugin(Project project) {
        project.plugins.apply(HoloEverywhereRepoPlugin)
    }

    def void loadCorePlugin(Project project) {
        project.plugins.apply(HoloEverywhereCorePlugin)
    }

    def void checkPluginOrder(Project project) {
        if (project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            throw new IllegalStateException("HoloEverywhere plugin should be applied before any android plugin, please correct your build.gradle")
        }
    }
}
