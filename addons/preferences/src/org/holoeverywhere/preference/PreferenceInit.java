
package org.holoeverywhere.preference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.widget.FragmentBreadCrumbs;

public class PreferenceInit {
    public static final String PACKAGE;
    public static final int THEME_FLAG;

    static {
        PACKAGE = PreferenceInit.class.getPackage().getName();
        THEME_FLAG = ThemeManager.makeNewFlag();

        LayoutInflater.remap(PreferenceFrameLayout.class);
        LayoutInflater.remap(FragmentBreadCrumbs.class);

        map(R.style.Holo_PreferenceTheme, R.style.Holo_PreferenceTheme_Light);
    }

    /**
     * Nop method for execute static code block
     */
    public static void init() {

    }

    public static void map(int darkTheme, int lightTheme) {
        if (darkTheme > 0) {
            ThemeManager.map(THEME_FLAG | ThemeManager.DARK, darkTheme);
        }
        if (lightTheme > 0) {
            ThemeManager.map(THEME_FLAG | ThemeManager.LIGHT, lightTheme);
            ThemeManager.map(THEME_FLAG | ThemeManager.MIXED, lightTheme);
        }
    }

    private PreferenceInit() {
    }

}
