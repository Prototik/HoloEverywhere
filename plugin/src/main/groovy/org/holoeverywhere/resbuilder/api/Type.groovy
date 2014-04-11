package org.holoeverywhere.resbuilder.api

import org.holoeverywhere.resbuilder.tasks.ResbuilderDefaultTask

interface Type {
    def String getName()

    def Object process(Map<String, ?> data, File output, Object state, File input)

    def void flush(Object state)

    def void bind(ResbuilderDefaultTask task)
}