package com.WazaBe.HoloEverywhere;

import java.util.HashSet;
import java.util.Set;

public final class Settings {
	private static final class DefaultSettingsListener implements
			SettingsListener {
		@Override
		public void onChangePackageName(String oldPackageName,
				String newPackageName) {
			setWidgetsPackage(newPackageName + ".widget");
			setPreferencePackage(newPackageName + ".preference");
		}

		@Override
		public void onChangePreferencePackage(String oldPreferencePackage,
				String newPreferencePackage) {

		}

		@Override
		public void onChangeWidgetsPackage(String oldWidgetsPackage,
				String newWidgetsPackage) {

		}
	}

	public static interface SettingsListener {
		public void onChangePackageName(String oldPackageName,
				String newPackageName);

		public void onChangePreferencePackage(String oldPreferencePackage,
				String newPreferencePackage);

		public void onChangeWidgetsPackage(String oldWidgetsPackage,
				String newWidgetsPackage);
	}

	private static final String DEFAULT_PACKAGE_NAME = Settings.class
			.getPackage().getName();
	private static final SettingsListener DEFAULT_SETTINGS_LISTENER = new DefaultSettingsListener();
	private static final Set<SettingsListener> LISTENERS = new HashSet<SettingsListener>();
	private static String packageName, widgetsPackage, preferencePackage;

	static {
		addSettingsListener(DEFAULT_SETTINGS_LISTENER);
		setPackageName(DEFAULT_PACKAGE_NAME);
	}

	public static void addSettingsListener(SettingsListener listener) {
		if (listener != null) {
			if (LISTENERS.contains(listener)) {
				synchronized (LISTENERS) {
					if (LISTENERS.contains(listener)) {
						LISTENERS.remove(listener);
					}
				}
			}
			LISTENERS.add(listener);
		}
	}

	public static String getPackageName() {
		return packageName;
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

	private static void onChangePackageName(String oldPackageName,
			String newPackageName) {
		for (SettingsListener listener : LISTENERS) {
			if (listener != null) {
				listener.onChangePackageName(oldPackageName, newPackageName);
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

	public static void setPreferencePackage(String preferencePackage) {
		if (preferencePackage != null
				&& !preferencePackage.equals(Settings.preferencePackage)) {
			String oldPreferencePackage = Settings.preferencePackage;
			Settings.preferencePackage = preferencePackage;
			onChangePreferencePackage(oldPreferencePackage, preferencePackage);
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
