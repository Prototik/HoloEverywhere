package com.WazaBe.HoloEverywhere;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import com.WazaBe.HoloEverywhere.app.Application;
import com.WazaBe.HoloEverywhere.app.Base;

public final class ThemeManager {
	public static interface ThemedIntentStarter {
		public void superStartActivity(Intent intent, int requestCode,
				Bundle options);
	}

	public static interface ThemeGetter {
		public int getThemeResource(int themeTag, boolean abs);
	}

	public static final int DARK = 1;
	private static int defaultTheme = ThemeManager.DARK;
	public static final int FULLSCREEN = 16;
	public static final int LIGHT = 2;
	public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
	public static final int NO_ACTION_BAR = 8;
	private static boolean onlyBaseThemes = true;
	private static final int THEME_MASK = ThemeManager.DARK
			| ThemeManager.LIGHT | ThemeManager.LIGHT_WITH_DARK_ACTION_BAR
			| ThemeManager.NO_ACTION_BAR | ThemeManager.FULLSCREEN;
	private static final String THEME_TAG = "holoeverywhere:theme";
	private static ThemeGetter themeGetter;
	private static int themeModifier = 0;

	public static void applyTheme(Activity activity) {
		boolean force = activity instanceof Base ? ((Base) activity)
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
		if (ThemeManager.onlyBaseThemes) {
			i &= ThemeManager.THEME_MASK;
		}
		if (ThemeManager.themeModifier > 0) {
			i |= ThemeManager.themeModifier;
		}
		return i;
	}

	public static int getThemeResource(Activity activity) {
		boolean force = activity instanceof Base ? ((Base) activity)
				.isABSSupport() : false;
		return ThemeManager.getThemeResource(ThemeManager.getTheme(activity),
				force);
	}

	public static int getThemeResource(int themeTag, boolean abs) {
		if (ThemeManager.themeModifier > 0) {
			themeTag |= ThemeManager.themeModifier;
		}
		if (ThemeManager.themeGetter != null) {
			int getterResource = ThemeManager.themeGetter.getThemeResource(
					themeTag, abs);
			if (getterResource > 0) {
				return getterResource;
			}
		}
		if (ThemeManager.onlyBaseThemes) {
			themeTag &= ThemeManager.THEME_MASK;
		} else if (themeTag >= 0x01000000) {
			return themeTag;
		}
		boolean dark = ThemeManager.isDark(themeTag);
		boolean light = ThemeManager.isLight(themeTag);
		boolean lightWithDarkActionBar = ThemeManager
				.isLightWithDarkActionBar(themeTag);
		boolean noActionBar = ThemeManager.isNoActionBar(themeTag);
		boolean fullScreen = ThemeManager.isFullScreen(themeTag);
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
				return abs ? R.style.Holo_Theme_Sherlock : R.style.Holo_Theme;
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
		return themeTag;
	}

	public static boolean hasSpecifiedTheme(Activity activity) {
		return ThemeManager.hasSpecifiedTheme(activity.getIntent());
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

	public static boolean isLightWithDarkActionBar(Activity activity) {
		return ThemeManager.isLightWithDarkActionBar(ThemeManager
				.getTheme(activity));
	}

	public static boolean isLightWithDarkActionBar(int i) {
		return ThemeManager.is(i, ThemeManager.LIGHT_WITH_DARK_ACTION_BAR);
	}

	public static boolean isLightWithDarkActionBar(Intent intent) {
		return ThemeManager.isLightWithDarkActionBar(ThemeManager
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

	public static void modify(int mod) {
		if (ThemeManager.onlyBaseThemes) {
			mod &= ThemeManager.THEME_MASK;
		}
		ThemeManager.themeModifier |= mod;
	}

	public static void modifyDefaultTheme(int mod) {
		if (ThemeManager.onlyBaseThemes) {
			mod &= ThemeManager.THEME_MASK;
		}
		ThemeManager.defaultTheme |= mod;
	}

	public static void restartWithDarkTheme(Activity activity) {
		ThemeManager.restartWithTheme(activity, ThemeManager.DARK);
	}

	public static void restartWithLightTheme(Activity activity) {
		ThemeManager.restartWithTheme(activity, ThemeManager.LIGHT);
	}

	public static void restartWithLightWithDarkActionBarTheme(Activity activity) {
		ThemeManager.restartWithTheme(activity,
				ThemeManager.LIGHT_WITH_DARK_ACTION_BAR);
	}

	public static void restartWithTheme(Activity activity, int theme) {
		ThemeManager.restartWithTheme(activity, theme, false);
	}

	public static void restartWithTheme(Activity activity, int theme,
			boolean force) {
		if (ThemeManager.themeModifier > 0) {
			theme |= ThemeManager.themeModifier;
		}
		if (ThemeManager.onlyBaseThemes) {
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

	public static void setDefaultTheme(int theme) {
		if (ThemeManager.onlyBaseThemes) {
			theme &= ThemeManager.THEME_MASK;
		}
		ThemeManager.defaultTheme = theme;
	}

	public static void setOnlyBaseThemes(boolean onlyBaseThemes) {
		ThemeManager.onlyBaseThemes = onlyBaseThemes;
	}

	public static void setThemeGetter(ThemeGetter themeGetter) {
		ThemeManager.themeGetter = themeGetter;
	}

	public static void setThemeModifier(int mod) {
		if (ThemeManager.onlyBaseThemes) {
			mod &= ThemeManager.THEME_MASK;
		}
		ThemeManager.themeModifier = mod;
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
