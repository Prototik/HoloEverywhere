package com.WazaBe.HoloEverywhere;

import android.content.Intent;

import com.WazaBe.HoloEverywhere.app.Activity;

public final class ThemeManager {
	public static interface ThemeGetter {
		public int getThemeResource(int themeTag, boolean abs);
	}

	public static final int HOLO_DARK = 1;
	public static final int HOLO_LIGHT = 2;
	public static final int THEME_DEFAULT = HOLO_DARK;

	public static final String THEME_TAG = "holoeverywhere:theme";

	private static ThemeGetter themeGetter;

	public static void applyTheme(Activity activity) {
		applyTheme(activity, false);
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
		switch (themeTag) {
		case HOLO_DARK:
		default:
			return abs ? R.style.Holo_Theme_Sherlock : R.style.Holo_Theme;
		case HOLO_LIGHT:
			return abs ? R.style.Holo_Theme_Sherlock_Light
					: R.style.Holo_Theme_Light;
		}
	}

	public static boolean hasSpecifiedTheme(Activity activity) {
		return activity.getIntent().hasExtra(THEME_TAG)
				&& activity.getIntent().getIntExtra(THEME_TAG, 0) > 0;
	}

	public static void restartWithTheme(Activity activity, int theme) {
		restartWithTheme(activity, theme, false);
	}

	public static void restartWithTheme(Activity activity, int theme,
			boolean force) {
		if (getTheme(activity) != theme || force) {
			Intent intent = activity.getIntent();
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
