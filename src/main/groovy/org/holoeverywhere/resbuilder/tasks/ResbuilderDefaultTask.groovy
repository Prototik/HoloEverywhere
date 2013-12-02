package org.holoeverywhere.resbuilder.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet

abstract class ResbuilderDefaultTask extends DefaultTask {
    @Input
    def Set<ResbuilderSourceSet> source
}
