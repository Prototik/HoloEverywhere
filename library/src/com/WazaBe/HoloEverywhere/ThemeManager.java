package com.WazaBe.HoloEverywhere;

import android.content.Intent;

import com.WazaBe.HoloEverywhere.app.Activity;

public final class ThemeManager {
	public static interface ThemeGetter {
		public int getThemeResource(int themeTag, boolean abs);
	}

	public static final int DARK = 1;
	public static final int FULLSCREEN = 4096;
	public static final int LIGHT = 2;
	public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
	public static final int NO_ACTION_BAR = 2048;
	public static final int THEME_DEFAULT = DARK;
	public static final String THEME_TAG = "holoeverywhere:theme";
	private static ThemeGetter themeGetter;

	public static void applyTheme(Activity activity) {
		applyTheme(activity, activity.isForceThemeApply());
	}

	public static void applyTheme(Activity activity, boolean force) {
		if (hasSpecifiedTheme(activity) || force) {
			int theme = getThemeResource(getTheme(activity),
					activity.isABSSupport());
			activity.setTheme(theme);
		}
	}

	public static int getTheme(Activity activity) {
		return activity.getIntent().getIntExtra(THEME_TAG, THEME_DEFAULT);
	}

	public static int getThemeResource(int themeTag, boolean abs) {
		if (themeGetter != null) {
			int getterResource = themeGetter.getThemeResource(themeTag, abs);
			if (getterResource > 0) {
				return getterResource;
			}
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
			} else {
				throw new RuntimeException("AHTUNG EXCEPTION");
			}
		} else {
			return themeTag;
		}
	}

	public static boolean hasSpecifiedTheme(Activity activity) {
		return activity.getIntent().hasExtra(THEME_TAG)
				&& activity.getIntent().getIntExtra(THEME_TAG, 0) > 0;
	}

	public static boolean is(int config, int key) {
		return (config & key) != 0;
	}

	public static void restartWithTheme(Activity activity, int theme) {
		restartWithTheme(activity, theme, false);
	}

	public static void restartWithTheme(Activity activity, int theme,
			boolean force) {
		if (getTheme(activity) != theme || force) {
			Intent intent = activity.getIntent();
			intent.setClass(activity, activity.getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(THEME_TAG, theme);
			activity.finish();
			activity.startActivity(intent);
		}
	}

	public static void setThemeGetter(ThemeGetter themeGetter) {
		ThemeManager.themeGetter = themeGetter;
	}

	private ThemeManager() {
	}
}
