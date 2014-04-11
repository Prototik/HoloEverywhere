package org.holoeverywhere.plugin

import com.android.build.gradle.BasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

abstract class HoloEverywhereAbstractPlugin implements Plugin<Project> {
    protected final Instantiator instantiator
    protected final BaseRepositoryFactory repositoryFactory

    public HoloEverywhereAbstractPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        this.instantiator = instantiator
        this.repositoryFactory = repositoryFactory
    }

    public HoloEverywhereExtension extension(Project project) {
        return HoloEverywhereExtension.getOrCreateExtension(project, instantiator, repositoryFactory)
    }

    public static void loadRepoPlugin(Project project) {
        project.plugins.apply(HoloEverywhereRepoPlugin)
    }

    public static void loadCorePlugin(Project project) {
        project.plugins.apply(HoloEverywhereCorePlugin)
    }

    public static void checkPluginOrder(Project project) {
        if (project.plugins.any { Plugin i -> BasePlugin.class.isAssignableFrom(i.class) }) {
            throw new IllegalStateException("HoloEverywhere plugin should be applied before any android plugin, please correct your build.gradle")
        }
    }

    public static void publish(HoloEverywhereExtension extension, Task task, boolean enable) {
        if ((task.enabled = enable)) {
            extension.publish.artifact(task)
        }
    }

    public static void publish(HoloEverywhereExtension extension, Task task) {
        extension.publish.artifact(task)
    }

    public static void publish(HoloEverywhereExtension extension, PublishArtifact artifact) {
        extension.publish.artifact(artifact)
    }
}
