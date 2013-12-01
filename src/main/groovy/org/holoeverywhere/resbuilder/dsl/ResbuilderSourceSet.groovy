package org.holoeverywhere.resbuilder.dsl

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetOutput

/**
 * Created with IntelliJ IDEA.
 * User: prok
 * Date: 11/23/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
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
