package org.holoeverywhere.plugin.extension

import org.gradle.api.Project

class LibraryContainer extends IncludeContainer {
    public static enum AttachArchives {
        Auto('auto'), Yes('yes'), No('no');

        String localName;

        AttachArchives(String localName) {
            this.localName = localName
        }

        public static AttachArchives find(String localName, AttachArchives defaultValue) {
            values().find { it.localName == localName } ?: defaultValue
        }
    }

    LibraryContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = HoloEverywhereExtension.HOLO_EVERYWHERE_GROUP
    def String name = HoloEverywhereExtension.HOLO_EVERYWHERE_NAME
    def String version = HoloEverywhereExtension.HOLO_EVERYWHERE_VERSION
    def AttachArchives attachArchives = AttachArchives.Auto
    def boolean javadoc = true
    def boolean sources = true
    def boolean applyPlugin = true

    def void attachArchives(String attachArchives) {
        this.attachArchives = AttachArchives.find(attachArchives, AttachArchives.Auto)
    }

    def boolean attachArchives(Project project) {
        switch (attachArchives) {
            case AttachArchives.Yes: return true
            case AttachArchives.No: return true
            case AttachArchives.Auto: return project.gradle.taskGraph.hasTask('uploadArchives')
        }
    }
}

