
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

        ThemeManager.map(THEME_FLAG | ThemeManager.DARK,
                R.style.Holo_PreferenceTheme_Dark);
        ThemeManager.map(THEME_FLAG | ThemeManager.LIGHT,
                R.style.Holo_PreferenceTheme_Light);
        ThemeManager.map(THEME_FLAG | ThemeManager.MIXED,
                R.style.Holo_PreferenceTheme_Light);

        LayoutInflater.remap(PreferenceFrameLayout.class);
        LayoutInflater.remap(FragmentBreadCrumbs.class);
    }

    /**
     * Nop method for execute static code block
     */
    public static void init() {

    }

    private PreferenceInit() {
    }

}
