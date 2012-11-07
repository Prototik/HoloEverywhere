
package org.holoeverywhere;

import org.holoeverywhere.app.Application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.SparseIntArray;

public final class ThemeManager {
    public static interface ThemedIntentStarter {
        public void superStartActivity(Intent intent, int requestCode,
                Bundle options);
    }

    public static interface ThemeGetter {
        public int getThemeResource(ThemeTag themeTag);
    }

    public static final class ThemeTag {
        public final boolean dark;
        public final boolean fullscreen;
        public final boolean light;
        public final boolean mixed;
        public final boolean noActionBar;
        public final int themeTag;

        private ThemeTag(int themeTag) {
            this.themeTag = themeTag;
            dark = isDark(themeTag);
            light = isLight(themeTag);
            mixed = isMixed(themeTag);
            noActionBar = isNoActionBar(themeTag);
            fullscreen = isFullScreen(themeTag);
        }
    }

    public static final int DARK = 1;
    private static int defaultTheme = ThemeManager.DARK;
    public static final int FULLSCREEN = 16;
    public static final int LIGHT = 2;
    /**
     * @deprecated Use {@link #MIXED} instead
     */
    @Deprecated
    public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
    public static final int MIXED = 4;
    public static final int NO_ACTION_BAR = 8;
    private static final int THEME_MASK = ThemeManager.DARK
            | ThemeManager.LIGHT | ThemeManager.MIXED
            | ThemeManager.NO_ACTION_BAR | ThemeManager.FULLSCREEN;
    private static final String THEME_TAG = "holoeverywhere:theme";
    private static ThemeGetter themeGetter;
    private static int themeModifier = 0;
    private static final SparseIntArray THEMES = new SparseIntArray();

    static {
        map(DARK, R.style.Holo_Theme);
        map(DARK | FULLSCREEN, R.style.Holo_Theme_Fullscreen);
        map(DARK | NO_ACTION_BAR, R.style.Holo_Theme_NoActionBar);
        map(DARK | NO_ACTION_BAR | FULLSCREEN, R.style.Holo_Theme_NoActionBar_Fullscreen);
        map(LIGHT, R.style.Holo_Theme);
        map(LIGHT | FULLSCREEN, R.style.Holo_Theme_Light_Fullscreen);
        map(LIGHT | NO_ACTION_BAR, R.style.Holo_Theme_Light_NoActionBar);
        map(LIGHT | NO_ACTION_BAR | FULLSCREEN, R.style.Holo_Theme_Light_NoActionBar_Fullscreen);
        map(MIXED, R.style.Holo_Theme_Light_DarkActionBar);
        map(MIXED | FULLSCREEN,
                R.style.Holo_Theme_Light_DarkActionBar_Fullscreen);
        map(MIXED | NO_ACTION_BAR,
                R.style.Holo_Theme_Light_DarkActionBar_NoActionBar);
        map(MIXED | NO_ACTION_BAR | FULLSCREEN,
                R.style.Holo_Theme_Light_DarkActionBar_NoActionBar_Fullscreen);
    }

    public static void applyTheme(Activity activity) {
        boolean force = activity instanceof IHoloActivity ? ((IHoloActivity) activity)
                .isForceThemeApply() : false;
        ThemeManager.applyTheme(activity, force);
    }

    public static void applyTheme(Activity activity, boolean force) {
        if (force || ThemeManager.hasSpecifiedTheme(activity)) {
            activity.setTheme(ThemeManager.getThemeResource(activity));
        }
    }

    public static void cloneTheme(Intent sourceIntent, Intent intent) {
        ThemeManager.cloneTheme(sourceIntent, intent, false);
    }

    public static void cloneTheme(Intent sourceIntent, Intent intent,
            boolean force) {
        if (ThemeManager.hasSpecifiedTheme(sourceIntent) || force) {
            if (!ThemeManager.hasSpecifiedTheme(intent) || force) {
                intent.putExtra(ThemeManager.THEME_TAG,
                        ThemeManager.getTheme(sourceIntent));
            } else {
                intent.putExtra(ThemeManager.THEME_TAG,
                        ThemeManager.defaultTheme);
            }
        }
    }

    public static int getDefaultTheme() {
        return ThemeManager.defaultTheme;
    }

    public static int getModifier() {
        return ThemeManager.themeModifier;
    }

    public static int getTheme(Activity activity) {
        return ThemeManager.getTheme(activity.getIntent());
    }

    public static int getTheme(Intent intent) {
        int i = intent.getIntExtra(ThemeManager.THEME_TAG,
                ThemeManager.defaultTheme);
        if (ThemeManager.themeModifier > 0) {
            i |= ThemeManager.themeModifier;
        }
        return i & ThemeManager.THEME_MASK;
    }

    public static int getThemeResource(Activity activity) {
        return ThemeManager.getThemeResource(ThemeManager.getTheme(activity));
    }

    private static final int START_RESOURCES_ID = 0x01000000;

    public static int getThemeResource(int themeTag) {
        if (themeTag >= START_RESOURCES_ID) {
            return themeTag;
        }
        if (ThemeManager.themeModifier > 0) {
            themeTag |= ThemeManager.themeModifier;
        }
        themeTag &= ThemeManager.THEME_MASK;
        if (ThemeManager.themeGetter != null) {
            final int getterResource = ThemeManager.themeGetter.getThemeResource(
                    new ThemeTag(themeTag));
            if (getterResource > 0) {
                return getterResource;
            }
        }
        return THEMES.get(themeTag, THEMES.get(defaultTheme));
    }

    public static boolean hasSpecifiedTheme(Activity activity) {
        return activity == null ? false : ThemeManager.hasSpecifiedTheme(activity.getIntent());
    }

    public static boolean hasSpecifiedTheme(Intent intent) {
        return intent != null && intent.hasExtra(ThemeManager.THEME_TAG)
                && intent.getIntExtra(ThemeManager.THEME_TAG, 0) > 0;
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
        return ThemeManager.isMixed(ThemeManager
                .getTheme(activity));
    }

    public static boolean isMixed(int i) {
        return ThemeManager.is(i, ThemeManager.MIXED);
    }

    public static boolean isMixed(Intent intent) {
        return ThemeManager.isMixed(ThemeManager
                .getTheme(intent));
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

    public static void map(int flags, int theme) {
        THEMES.put(flags & THEME_MASK, theme);
    }

    public static void modify(int mod) {
        ThemeManager.themeModifier |= mod & ThemeManager.THEME_MASK;
    }

    public static void modifyDefaultTheme(int mod) {
        ThemeManager.defaultTheme |= mod & ThemeManager.THEME_MASK;
    }

    public static void restartWithDarkTheme(Activity activity) {
        ThemeManager.restartWithTheme(activity, ThemeManager.DARK);
    }

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

    public static void restartWithMixedTheme(Activity activity) {
        ThemeManager.restartWithTheme(activity, ThemeManager.MIXED);
    }

    public static void restartWithTheme(Activity activity, int theme) {
        ThemeManager.restartWithTheme(activity, theme, false);
    }

    public static void restartWithTheme(Activity activity, int theme,
            boolean force) {
        if (theme < START_RESOURCES_ID) {
            if (ThemeManager.themeModifier > 0) {
                theme |= ThemeManager.themeModifier;
            }
            theme &= ThemeManager.THEME_MASK;
        }
        if (force || ThemeManager.getTheme(activity) != theme) {
            Intent intent = activity.getIntent();
            intent.setClass(activity, activity.getClass());
            intent.putExtra(ThemeManager.THEME_TAG, theme);
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
                if (activity instanceof ThemedIntentStarter) {
                    ((ThemedIntentStarter) activity).superStartActivity(intent,
                            -1, null);
                } else {
                    activity.startActivity(intent);
                }
            }
        }
    }

    public static void setDefaultTheme(int theme) {
        ThemeManager.defaultTheme = theme;
        if (theme < START_RESOURCES_ID) {
            ThemeManager.defaultTheme &= ThemeManager.THEME_MASK;
        }
    }

    public static void setThemeGetter(ThemeGetter themeGetter) {
        ThemeManager.themeGetter = themeGetter;
    }

    public static void setThemeModifier(int mod) {
        ThemeManager.themeModifier = mod & ThemeManager.THEME_MASK;
    }

    public static void startActivity(Context context, Intent intent) {
        ThemeManager.startActivity(context, intent, -1);
    }

    public static void startActivity(Context context, Intent intent,
            Bundle options) {
        ThemeManager.startActivity(context, intent, -1, options);
    }

    public static void startActivity(Context context, Intent intent,
            int requestCode) {
        ThemeManager.startActivity(context, intent, requestCode, null);
    }

    @SuppressLint("NewApi")
    public static void startActivity(Context context, Intent intent,
            int requestCode, Bundle options) {
        final Activity activity = context instanceof Activity ? (Activity) context
                : null;
        if (activity != null) {
            ThemeManager.cloneTheme(activity.getIntent(), intent, true);
        }
        if (context instanceof ThemedIntentStarter) {
            ((ThemedIntentStarter) context).superStartActivity(intent,
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
