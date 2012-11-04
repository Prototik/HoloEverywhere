
package org.holoeverywhere.demo;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.demo.widget.OtherButton;
import org.holoeverywhere.demo.widget.WidgetContainer;

public class DemoApplication extends Application {
    static {
        getConfig().setUseThemeManager(true).setAlwaysUseParentTheme(true)
                .setDebugMode(true);
        LayoutInflater.remap(WidgetContainer.class);
        LayoutInflater.remap(OtherButton.class);
        ThemeManager.modify(ThemeManager.FULLSCREEN);
    }
}
