package org.holoeverywhere.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class HoloEverywherePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.logger.warn("'holoeverywhere' plugin deprecated. Use a 'holoeverywhere-app' or `holoeverywhere-library`")
        project.plugins.apply(HoloEverywhereAppPlugin)
    }
}
