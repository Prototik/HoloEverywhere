
package org.holoeverywhere.demo;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class DemoApplication extends Application {
    private static final String PACKAGE = DemoApplication.class.getPackage().getName();
    static {
        HoloEverywhere.DEBUG = true;
        HoloEverywhere.PREFERENCE_IMPL = PreferenceImpl.JSON;

        LayoutInflater.registerPackage(PACKAGE + ".widget");

        ThemeManager.setDefaultTheme(ThemeManager.MIXED);

        // Android 2.* incorrect process FULLSCREEN flag when we are modify
        // DecorView of Window. This hack using HoloEverywhere Slider
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
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
