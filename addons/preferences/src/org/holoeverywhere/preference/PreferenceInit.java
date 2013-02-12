
package org.holoeverywhere.preference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;

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

    /**
     * Remap all Preference themes
     */
    public static void map(int theme) {
        map(theme, theme, theme);
    }

    /**
     * Remap PreferenceThemes, splited by dark and light color scheme. For mixed
     * color scheme will be using light theme
     */
    public static void map(int darkTheme, int lightTheme) {
        map(darkTheme, lightTheme, lightTheme);
    }

    /**
     * Remap PreferenceThemes, splited by color scheme
     */
    public static void map(int darkTheme, int lightTheme, int mixedTheme) {
        if (darkTheme > 0) {
            ThemeManager.map(THEME_FLAG | ThemeManager.DARK, darkTheme);
        }
        if (lightTheme > 0) {
            ThemeManager.map(THEME_FLAG | ThemeManager.LIGHT, lightTheme);
        }
        if (mixedTheme > 0) {
            ThemeManager.map(THEME_FLAG | ThemeManager.MIXED, mixedTheme);
        }
    }

    private PreferenceInit() {
    }

}
