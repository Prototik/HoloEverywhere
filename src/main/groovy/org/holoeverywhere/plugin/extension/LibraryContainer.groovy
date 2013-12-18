package org.holoeverywhere.plugin.extension

class LibraryContainer extends IncludeContainer {
    LibraryContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.HOLO_EVERYWHERE_GROUP
    def String name = HoloEverywhereExtension.HOLO_EVERYWHERE_NAME
    def String version = HoloEverywhereExtension.HOLO_EVERYWHERE_VERSION
    def boolean javadoc = true
    def boolean sources = true
    def boolean classes = true
    def boolean applyPlugin = true
}

