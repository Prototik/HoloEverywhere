package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable

class LibraryContainer extends IncludeContainer implements Configurable<LibraryContainer> {
    LibraryContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.HOLO_EVERYWHERE_GROUP
    def String name = HoloEverywhereExtension.HOLO_EVERYWHERE_NAME
    def String version = HoloEverywhereExtension.HOLO_EVERYWHERE_VERSION
    def boolean javadoc = true
    def boolean sources = true
    def boolean classes = true
    def boolean apklibExternalCreation = false

    @Override
    LibraryContainer configure(Closure closure) {
        super.configure(closure) as LibraryContainer
    }
}

