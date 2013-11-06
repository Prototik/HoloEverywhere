
package org.holoeverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.IAddonThemes;
import org.holoeverywhere.addon.IAddonThemes.ThemeResolver;

public class PreferenceInit {
    public static final String PACKAGE;
    public static final int THEME_FLAG;
    private static final IAddonThemes sThemes;

    static {
        PACKAGE = PreferenceInit.class.getPackage().getName();

        sThemes = new IAddonThemes();
        THEME_FLAG = sThemes.getThemeFlag();

        LayoutInflater.register(PreferenceFrameLayout.class);
        LayoutInflater.register(FragmentBreadCrumbs.class);

        map(R.style.Holo_Internal_Preference, R.style.Holo_Internal_Preference_Light);
    }

    private static final ThemeResolver sThemeResolver = new ThemeResolver() {
        @Override
        public int resolveThemeForContext(Context context, int invalidTheme) {
            TypedArray a;
            int preferenceTheme = (a = context.obtainStyledAttributes(new int[]{
                    R.attr.preferenceTheme
            })).getResourceId(0, 0);
            a.recycle();
            if (preferenceTheme != 0) {
                return preferenceTheme;
            }
            return ThemeManager.getThemeResource(ThemeManager.getThemeType(context) | THEME_FLAG, false);
        }
    };

    private PreferenceInit() {
    }

    public static Context context(Context context) {
        return sThemes.context(context, 0, sThemeResolver);
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
        sThemes.map(darkTheme, lightTheme, mixedTheme);
    }

    public static Context unwrap(Context context) {
        return sThemes.unwrap(context);
    }
}
