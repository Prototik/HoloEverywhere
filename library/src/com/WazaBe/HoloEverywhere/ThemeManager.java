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
	private static int defaultTheme = DARK;
	public static final int FULLSCREEN = 16;
	public static final int LIGHT = 2;
	public static final int LIGHT_WITH_DARK_ACTION_BAR = 4;
	public static final int NO_ACTION_BAR = 8;
	private static boolean onlyBaseThemes = true;
	private static final int THEME_MASK = DARK | LIGHT
			| LIGHT_WITH_DARK_ACTION_BAR | NO_ACTION_BAR | FULLSCREEN;
	private static final String THEME_TAG = "holoeverywhere:theme";
	private static ThemeGetter themeGetter;
	private static int themeModifier = 0;

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
		cloneTheme(sourceIntent, intent, false);
	}

	public static void cloneTheme(Intent sourceIntent, Intent intent,
			boolean force) {
		if (hasSpecifiedTheme(sourceIntent) || force) {
			if (!hasSpecifiedTheme(intent) || force) {
				intent.putExtra(THEME_TAG, getTheme(sourceIntent));
			} else {
				intent.putExtra(THEME_TAG, defaultTheme);
			}
		}
	}

	public static int getDefaultTheme() {
		return defaultTheme;
	}

	public static int getModifier() {
		return themeModifier;
	}

	public static int getTheme(Activity activity) {
		return getTheme(activity.getIntent());
	}

	public static int getTheme(Intent intent) {
		int i = intent.getIntExtra(THEME_TAG, defaultTheme);
		if (onlyBaseThemes) {
			i &= THEME_MASK;
		}
		if (themeModifier > 0) {
			i |= themeModifier;
		}
		return i;
	}

	public static int getThemeResource(Activity activity) {
		boolean force = activity instanceof Base ? ((Base) activity)
				.isABSSupport() : false;
		return getThemeResource(getTheme(activity), force);
	}

	public static int getThemeResource(int themeTag, boolean abs) {
		if (themeModifier > 0) {
			themeTag |= themeModifier;
		}
		if (themeGetter != null) {
			int getterResource = themeGetter.getThemeResource(themeTag, abs);
			if (getterResource > 0) {
				return getterResource;
			}
		}
		if (onlyBaseThemes) {
			themeTag &= THEME_MASK;
		} else if (themeTag >= 0x01000000) {
			return themeTag;
		}
		boolean dark = isDark(themeTag);
		boolean light = isLight(themeTag);
		boolean lightWithDarkActionBar = isLightWithDarkActionBar(themeTag);
		boolean noActionBar = isNoActionBar(themeTag);
		boolean fullScreen = isFullScreen(themeTag);
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
		return hasSpecifiedTheme(activity.getIntent());
	}

	public static boolean hasSpecifiedTheme(Intent intent) {
		return intent != null && intent.hasExtra(THEME_TAG)
				&& intent.getIntExtra(THEME_TAG, 0) > 0;
	}

	private static boolean is(int config, int key) {
		return (config & key) != 0;
	}

	public static boolean isDark(Activity activity) {
		return isDark(getTheme(activity));
	}

	public static boolean isDark(int i) {
		return is(i, DARK);
	}

	public static boolean isDark(Intent intent) {
		return isDark(getTheme(intent));
	}

	public static boolean isFullScreen(Activity activity) {
		return isFullScreen(getTheme(activity));
	}

	public static boolean isFullScreen(int i) {
		return is(i, FULLSCREEN);
	}

	public static boolean isFullScreen(Intent intent) {
		return isFullScreen(getTheme(intent));
	}

	public static boolean isLight(Activity activity) {
		return isLight(getTheme(activity));
	}

	public static boolean isLight(int i) {
		return is(i, LIGHT);
	}

	public static boolean isLight(Intent intent) {
		return isLight(getTheme(intent));
	}

	public static boolean isLightWithDarkActionBar(Activity activity) {
		return isLightWithDarkActionBar(getTheme(activity));
	}

	public static boolean isLightWithDarkActionBar(int i) {
		return is(i, LIGHT_WITH_DARK_ACTION_BAR);
	}

	public static boolean isLightWithDarkActionBar(Intent intent) {
		return isLightWithDarkActionBar(getTheme(intent));
	}

	public static boolean isNoActionBar(Activity activity) {
		return isNoActionBar(getTheme(activity));
	}

	public static boolean isNoActionBar(int i) {
		return is(i, NO_ACTION_BAR);
	}

	public static boolean isNoActionBar(Intent intent) {
		return isNoActionBar(getTheme(intent));
	}

	public static void modify(int mod) {
		if (onlyBaseThemes) {
			mod &= THEME_MASK;
		}
		themeModifier |= mod;
	}

	public static void modifyDefaultTheme(int mod) {
		if (onlyBaseThemes) {
			mod &= THEME_MASK;
		}
		defaultTheme |= mod;
	}

	public static void restartWithDarkTheme(Activity activity) {
		restartWithTheme(activity, DARK);
	}

	public static void restartWithLightTheme(Activity activity) {
		restartWithTheme(activity, LIGHT);
	}

	public static void restartWithLightWithDarkActionBarTheme(Activity activity) {
		restartWithTheme(activity, LIGHT_WITH_DARK_ACTION_BAR);
	}

	public static void restartWithTheme(Activity activity, int theme) {
		restartWithTheme(activity, theme, false);
	}

	public static void restartWithTheme(Activity activity, int theme,
			boolean force) {
		if (themeModifier > 0) {
			theme |= themeModifier;
		}
		if (onlyBaseThemes) {
			theme &= THEME_MASK;
		}
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

	public static void setDefaultTheme(int theme) {
		if (onlyBaseThemes) {
			theme &= THEME_MASK;
		}
		defaultTheme = theme;
	}

	public static void setOnlyBaseThemes(boolean onlyBaseThemes) {
		ThemeManager.onlyBaseThemes = onlyBaseThemes;
	}

	public static void setThemeGetter(ThemeGetter themeGetter) {
		ThemeManager.themeGetter = themeGetter;
	}

	public static void setThemeModifier(int mod) {
		if (onlyBaseThemes) {
			mod &= THEME_MASK;
		}
		themeModifier = mod;
	}

	public static void startActivity(Context context, Intent intent) {
		startActivity(context, intent, -1);
	}

	public static void startActivity(Context context, Intent intent,
			Bundle options) {
		startActivity(context, intent, -1, options);
	}

	public static void startActivity(Context context, Intent intent,
			int requestCode) {
		startActivity(context, intent, requestCode, null);
	}

	@SuppressLint("NewApi")
	public static void startActivity(Context context, Intent intent,
			int requestCode, Bundle options) {
		final Activity activity = context instanceof Activity ? (Activity) context
				: null;
		if (activity != null) {
			cloneTheme(activity.getIntent(), intent, true);
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
