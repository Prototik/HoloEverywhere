package com.WazaBe.HoloEverywhere.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.Settings;
import com.WazaBe.HoloEverywhere.Settings.BooleanProperty;
import com.WazaBe.HoloEverywhere.Settings.EnumProperty;
import com.WazaBe.HoloEverywhere.Settings.Property;
import com.WazaBe.HoloEverywhere.Settings.SettingListener;
import com.WazaBe.HoloEverywhere.Settings.StringProperty;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.ThemeManager.ThemedIntentStarter;

public class Application extends android.app.Application implements
		ThemedIntentStarter {
	private static final String DEFAULT_HOLO_EVERYWHERE_PACKAGE = "com.WazaBe.HoloEverywhere";

	public static final class Setting extends Settings.Setting<Setting> {
		public static enum PreferenceImpl {
			JSON, XML
		}

		private final SettingListener DEFAULT_SETTINGS_LISTENER = new SettingListener() {
			@Override
			public void onAttach(Settings.Setting<?> setting) {
			}

			@Override
			public void onDetach(Settings.Setting<?> setting) {
			}

			@Override
			public void onPropertyChange(Settings.Setting<?> setting,
					Property<?> property) {
				if (property == holoEverywherePackage) {
					String p = holoEverywherePackage.getValue();
					setWidgetsPackage(p + ".widget");
					setPreferencePackage(p + ".preference");
				}
			}
		};
		@SettingProperty(create = true)
		private BooleanProperty alwaysUseParentTheme;
		@SettingProperty(create = true)
		private StringProperty holoEverywherePackage;
		@SettingProperty(create = true)
		private StringProperty widgetsPackage;
		@SettingProperty(create = true)
		private StringProperty preferencePackage;
		@SettingProperty(create = true)
		private EnumProperty<PreferenceImpl> preferenceImpl;
		@SettingProperty(create = true)
		private BooleanProperty useThemeManager;

		public Setting attachDefaultListener() {
			return addListener(DEFAULT_SETTINGS_LISTENER);
		}

		public Setting detachDefaultListener() {
			return removeListener(DEFAULT_SETTINGS_LISTENER);
		}

		public String getHoloEverywherePackage() {
			return holoEverywherePackage.getValue();
		}

		public PreferenceImpl getPreferenceImpl() {
			return preferenceImpl.getValue();
		}

		public String getPreferencePackage() {
			return preferencePackage.getValue();
		}

		public String getWidgetsPackage() {
			return widgetsPackage.getValue();
		}

		public boolean isAlwaysUseParentTheme() {
			return alwaysUseParentTheme.getValue();
		}

		public boolean isUseThemeManager() {
			return useThemeManager.getValue();
		}

		@Override
		protected void onInit() {
			attachDefaultListener();
			setPreferenceImpl(PreferenceImpl.JSON);
			setAlwaysUseParentTheme(false);
			setUseThemeManager(false);
			setHoloEverywherePackage(DEFAULT_HOLO_EVERYWHERE_PACKAGE);
		}

		public Setting setAlwaysUseParentTheme(boolean alwaysUseParentTheme) {
			this.alwaysUseParentTheme.setValue(alwaysUseParentTheme);
			return this;
		}

		public Setting setHoloEverywherePackage(String holoEverywherePackage) {
			this.holoEverywherePackage.setValue(holoEverywherePackage);
			return this;
		}

		public Setting setPreferenceImpl(PreferenceImpl preferenceImpl) {
			this.preferenceImpl.setValue(preferenceImpl);
			return this;
		}

		public Setting setPreferencePackage(String preferencePackage) {
			this.preferencePackage.setValue(preferencePackage);
			return this;
		}

		public Setting setUseThemeManager(boolean useThemeManager) {
			this.useThemeManager.setValue(useThemeManager);
			return this;
		}

		public Setting setWidgetsPackage(String widgetsPackage) {
			this.widgetsPackage.setValue(widgetsPackage);
			return this;
		}
	}

	private static Application lastInstance;

	public static Application getLastInstance() {
		return lastInstance;
	}

	public static Setting getSettings() {
		return Settings.get(Setting.class);
	}

	public Application() {
		lastInstance = this;
	}

	@Override
	@SuppressLint("NewApi")
	public void superStartActivity(Intent intent, int requestCode,
			Bundle options) {
		if (VERSION.SDK_INT >= 16) {
			super.startActivity(intent, options);
		} else {
			super.startActivity(intent);
		}
	}

	@Override
	public void onTerminate() {
		LayoutInflater.clearInstances();
		super.onTerminate();
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivities(Intent[] intents) {
		startActivities(intents, null);
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivities(Intent[] intents, Bundle options) {
		for (Intent intent : intents) {
			startActivity(intent, options);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void startActivity(Intent intent) {
		startActivity(intent, null);
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		if (getSettings().isAlwaysUseParentTheme()) {
			ThemeManager.startActivity(this, intent, options);
		} else {
			superStartActivity(intent, -1, options);
		}
	}
}
