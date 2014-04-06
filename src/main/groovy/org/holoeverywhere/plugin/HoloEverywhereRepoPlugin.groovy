package org.holoeverywhere.plugin

import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

import javax.inject.Inject

class HoloEverywhereRepoPlugin extends HoloEverywhereAbstractPlugin {
    private static final String REPO_NAME = 'holoeverywhere'
    private static final String SNAPSHOT_REPO_NAME = 'holoeverywhere-snapshot'

    @Inject
    HoloEverywhereRepoPlugin(Instantiator instantiator) {
        super(instantiator)
    }

    @Override
    void apply(Project project) {
        project.repositories.mavenCentral()

        HoloEverywhereExtension holoeverywhere = HoloEverywhereExtension.getOrCreateExtension(project, instantiator)
        // HoloEverywhere repository
        if (holoeverywhere.repository.include()) {
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
