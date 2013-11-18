package org.holoeverywhere.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class HoloEverywherePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.add('holoeverywhere', HoloEverywhereExtension)
    }
}
