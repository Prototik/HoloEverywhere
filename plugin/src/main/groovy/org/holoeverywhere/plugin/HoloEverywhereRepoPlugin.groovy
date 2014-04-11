package org.holoeverywhere.plugin

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject

public class HoloEverywhereRepoPlugin extends HoloEverywhereAbstractPlugin {
    private static final String REPO_NAME = 'holoeverywhere'
    private static final String SNAPSHOT_REPO_NAME = 'holoeverywhere-snapshot'

    @Inject
    HoloEverywhereRepoPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        super(instantiator, repositoryFactory)
    }

    @Override
    void apply(Project project) {
        HoloEverywhereExtension holoeverywhere = extension(project)

        // HoloEverywhere repository
        if (holoeverywhere.repository.include()) {
            project.repositories.mavenCentral()

            final boolean snapshot = holoeverywhere.repository.snapshot();
            project.rootProject.allprojects.each { Project p ->
                if (!p.repositories.any { it.name == REPO_NAME }) {
                    p.repositories.maven {
                        name REPO_NAME
                        url holoeverywhere.repository.url
                    }
                }
                if (snapshot && !p.repositories.any { it.name == SNAPSHOT_REPO_NAME }) {
                    p.repositories.maven {
                        name SNAPSHOT_REPO_NAME
                        url holoeverywhere.repository.snapshotUrl
                    }
                }
            }
        }
    }
}
