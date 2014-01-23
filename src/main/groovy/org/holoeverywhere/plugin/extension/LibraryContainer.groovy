package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.holoeverywhere.plugin.util.VersionHelper

class LibraryContainer extends IncludeContainer implements Configurable<LibraryContainer> {
    LibraryContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.HOLO_EVERYWHERE_GROUP
    def String name = HoloEverywhereExtension.HOLO_EVERYWHERE_NAME
    def String version = 'latest'
    def boolean javadoc = true
    def boolean sources = true
    def boolean classes = true
    def boolean apklibExternalCreation = false

    @Override
    LibraryContainer configure(Closure closure) {
        super.configure(closure) as LibraryContainer
    }

    public String resolveVersion(Project project) {
        if (version == 'snapshot') {
            return VersionHelper.get(project).resolveVersion("${group}:${name}", VersionHelper.VersionType.Snapshot)
        }
        if (version == 'latest') {
            return VersionHelper.get(project).resolveVersion("${group}:${name}", VersionHelper.VersionType.Stable)
        }
        return version
    }
}

