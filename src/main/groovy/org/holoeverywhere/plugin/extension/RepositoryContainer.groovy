package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable

class RepositoryContainer extends IncludeContainer implements Configurable<RepositoryContainer> {
    RepositoryContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String url = HoloEverywhereExtension.HOLO_EVERYWHERE_REPO
    def String snapshotUrl = HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO
    def Include snapshot = Include.Yes

    def void snapshot(String snapshot) {
        this.snapshot = Include.find(snapshot, Include.Yes)
    }

    def boolean snapshot() {
        return snapshot == Include.Yes
    }

    @Override
    RepositoryContainer configure(Closure closure) {
        super.configure(closure) as RepositoryContainer
    }
}
