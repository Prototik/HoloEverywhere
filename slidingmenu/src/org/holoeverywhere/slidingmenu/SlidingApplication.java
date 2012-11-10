
package org.holoeverywhere.slidingmenu;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Application;

public class SlidingApplication extends Application {
    static {
        LayoutInflater.remap(SlidingMenu.class);
    }
}
