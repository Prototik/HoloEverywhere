package com.WazaBe.HoloEverywhere;

import android.app.Activity;
import android.content.Intent;

import com.WazaBe.HoloEverywhere.app.Application;
import com.WazaBe.HoloEverywhere.app.Base;

public final class ThemeManager {
	public static interface ThemeGetter {
		public int getThemeResource(int themeTag, boolean abs);
	}

	public static final int DARK = 1;
	public static final int FULLSCREEN = 4096;
	public static final int LIGHT = 2;
	public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
	public static final int NO_ACTION_BAR = 2048;
	public static int THEME_DEFAULT = DARK;
	public static final String THEME_TAG = "holoeverywhere:theme";
	private static ThemeGetter themeGetter;

	public static void applyTheme(Activity activity) {
		boolean force = activity instanceof Base ? ((Base) activity)
				.isForceThemeApply() : false;
		applyTheme(activity, force);
	}

	public static void applyTheme(Activity activity, boolean force) {
		if (force || hasSpecifiedTheme(activity)) {
			activity.setTheme(getThemeResource(activity));
		}
	}

	public static void cloneTheme(Intent sourceIntent, Intent intent) {
		if (hasSpecifiedTheme(sourceIntent)) {
			intent.putExtra(THEME_TAG, getTheme(sourceIntent));
		}
	}

	public static int getTheme(Activity activity) {
		return getTheme(activity.getIntent());
	}

	public static int getTheme(Intent intent) {
		return intent.getIntExtra(THEME_TAG, THEME_DEFAULT);
	}

	public static int getThemeResource(Activity activity) {
		boolean force = activity instanceof Base ? ((Base) activity)
				.isABSSupport() : false;
		return getThemeResource(getTheme(activity), force);
	}

	public static int getThemeResource(int themeTag, boolean abs) {
		if (themeGetter != null) {
			int getterResource = themeGetter.getThemeResource(themeTag, abs);
			if (getterResource > 0) {
				return getterResource;
			}
		}
		if (themeTag >= 0x01000000) {
			return themeTag;
		}
		boolean dark = is(themeTag, DARK);
		boolean light = is(themeTag, LIGHT);
		boolean lightWithDarkActionBar = is(themeTag,
				LIGHT_WITH_DARK_ACTION_BAR);
		boolean noActionBar = is(themeTag, NO_ACTION_BAR);
		boolean fullScreen = is(themeTag, FULLSCREEN);
		if (dark || light || lightWithDarkActionBar) {
			if (dark) {
				if (noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_NoActionBar_Fullscreen
							: R.style.Holo_Theme_NoActionBar_Fullscreen;
				} else if (noActionBar && !fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_NoActionBar
							: R.style.Holo_Theme_NoActionBar;
				} else if (!noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Fullscreen
							: R.style.Holo_Theme_Fullscreen;
				} else {
					return abs ? R.style.Holo_Theme_Sherlock
							: R.style.Holo_Theme;
				}
			} else if (light) {
				if (noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_NoActionBar_Fullscreen
							: R.style.Holo_Theme_Light_NoActionBar_Fullscreen;
				} else if (noActionBar && !fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_NoActionBar
							: R.style.Holo_Theme_Light_NoActionBar;
				} else if (!noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_Fullscreen
							: R.style.Holo_Theme_Light_Fullscreen;
				} else {
					return abs ? R.style.Holo_Theme_Sherlock_Light
							: R.style.Holo_Theme_Light;
				}
			} else if (lightWithDarkActionBar) {
				if (noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_DarkActionBar_NoActionBar_Fullscreen
							: R.style.Holo_Theme_Light_DarkActionBar_NoActionBar_Fullscreen;
				} else if (noActionBar && !fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_DarkActionBar_NoActionBar
							: R.style.Holo_Theme_Light_DarkActionBar_NoActionBar;
				} else if (!noActionBar && fullScreen) {
					return abs ? R.style.Holo_Theme_Sherlock_Light_DarkActionBar_Fullscreen
							: R.style.Holo_Theme_Light_DarkActionBar_Fullscreen;
				} else {
					return abs ? R.style.Holo_Theme_Sherlock_Light_DarkActionBar
							: R.style.Holo_Theme_Light_DarkActionBar;
				}
			}
		}
		return themeTag;
	}

	public static boolean hasSpecifiedTheme(Activity activity) {
		return hasSpecifiedTheme(activity.getIntent());
	}

	public static boolean hasSpecifiedTheme(Intent intent) {
		return intent != null && intent.hasExtra(THEME_TAG)
				&& intent.getIntExtra(THEME_TAG, 0) > 0;
	}

	private static boolean is(int config, int key) {
		return (config & key) != 0;
	}

	public static void restartWithTheme(Activity activity, int theme) {
		restartWithTheme(activity, theme, false);
	}

	public static void restartWithTheme(Activity activity, int theme,
			boolean force) {
		if (force || getTheme(activity) != theme) {
			Intent intent = activity.getIntent();
			intent.setClass(activity, activity.getClass());
			intent.putExtra(THEME_TAG, theme);
			if (activity.isRestricted()) {
				Application app = Application.getLastInstance();
				if (app != null && !app.isRestricted()) {
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					app.startActivity(intent);
				}
			} else {
				if (!activity.isFinishing()) {
					activity.finish();
				}
				activity.startActivity(intent);
			}
		}
	}

	public static void setThemeGetter(ThemeGetter themeGetter) {
		ThemeManager.themeGetter = themeGetter;
	}

	private ThemeManager() {
	}
}
