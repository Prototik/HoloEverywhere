
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
        ThemeManager.modify(ThemeManager.FULLSCREEN);
        ThemeManager.map(ThemeManager.DARK | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Fullscreen);
        ThemeManager.map(ThemeManager.LIGHT | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Light_Fullscreen);
        ThemeManager.map(ThemeManager.MIXED | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Light_DarkActionBar_Fullscreen);
    }
}
