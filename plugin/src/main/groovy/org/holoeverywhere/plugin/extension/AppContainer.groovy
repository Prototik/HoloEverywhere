package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class AppContainer implements Configurable<AppContainer> {
    AppContainer(Project project) {
        this.project = project
    }

    private final Project project
    def boolean publishReleaseApk = true

    @Override
    AppContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}

