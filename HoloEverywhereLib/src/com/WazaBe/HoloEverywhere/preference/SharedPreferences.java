package com.WazaBe.HoloEverywhere.preference;

import java.util.Map;
import java.util.Set;

public interface SharedPreferences {
	public static interface Editor {
		public void apply();

		public Editor clear();

		public boolean commit();

		public Editor putBoolean(String key, boolean value);

		public Editor putFloat(String key, float value);

		public Editor putInt(String key, int value);

		public Editor putLong(String key, long value);

		public Editor putString(String key, String value);

		public Editor putStringSet(String key, Set<String> value);

		public Editor remove(String key);
	}

	public static interface OnSharedPreferenceChangeListener {
		void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
				String key);
	}

	public boolean contains(String key);

	public Editor edit();

	public Map<String, ?> getAll();

	public boolean getBoolean(String key, boolean defValue);

	public float getFloat(String key, float defValue);

	public int getInt(String key, int defValue);

	public long getLong(String key, long defValue);

	public android.content.SharedPreferences getPreferences();

	public String getString(String key, String defValue);

	public Set<String> getStringSet(String key, Set<String> defValue);

	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener);

	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener);
}
