package com.WazaBe.HoloEverywhere;

import java.util.HashSet;
import java.util.Set;

public final class Settings {
	private static final class DefaultSettingsListener implements
			SettingsListener {
		@Override
		public void onAttach() {
			setPackageName(DEFAULT_PACKAGE_NAME);
			setUseThemeManager(false);
			setUseParentTheme(false);
			setPreferenceMode(PreferenceMode.JSON);
		}

		@Override
		public void onChangePackageName(String oldPackageName,
				String newPackageName) {
			setWidgetsPackage(newPackageName + ".widget");
			setPreferencePackage(newPackageName + ".preference");
		}

		@Override
		public void onChangePreferenceMode(PreferenceMode oldPreferenceMode,
				PreferenceMode newPreferenceMode) {

		}

		@Override
		public void onChangePreferencePackage(String oldPreferencePackage,
				String newPreferencePackage) {

		}

		@Override
		public void onChangeUseParentTheme(boolean oldUseParentTheme,
				boolean newUseParentTheme) {

		}

		@Override
		public void onChangeUseThemeManager(boolean oldUseThemeManager,
				boolean newUseThemeManager) {

		}

		@Override
		public void onChangeWidgetsPackage(String oldWidgetsPackage,
				String newWidgetsPackage) {

		}

		@Override
		public void onDeattach() {

		}
	}

	public static enum PreferenceMode {
		JSON, XML;
	}

	public static interface SettingsListener {
		public void onAttach();

		public void onChangePackageName(String oldPackageName,
				String newPackageName);

		public void onChangePreferenceMode(PreferenceMode oldPreferenceMode,
				PreferenceMode newPreferenceMode);

		public void onChangePreferencePackage(String oldPreferencePackage,
				String newPreferencePackage);

		public void onChangeUseParentTheme(boolean oldUseParentTheme,
				boolean newUseParentTheme);

		public void onChangeUseThemeManager(boolean oldUseThemeManager,
				boolean newUseThemeManager);

		public void onChangeWidgetsPackage(String oldWidgetsPackage,
				String newWidgetsPackage);

		public void onDeattach();
	}

	private static final String DEFAULT_PACKAGE_NAME = Settings.class
			.getPackage().getName();
	private static final SettingsListener DEFAULT_SETTINGS_LISTENER = new DefaultSettingsListener();
	private static final Set<SettingsListener> LISTENERS = new HashSet<SettingsListener>();
	private static String packageName, widgetsPackage, preferencePackage;
	private static PreferenceMode preferenceMode;
	private static boolean useThemeManager, useParentTheme;

	static {
		addSettingsListener(DEFAULT_SETTINGS_LISTENER);
	}

	public static void addSettingsListener(SettingsListener listener) {
		if (listener != null) {
			if (LISTENERS.contains(listener)) {
				synchronized (LISTENERS) {
					if (LISTENERS.contains(listener)) {
						LISTENERS.remove(listener);
						listener.onDeattach();
					}
				}
			}
			LISTENERS.add(listener);
			listener.onAttach();
		}
	}

	public static String getPackageName() {
		return packageName;
	}

	public static PreferenceMode getPreferenceMode() {
		return preferenceMode;
	}

	public static String getPreferencePackage() {
		return preferencePackage;
	}

	public static String getWidgetsPackage() {
		return widgetsPackage;
	}

	/*
	 * Nop. For execute static code block
	 */
	public static void init() {

	}

	public static boolean isUseParentTheme() {
		return useParentTheme;
	}

	public static boolean isUseThemeManager() {
		return useThemeManager;
	}

	private static void onChangePackageName(String oldPackageName,
			String newPackageName) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangePackageName(oldPackageName, newPackageName);
			}
		}
	}

	private static void onChangePreferenceMode(
			PreferenceMode oldPreferenceMode, PreferenceMode newPreferenceMode) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangePreferenceMode(oldPreferenceMode,
						newPreferenceMode);
			}
		}
	}

	private static void onChangePreferencePackage(String oldPreferencePackage,
			String newPreferencePackage) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangePreferencePackage(oldPreferencePackage,
						newPreferencePackage);
			}
		}
	}

	private static void onChangeUseParentTheme(boolean oldUseParentTheme,
			boolean newUseParentTheme) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangeUseParentTheme(oldUseParentTheme,
						newUseParentTheme);
			}
		}
	}

	private static void onChangeUseThemeManager(boolean oldUseThemeManager,
			boolean newUseThemeManager) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangeUseThemeManager(oldUseThemeManager,
						newUseThemeManager);
			}
		}
	}

	private static void onChangeWidgetsPackage(String oldWidgetsPackage,
			String newWidgetsPackage) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangeWidgetsPackage(oldWidgetsPackage,
						newWidgetsPackage);
			}
		}
	}

	public static void removeDefaultSettingsListener() {
		removeSettingsListener(DEFAULT_SETTINGS_LISTENER);
	}

	public static void removeSettingsListener(SettingsListener listener) {
		if (listener != null) {
			if (LISTENERS.contains(listener)) {
				synchronized (LISTENERS) {
					if (LISTENERS.contains(listener)) {
						LISTENERS.remove(listener);
						listener.onDeattach();
					}
				}
			}
		}
	}

	public static void setPackageName(String packageName) {
		if (packageName != null && !packageName.equals(Settings.packageName)) {
			String oldPackageName = Settings.packageName;
			Settings.packageName = packageName;
			onChangePackageName(oldPackageName, packageName);
		}
	}

	public static void setPreferenceMode(PreferenceMode preferenceMode) {
		if (preferenceMode != null && preferenceMode != Settings.preferenceMode) {
			PreferenceMode oldPreferenceMode = Settings.preferenceMode;
			Settings.preferenceMode = preferenceMode;
			onChangePreferenceMode(oldPreferenceMode, preferenceMode);
		}
	}

	public static void setPreferencePackage(String preferencePackage) {
		if (preferencePackage != null
				&& !preferencePackage.equals(Settings.preferencePackage)) {
			String oldPreferencePackage = Settings.preferencePackage;
			Settings.preferencePackage = preferencePackage;
			onChangePreferencePackage(oldPreferencePackage, preferencePackage);
		}
	}

	public static void setUseParentTheme(boolean useParentTheme) {
		if (useParentTheme != Settings.useParentTheme) {
			boolean oldUseParentTheme = Settings.useParentTheme;
			Settings.useParentTheme = useParentTheme;
			onChangeUseParentTheme(oldUseParentTheme, useParentTheme);
		}
	}

	public static void setUseThemeManager(boolean useThemeManager) {
		if (useThemeManager != Settings.useThemeManager) {
			boolean oldUseThemeManager = Settings.useThemeManager;
			Settings.useThemeManager = useThemeManager;
			onChangeUseThemeManager(oldUseThemeManager, useThemeManager);
		}
	}

	public static void setWidgetsPackage(String widgetsPackage) {
		if (widgetsPackage != null
				&& !widgetsPackage.equals(Settings.widgetsPackage)) {
			String oldWidgetsPackage = Settings.widgetsPackage;
			Settings.widgetsPackage = widgetsPackage;
			onChangeWidgetsPackage(oldWidgetsPackage, widgetsPackage);
		}
	}

}
