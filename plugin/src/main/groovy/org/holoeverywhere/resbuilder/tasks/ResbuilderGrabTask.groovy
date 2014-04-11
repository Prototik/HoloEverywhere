package org.holoeverywhere.resbuilder.tasks

import org.holoeverywhere.resbuilder.types.TypeGrab

class ResbuilderGrabTask extends ResbuilderBaseProcesserTask {
    ResbuilderGrabTask() {
        types.add(new TypeGrab())
    }
}
