package org.holoeverywhere.resbuilder.dsl

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetOutput

public interface ResbuilderSourceSet {
    /**
     * Name for this source set
     */
    String getName()
    /**
     * Dirs for processing
     */
    SourceDirectorySet getResources()

    /**
     * Configure resources set
     */
    ResbuilderSourceSet resources(Closure<?> closure)

    /**
     * Configure resources set with given pattern
     */
    ResbuilderSourceSet resources(String pattern)

    /**
     * Output for this set
     */
    SourceSetOutput getOutput()
}
