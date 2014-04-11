package org.holoeverywhere.resbuilder.tasks

import org.holoeverywhere.resbuilder.types.TypeAttrs
import org.holoeverywhere.resbuilder.types.TypeStrings
import org.holoeverywhere.resbuilder.types.TypeStyles

class ResbuilderProcesserTask extends ResbuilderBaseProcesserTask {
    ResbuilderProcesserTask() {
        types.add(new TypeAttrs())
        types.add(new TypeStyles())
        types.add(new TypeStrings())
    }
}
