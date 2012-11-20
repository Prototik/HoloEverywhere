
package org.holoeverywhere;

import static org.holoeverywhere.R.style.Holo_Theme;
import static org.holoeverywhere.R.style.Holo_Theme_Fullscreen;
import static org.holoeverywhere.R.style.Holo_Theme_Light;
import static org.holoeverywhere.R.style.Holo_Theme_Light_DarkActionBar;
import static org.holoeverywhere.R.style.Holo_Theme_Light_DarkActionBar_Fullscreen;
import static org.holoeverywhere.R.style.Holo_Theme_Light_DarkActionBar_NoActionBar;
import static org.holoeverywhere.R.style.Holo_Theme_Light_DarkActionBar_NoActionBar_Fullscreen;
import static org.holoeverywhere.R.style.Holo_Theme_Light_Fullscreen;
import static org.holoeverywhere.R.style.Holo_Theme_Light_NoActionBar;
import static org.holoeverywhere.R.style.Holo_Theme_Light_NoActionBar_Fullscreen;
import static org.holoeverywhere.R.style.Holo_Theme_NoActionBar;
import static org.holoeverywhere.R.style.Holo_Theme_NoActionBar_Fullscreen;

import org.holoeverywhere.ThemeManager.ThemeGetter.ThemeTag;
import org.holoeverywhere.app.Application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.SparseIntArray;

/**
 * ThemeManager for using different themes in activity, dialogs, etc. <br />
 * It uses the principle of binary flags. For example, you can get a dark theme
 * on the fullscreen mixing the two flags:<br />
 * {@link ThemeManager#DARK} | {@link ThemeManager#FULLSCREEN} <br />
 * <br />
 * Default themes map for different flags:
 * <table>
 * <th>
 * <td>{@link #DARK}</td>
 * <td>{@link #LIGHT}</td>
 * <td>{@link #MIXED}</td></th>
 * <tr>
 * <td>no other flags</td>
 * <td>{@link R.style#Holo_Theme}</td>
 * <td>{@link R.style#Holo_Theme_Light}</td>
 * <td>{@link R.style#Holo_Theme_Light_DarkActionBar}</td>
 * </tr>
 * <tr>
 * <td>{@link #FULLSCREEN}</td>
 * <td>{@link R.style#Holo_Theme_Fullscreen}</td>
 * <td>{@link R.style#Holo_Theme_Light_Fullscreen}</td>
 * <td>{@link R.style#Holo_Theme_Light_DarkActionBar_Fullscreen}</td>
 * </tr>
 * <tr>
 * <td>{@link #NO_ACTION_BAR}</td>
 * <td>{@link R.style#Holo_Theme_NoActionBar}</td>
 * <td>{@link R.style#Holo_Theme_Light_NoActionBar}</td>
 * <td>{@link R.style#Holo_Theme_Light_DarkActionBar_NoActionBar}</td>
 * </tr>
 * <tr>
 * <td>{@link #NO_ACTION_BAR} | {@link #FULLSCREEN}</td>
 * <td>{@link R.style#Holo_Theme_NoActionBar_Fullscreen}</td>
 * <td>{@link R.style#Holo_Theme_Light_NoActionBar_Fullscreen}</td>
 * <td>{@link R.style#Holo_Theme_Light_DarkActionBar_NoActionBar_Fullscreen}</td>
 * </tr>
 * </table>
 * <br />
 * You may remap themes for certain flags with method {@link #map(int, int)}
 * where first arg - flags, second - theme resource <br />
 * <br />
 * Part of HoloEverywhere
 * 
 * @author prok (prototypegamez@gmail.com)
 */
public final class ThemeManager {
    /**
     * System interface for calling super.startActivity in the activities.
     */
    public static interface SuperStartActivity {
        public void superStartActivity(Intent intent, int requestCode,
                Bundle options);
    }

    /**
     * Theme getter. This class should return theme resource for specify
     * {@link ThemeGetter.ThemeTag} object. If under the right ThemeTag no have
     * theme, return a negative number or zero. <br />
     * <br />
     * Example:
     * 
     * <pre>
     * ThemeGetter getter = new ThemeGetter() {
     * 	public int getThemeResource(ThemeTag themeTag) {
     * 		if(themeTag.fullscreen) { // theme has {@link ThemeManager#FULLSCREEN} flag
     * 			return R.style.CustomThemeWithFullscreenFlag;
     * 		}
     * 		return 0; // default behavior
     * 	}
     * }
     * </pre>
     */
    public static interface ThemeGetter {
        /**
         * Class-container for theme flags.
         */
        public static final class ThemeTag {
            public final boolean dark, fullscreen, light, mixed, noActionBar;
            public final int flags;

            private ThemeTag(int flags) {
                this.flags = flags;
                dark = isDark(flags);
                light = isLight(flags);
                mixed = isMixed(flags);
                noActionBar = isNoActionBar(flags);
                fullscreen = isFullScreen(flags);
            }
        }

        public int getThemeResource(ThemeTag themeTag);
    }

    private static int _DEFAULT_THEME = ThemeManager.DARK;
    private static final int _START_RESOURCES_ID = 0x01000000;
    private static ThemeGetter _THEME_GETTER;
    private static final int _THEME_MASK = ThemeManager.DARK
            | ThemeManager.LIGHT | ThemeManager.MIXED
            | ThemeManager.NO_ACTION_BAR | ThemeManager.FULLSCREEN;
    private static int _THEME_MODIFIER = 0;
    private static final String _THEME_TAG = "holoeverywhere:theme";
    private static final SparseIntArray _THEMES_MAP = new SparseIntArray();
    /**
     * Flag indicates on the dark theme
     */
    public static final int DARK = 1;
    /**
     * Flag indicates on the fullscreen theme
     */
    public static final int FULLSCREEN = 16;
    /**
     * Flag indicates on the light theme. If you want light theme with dark
     * action bar, use {@link #MIXED} flag
     */
    public static final int LIGHT = 2;
    /**
     * @deprecated Use {@link #MIXED} instead
     */
    @Deprecated
    public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
    /**
     * Flag indicates on the light theme with dark action bar
     */
    public static final int MIXED = 4;
    /**
     * Flag indicates on the theme without action bar by default (may be show
     * later)
     */
    public static final int NO_ACTION_BAR = 8;

    /**
     * Map default themes
     */
    static {
        map(DARK, Holo_Theme);
        map(DARK | FULLSCREEN, Holo_Theme_Fullscreen);
        map(DARK | NO_ACTION_BAR, Holo_Theme_NoActionBar);
        map(DARK | NO_ACTION_BAR | FULLSCREEN,
                Holo_Theme_NoActionBar_Fullscreen);
        map(LIGHT, Holo_Theme_Light);
        map(LIGHT | FULLSCREEN, Holo_Theme_Light_Fullscreen);
        map(LIGHT | NO_ACTION_BAR, Holo_Theme_Light_NoActionBar);
        map(LIGHT | NO_ACTION_BAR | FULLSCREEN,
                Holo_Theme_Light_NoActionBar_Fullscreen);
        map(MIXED, Holo_Theme_Light_DarkActionBar);
        map(MIXED | FULLSCREEN, Holo_Theme_Light_DarkActionBar_Fullscreen);
        map(MIXED | NO_ACTION_BAR, Holo_Theme_Light_DarkActionBar_NoActionBar);
        map(MIXED | NO_ACTION_BAR | FULLSCREEN,
                Holo_Theme_Light_DarkActionBar_NoActionBar_Fullscreen);
    }

    /**
     * Apply theme from intent. Only system use, don't call it!
     */
    public static void applyTheme(Activity activity) {
        boolean force = activity instanceof IHoloActivity ? ((IHoloActivity) activity)
                .isForceThemeApply() : false;
        ThemeManager.applyTheme(activity, force);
    }

    /**
     * Apply theme from intent. Only system use, don't call it!
     */
    public static void applyTheme(Activity activity, boolean force) {
        if (force || ThemeManager.hasSpecifiedTheme(activity)) {
            activity.setTheme(ThemeManager.getThemeResource(activity));
        }
    }

    /**
     * Synonym for {@link #cloneTheme(Intent, Intent, boolean)} with third arg -
     * false
     * 
     * @see #cloneTheme(Intent, Intent, boolean)
     */
    public static void cloneTheme(Intent sourceIntent, Intent intent) {
        ThemeManager.cloneTheme(sourceIntent, intent, false);
    }

    /**
     * Clone theme from sourceIntent to intent, if it specified for sourceIntent
     * or set flag force
     * 
     * @param sourceIntent Intent with specified {@link #_THEME_TAG}
     * @param intent Intent into which will be put a theme
     * @param force Clone theme even if sourceIntent not contain
     *            {@link #_THEME_TAG}
     */
    public static void cloneTheme(Intent sourceIntent, Intent intent,
            boolean force) {
        final boolean hasSourceTheme = hasSpecifiedTheme(sourceIntent);
        if (force || hasSourceTheme) {
            intent.putExtra(_THEME_TAG, hasSourceTheme ? getTheme(sourceIntent)
                    : _DEFAULT_THEME);
        }
    }

    /**
     * @return Default theme, which will be using if theme not specified for
     *         intent
     * @see #setDefaultTheme(int)
     * @see #modifyDefaultTheme(int)
     * @see #modifyDefaultThemeClear(int)
     */
    public static int getDefaultTheme() {
        return _DEFAULT_THEME;
    }

    /**
     * @return Modifier, which applying on all themes.
     * @see #modify(int)
     * @see #setModifier(int)
     */
    public static int getModifier() {
        return _THEME_MODIFIER;
    }

    /**
     * Extract theme flags from activity intent
     */
    public static int getTheme(Activity activity) {
        return getTheme(activity.getIntent());
    }

    /**
     * Extract theme flags from intent
     */
    public static int getTheme(Intent intent) {
        return prepareFlags(intent.getIntExtra(ThemeManager._THEME_TAG,
                ThemeManager._DEFAULT_THEME));
    }

    /**
     * Resolve theme resource id by flags from activity intent
     */
    public static int getThemeResource(Activity activity) {
        return getThemeResource(getTheme(activity));
    }

    /**
     * Resolve theme resource id by flags
     */
    public static int getThemeResource(int themeTag) {
        themeTag = prepareFlags(themeTag);
        if (themeTag >= _START_RESOURCES_ID) {
            return themeTag;
        }
        if (ThemeManager._THEME_GETTER != null) {
            final int getterResource = ThemeManager._THEME_GETTER
                    .getThemeResource(new ThemeTag(themeTag));
            if (getterResource > 0) {
                return getterResource;
            }
        }
        final int i = _THEMES_MAP.get(themeTag, _DEFAULT_THEME);
        if (i == _DEFAULT_THEME) {
            return _THEMES_MAP.get(_DEFAULT_THEME, R.style.Holo_Theme);
        } else {
            return i;
        }
    }

    /**
     * Resolve theme resource id by flags from intent
     */
    public static int getThemeResource(Intent intent) {
        return getThemeResource(getTheme(intent));
    }

    /**
     * @return true if activity has specified theme in intent
     */
    public static boolean hasSpecifiedTheme(Activity activity) {
        return activity == null ? false : ThemeManager
                .hasSpecifiedTheme(activity.getIntent());
    }

    /**
     * @return true if intent has specified theme
     */
    public static boolean hasSpecifiedTheme(Intent intent) {
        return intent != null && intent.hasExtra(ThemeManager._THEME_TAG)
                && intent.getIntExtra(ThemeManager._THEME_TAG, 0) > 0;
    }

    private static boolean is(int config, int key) {
        return (config & key) != 0;
    }

    public static boolean isDark(Activity activity) {
        return ThemeManager.isDark(ThemeManager.getTheme(activity));
    }

    public static boolean isDark(int i) {
        return ThemeManager.is(i, ThemeManager.DARK);
    }

    public static boolean isDark(Intent intent) {
        return ThemeManager.isDark(ThemeManager.getTheme(intent));
    }

    public static boolean isFullScreen(Activity activity) {
        return ThemeManager.isFullScreen(ThemeManager.getTheme(activity));
    }

    public static boolean isFullScreen(int i) {
        return ThemeManager.is(i, ThemeManager.FULLSCREEN);
    }

    public static boolean isFullScreen(Intent intent) {
        return ThemeManager.isFullScreen(ThemeManager.getTheme(intent));
    }

    public static boolean isLight(Activity activity) {
        return ThemeManager.isLight(ThemeManager.getTheme(activity));
    }

    public static boolean isLight(int i) {
        return ThemeManager.is(i, ThemeManager.LIGHT);
    }

    public static boolean isLight(Intent intent) {
        return ThemeManager.isLight(ThemeManager.getTheme(intent));
    }

    /**
     * @deprecated Use {@link #isMixed(Activity)} instead
     */
    @Deprecated
    public static boolean isLightWithDarkActionBar(Activity activity) {
        return isMixed(activity);
    }

    /**
     * @deprecated Use {@link #isMixed(int)} instead
     */
    @Deprecated
    public static boolean isLightWithDarkActionBar(int i) {
        return isMixed(i);
    }

    /**
     * @deprecated Use {@link #isMixed(Intent)} instead
     */
    @Deprecated
    public static boolean isLightWithDarkActionBar(Intent intent) {
        return isMixed(intent);
    }

    public static boolean isMixed(Activity activity) {
        return ThemeManager.isMixed(ThemeManager.getTheme(activity));
    }

    public static boolean isMixed(int i) {
        return ThemeManager.is(i, ThemeManager.MIXED);
    }

    public static boolean isMixed(Intent intent) {
        return ThemeManager.isMixed(ThemeManager.getTheme(intent));
    }

    public static boolean isNoActionBar(Activity activity) {
        return ThemeManager.isNoActionBar(ThemeManager.getTheme(activity));
    }

    public static boolean isNoActionBar(int i) {
        return ThemeManager.is(i, ThemeManager.NO_ACTION_BAR);
    }

    public static boolean isNoActionBar(Intent intent) {
        return ThemeManager.isNoActionBar(ThemeManager.getTheme(intent));
    }

    /**
     * Remap default theme.
     * 
     * @see #map(int, int)
     */
    public static void map(int theme) {
        map(_DEFAULT_THEME, theme);
    }

    /**
     * Remap themes. <br />
     * <br />
     * Example, you can remap {@link #LIGHT} theme on
     * {@link R.style#Holo_Theme_Dialog_Light}:<br />
     * 
     * <pre>
     * ThemeManager.map({@link #LIGHT}, {@link R.style#Holo_Theme_Dialog_Light});
     * </pre>
     */
    public static void map(int flags, int theme) {
        _THEMES_MAP.put(flags & _THEME_MASK, theme);
    }

    /**
     * Add modifier to all themes, using in {@link ThemeManager}. If you call
     * modify({@link #NO_ACTION_BAR}), then all themes will be without action
     * bar by default, regardless of the flag is passed.
     * 
     * @see #modifyClear(int)
     * @see #modifyClear()
     * @see #setModifier(int)
     */
    public static void modify(int mod) {
        ThemeManager._THEME_MODIFIER |= mod & ThemeManager._THEME_MASK;
    }

    /**
     * Clear all modifiers
     * 
     * @see #modify(int)
     * @see #modifyClear(int)
     * @see #setModifier(int)
     */
    public static void modifyClear() {
        ThemeManager._THEME_MODIFIER = 0;
    }

    /**
     * Clear modifier
     * 
     * @see #modify(int)
     * @see #modifyClear()
     * @see #setModifier(int)
     */
    public static void modifyClear(int mod) {
        mod &= ThemeManager._THEME_MASK;
        ThemeManager._THEME_MODIFIER |= mod;
        ThemeManager._THEME_MODIFIER ^= mod;
    }

    /**
     * Like {@link #modify(int)}, but applying only on default theme.
     * 
     * @see #modifyDefaultThemeClear(int)
     * @see #setDefaultTheme(int)
     * @see #getDefaultTheme()
     */
    public static void modifyDefaultTheme(int mod) {
        ThemeManager._DEFAULT_THEME |= mod & ThemeManager._THEME_MASK;
    }

    /**
     * Clear modifier from default theme
     * 
     * @see #modifyDefaultTheme(int)
     * @see #setDefaultTheme(int)
     * @see #getDefaultTheme()
     */
    public static void modifyDefaultThemeClear(int mod) {
        mod &= ThemeManager._THEME_MASK;
        ThemeManager._DEFAULT_THEME |= mod;
        ThemeManager._DEFAULT_THEME ^= mod;
    }

    private static int prepareFlags(int i) {
        if (i >= _START_RESOURCES_ID) {
            return i;
        }
        if (ThemeManager._THEME_MODIFIER > 0) {
            i |= ThemeManager._THEME_MODIFIER;
        }
        return i & ThemeManager._THEME_MASK;
    }

    /**
     * Check activity on dark theme and restart it if theme incorrect.
     * 
     * @see #restartWithTheme(Activity, int)
     */
    public static void restartWithDarkTheme(Activity activity) {
        ThemeManager.restartWithTheme(activity, ThemeManager.DARK);
    }

    /**
     * Check activity on light theme and restart it if theme incorrect.
     * 
     * @see #restartWithTheme(Activity, int)
     */
    public static void restartWithLightTheme(Activity activity) {
        ThemeManager.restartWithTheme(activity, ThemeManager.LIGHT);
    }

    /**
     * @deprecated Use {@link #restartWithMixedTheme(Activity)} instead
     */
    @Deprecated
    public static void restartWithLightWithDarkActionBarTheme(Activity activity) {
        restartWithMixedTheme(activity);
    }

    /**
     * Check activity on light with dark action bar theme and restart it if
     * theme incorrect.
     * 
     * @see #restartWithTheme(Activity, int)
     */
    public static void restartWithMixedTheme(Activity activity) {
        ThemeManager.restartWithTheme(activity, ThemeManager.MIXED);
    }

    /**
     * Check activity on theme and restart it if theme incorrect.
     * 
     * @param activity Activity
     * @param theme Theme flags for check
     */
    public static void restartWithTheme(Activity activity, int theme) {
        ThemeManager.restartWithTheme(activity, theme, false);
    }

    /**
     * Like {@link #restartWithTheme(Activity, int)}, but if third arg is true -
     * restart activity regardless theme.
     * 
     * @param activity Activity
     * @param theme Theme flags for check
     * @param force Force restart activity
     */
    public static void restartWithTheme(Activity activity, int theme,
            boolean force) {
        if (theme < _START_RESOURCES_ID) {
            if (ThemeManager._THEME_MODIFIER > 0) {
                theme |= ThemeManager._THEME_MODIFIER;
            }
            theme &= ThemeManager._THEME_MASK;
        }
        if (force || ThemeManager.getTheme(activity) != theme) {
            Intent intent = activity.getIntent();
            intent.setClass(activity, activity.getClass());
            intent.putExtra(ThemeManager._THEME_TAG, theme);
            if (activity.isRestricted()) {
                Application app = Application.getLastInstance();
                if (app != null && !app.isRestricted()) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    app.superStartActivity(intent, -1, null);
                }
            } else {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
                if (activity instanceof SuperStartActivity) {
                    ((SuperStartActivity) activity).superStartActivity(intent,
                            -1, null);
                } else {
                    activity.startActivity(intent);
                }
            }
        }
    }

    /**
     * Set default theme. May be theme resource instead flags, but it not
     * recommend.
     * 
     * @param theme Theme
     * @see #modifyDefaultTheme(int)
     * @see #modifyDefaultThemeClear(int)
     * @see #getDefaultTheme()
     */
    public static void setDefaultTheme(int theme) {
        ThemeManager._DEFAULT_THEME = theme;
        if (theme < _START_RESOURCES_ID) {
            ThemeManager._DEFAULT_THEME &= ThemeManager._THEME_MASK;
        }
    }

    /**
     * Set theme modifiers. See {@link #modify(int)}
     * 
     * @param mod Modififers
     * @see #modify(int)
     */
    public static void setModifier(int mod) {
        ThemeManager._THEME_MODIFIER = mod & ThemeManager._THEME_MASK;
    }

    /**
     * Set {@link ThemeGetter} instance for getting theme resources.
     * 
     * @param themeGetter ThemeGetter
     */
    public static void setThemeGetter(ThemeGetter themeGetter) {
        ThemeManager._THEME_GETTER = themeGetter;
    }

    /**
     * @deprecated Use {@link #setModifier(int)} instead
     */
    @Deprecated
    public static void setThemeModifier(int mod) {
        setModifier(mod);
    }

    /**
     * Only for system use
     */
    public static void startActivity(Context context, Intent intent) {
        ThemeManager.startActivity(context, intent, -1);
    }

    /**
     * Only for system use
     */
    public static void startActivity(Context context, Intent intent,
            Bundle options) {
        ThemeManager.startActivity(context, intent, -1, options);
    }

    /**
     * Only for system use
     */
    public static void startActivity(Context context, Intent intent,
            int requestCode) {
        ThemeManager.startActivity(context, intent, requestCode, null);
    }

    /**
     * Only for system use
     */
    @SuppressLint("NewApi")
    public static void startActivity(Context context, Intent intent,
            int requestCode, Bundle options) {
        final Activity activity = context instanceof Activity ? (Activity) context
                : null;
        if (activity != null) {
            ThemeManager.cloneTheme(activity.getIntent(), intent, true);
        }
        if (context instanceof SuperStartActivity) {
            ((SuperStartActivity) context).superStartActivity(intent,
                    requestCode, options);
        } else {
            if (activity != null) {
                if (VERSION.SDK_INT >= 16) {
                    activity.startActivityForResult(intent, requestCode,
                            options);
                } else {
                    activity.startActivityForResult(intent, requestCode);
                }
            } else {
                if (VERSION.SDK_INT >= 16) {
                    context.startActivity(intent, options);
                } else {
                    context.startActivity(intent);
                }
            }
        }
    }

    private ThemeManager() {
    }
}
