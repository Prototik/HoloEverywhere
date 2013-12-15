package org.holoeverywhere.plugin.extension

class SupportV4Container extends IncludeContainer {
    SupportV4Container(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.SUPPORT_V4_GROUP
    def String name = HoloEverywhereExtension.SUPPORT_V4_NAME
    def String version = HoloEverywhereExtension.SUPPORT_V4_VERSION
}
