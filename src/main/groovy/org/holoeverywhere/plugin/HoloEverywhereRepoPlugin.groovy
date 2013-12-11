package org.holoeverywhere.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject

class HoloEverywhereRepoPlugin implements Plugin<Project> {
    private static final String REPO_NAME = 'holoeverywhere'
    private static final String SNAPSHOT_REPO_NAME = 'holoeverywhere-snapshot'
    private final Instantiator instantiator

    @Inject
    public HoloEverywhereRepoPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        HoloEverywhereExtension holoeverywhere = HoloEverywhereExtension.getOrCreateExtension(project, instantiator)
        // HoloEverywhere repository
        if (holoeverywhere.repository.include()) {
            project.allprojects.each { Project p ->
                if (!p.repositories.any { it.name == REPO_NAME }) {
                    p.repositories.maven {
                        name REPO_NAME
                        url holoeverywhere.repository.url
                    }
                }
            }
        }
        if (holoeverywhere.repository.snapshot()) {
            project.allprojects.each { Project p ->
                if (!p.repositories.any { it.name == SNAPSHOT_REPO_NAME }) {
                    p.repositories.maven {
                        name SNAPSHOT_REPO_NAME
                        url holoeverywhere.repository.snapshotUrl
                    }
                }
            }
        }
    }
}
