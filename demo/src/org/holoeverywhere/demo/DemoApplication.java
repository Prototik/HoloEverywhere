
package org.holoeverywhere.demo;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.demo.widget.DemoListRowView;
import org.holoeverywhere.demo.widget.DemoThemePicker;
import org.holoeverywhere.demo.widget.WidgetContainer;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class DemoApplication extends Application {
    static {
        HoloEverywhere.DEBUG = true;
        HoloEverywhere.PREFERENCE_IMPL = PreferenceImpl.JSON;

        LayoutInflater.remap(WidgetContainer.class);
        LayoutInflater.remap(DemoListRowView.class);
        LayoutInflater.remap(DemoThemePicker.class);

        ThemeManager.setDefaultTheme(ThemeManager.MIXED);

        // Android 2.1 incorrect process FULLSCREEN flag
        if (VERSION.SDK_INT >= VERSION_CODES.FROYO) {
            ThemeManager.modify(ThemeManager.FULLSCREEN);
        }

        ThemeManager.map(ThemeManager.DARK,
                R.style.Holo_Demo_Theme);
        ThemeManager.map(ThemeManager.LIGHT,
                R.style.Holo_Demo_Theme_Light);
        ThemeManager.map(ThemeManager.MIXED,
                R.style.Holo_Demo_Theme_Light_DarkActionBar);
        ThemeManager.map(ThemeManager.DARK | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Fullscreen);
        ThemeManager.map(ThemeManager.LIGHT | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Light_Fullscreen);
        ThemeManager.map(ThemeManager.MIXED | ThemeManager.FULLSCREEN,
                R.style.Holo_Demo_Theme_Light_DarkActionBar_Fullscreen);
    }
}
