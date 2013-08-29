
package org.holoeverywhere.preference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.IAddonThemes;
import org.holoeverywhere.addon.IAddonThemes.ThemeResolver;
import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

public class PreferenceInit {
    public static final String PACKAGE;
    private static final ThemeResolver sThemeResolver = new ThemeResolver() {
        @Override
        public int resolveThemeForContext(Context context, int invalidTheme) {
            int theme, mod = 0;
            TypedValue outValue = new TypedValue();
            TypedArray a;
            (a = context.obtainStyledAttributes(new int[] {
                    R.attr.preferenceTheme
            })).getValue(0, outValue);
            a.recycle();
            switch (outValue.type) {
                case TypedValue.TYPE_REFERENCE:
                    if (new ContextThemeWrapper(context, theme = outValue.resourceId)
                            .obtainStyledAttributes(new int[] {
                                    R.attr.holoTheme
                            }).getInt(0, 0) == 4) {
                        return theme;
                    }
                    break;
                case TypedValue.TYPE_INT_DEC:
                case TypedValue.TYPE_INT_HEX:
                    mod = outValue.resourceId;
                    break;
            }
            theme = THEME_FLAG;
            if (context instanceof Activity) {
                if (mod == 0 || mod == ThemeManager.getDefaultTheme()) {
                    mod = ThemeManager.getThemeType(context);
                    if (mod == PreferenceInit.THEME_FLAG) {
                        theme = mod;
                        mod = 0;
                    } else if (mod == ThemeManager.INVALID) {
                        mod = ThemeManager.getDefaultTheme() & ThemeManager.COLOR_SCHEME_MASK;
                        if (mod == 0) {
                            mod = ThemeManager.DARK;
                        }
                    }
                }
                if (mod > 0) {
                    theme |= mod & ThemeManager.COLOR_SCHEME_MASK;
                }
            } else {
                theme |= ThemeManager.getDefaultTheme() & ThemeManager.COLOR_SCHEME_MASK;
            }
            theme = ThemeManager.getThemeResource(theme, false);
            if (theme == ThemeManager.getDefaultTheme() || theme == 0) {
                theme = sThemes.getDarkTheme();
            }
            return theme;
        }
    };
    private static final IAddonThemes sThemes;
    public static final int THEME_FLAG;

    static {
        PACKAGE = PreferenceInit.class.getPackage().getName();

        sThemes = new IAddonThemes();
        THEME_FLAG = sThemes.getThemeFlag();

        LayoutInflater.register(PreferenceFrameLayout.class);
        LayoutInflater.register(FragmentBreadCrumbs.class);

        map(R.style.Holo_Internal_Preference, R.style.Holo_Internal_Preference_Light);
    }

    public static Context context(Context context) {
        return sThemes.context(context, ThemeManager.DARK, sThemeResolver);
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

    private PreferenceInit() {
    }
}
