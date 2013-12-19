package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable

class SupportV4Container extends IncludeContainer implements Configurable<SupportV4Container> {
    SupportV4Container(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.SUPPORT_V4_GROUP
    def String name = HoloEverywhereExtension.SUPPORT_V4_NAME
    def String version = HoloEverywhereExtension.SUPPORT_V4_VERSION

    @Override
    SupportV4Container configure(Closure closure) {
        super.configure(closure)
        return this
    }
}
