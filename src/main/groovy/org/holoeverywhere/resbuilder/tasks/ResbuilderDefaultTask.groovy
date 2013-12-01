package org.holoeverywhere.resbuilder.tasks

import org.gradle.api.DefaultTask
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet

abstract class ResbuilderDefaultTask extends DefaultTask {
    def Set<ResbuilderSourceSet> source
}
