package org.holoeverywhere.plugin

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension


public class HoloEverywhereJarLibraryPlugin extends HoloEverywhereAbstractPlugin {
    private HoloEverywhereExtension extension

    public HoloEverywhereJarLibraryPlugin(Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        super(instantiator, repositoryFactory)
    }

    @Override
    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        checkPluginOrder(project)

        project.afterEvaluate { afterEvaluate(project) }

        extension = extension(project)
    }

    void afterEvaluate(Project project) {

    }
}
