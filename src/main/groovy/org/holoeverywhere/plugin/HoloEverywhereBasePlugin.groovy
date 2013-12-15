package org.holoeverywhere.plugin

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
}
