
package org.holoeverywhere.demo;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.demo.widget.DemoFrame;
import org.holoeverywhere.demo.widget.DemoNavigationItem;
import org.holoeverywhere.demo.widget.WidgetContainer;
import org.holoeverywhere.slidingmenu.SlidingApplication;

public class DemoApplication extends SlidingApplication {
    static {
        config().setAlwaysUseParentTheme(true).setDebugMode(true);
        LayoutInflater.remap(WidgetContainer.class);
        LayoutInflater.remap(DemoFrame.class);
        LayoutInflater.remap(DemoNavigationItem.class);
        ThemeManager.setDefaultTheme(ThemeManager.MIXED);
        // Android 2.1 incorrect process FULLSCREEN flag
        // ThemeManager.modify(ThemeManager.FULLSCREEN);
        ThemeManager.map(ThemeManager.DARK,
                R.style.Holo_Demo_Theme);
        ThemeManager.map(ThemeManager.LIGHT,
                R.style.Holo_Demo_Theme_Light);
        ThemeManager.map(ThemeManager.MIXED,
                R.style.Holo_Demo_Theme_Light_DarkActionBar);
    }
}
