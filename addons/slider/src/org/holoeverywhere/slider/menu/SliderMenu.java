
package org.holoeverywhere.slider.menu;

import org.holoeverywhere.addon.IAddonThemes;
import org.holoeverywhere.slider.R;

import android.content.Context;

public class SliderMenu {
    private SliderMenu() {
    }

    public static final int THEME_FLAG;
    public static final String PACKAGE;
    private static final IAddonThemes sThemes;
    static {
        PACKAGE = SliderMenu.class.getPackage().getName();

        sThemes = new IAddonThemes();
        THEME_FLAG = sThemes.getThemeFlag();
        map(R.style.Holo_Theme_SliderMenu, R.style.Holo_Theme_SliderMenu_Light);
    }

    /**
     * Nop method for execute static code block
     */
    public static void init() {

    }

    /**
     * Remap all SliderMenu themes
     */
    public static void map(int theme) {
        map(theme, theme, theme);
    }

    /**
     * Remap SliderMenu themes, splited by dark and light color scheme. For
     * mixed color scheme will be using light theme
     */
    public static void map(int darkTheme, int lightTheme) {
        map(darkTheme, lightTheme, lightTheme);
    }

    /**
     * Remap SliderMenu themes, splited by color scheme
     */
    public static void map(int darkTheme, int lightTheme, int mixedTheme) {
        sThemes.map(darkTheme, lightTheme, mixedTheme);
    }

    public static Context context(Context context) {
        return sThemes.context(context);
    }
}
