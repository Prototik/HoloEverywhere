package com.WazaBe.HoloEverywhere.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;

import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public class BaseSharedPreferences implements SharedPreferences {
	public static class BaseEditor implements Editor {
		private android.content.SharedPreferences.Editor editor;

		public BaseEditor(android.content.SharedPreferences.Editor editor) {
			this.editor = editor;
		}

		@Override
		@SuppressLint("NewApi")
		public void apply() {
			if (VERSION.SDK_INT >= 9) {
				editor.apply();
			} else {
				editor.commit();
			}
		}

		@Override
		public Editor clear() {
			editor.clear();
			return this;
		}

		@Override
		public boolean commit() {
			return editor.commit();
		}

		@Override
		public Editor putBoolean(String key, boolean value) {
			editor.putBoolean(key, value);
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			editor.putFloat(key, value);
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			editor.putFloat(key, value);
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			editor.putLong(key, value);
			return this;
		}

		@Override
		public Editor putString(String key, String value) {
			editor.putString(key, value);
			return this;
		}

		@Override
		@SuppressLint("NewApi")
		public Editor putStringSet(String key, Set<String> value) {
			if (VERSION.SDK_INT >= 11) {
				editor.putStringSet(key, value);
			} else {
				editor.putString(key, setToString(value));
			}
			return this;
		}

		@Override
		public Editor remove(String key) {
			editor.remove(key);
			return this;
		}

	}

	public static class BaseOnSharedPreferenceChangeListener implements
			android.content.SharedPreferences.OnSharedPreferenceChangeListener {
		private static final Map<OnSharedPreferenceChangeListener, BaseOnSharedPreferenceChangeListener> instances = new HashMap<SharedPreferences.OnSharedPreferenceChangeListener, BaseOnSharedPreferenceChangeListener>();

		public static BaseOnSharedPreferenceChangeListener obtain(
				SharedPreferences prefs,
				OnSharedPreferenceChangeListener listener) {
			return obtain(prefs, listener,
					BaseOnSharedPreferenceChangeListener.class);
		}

		@SuppressWarnings("unchecked")
		public static <T extends BaseOnSharedPreferenceChangeListener> T obtain(
				SharedPreferences prefs,
				OnSharedPreferenceChangeListener listener, Class<T> clazz) {
			if (!instances.containsKey(listener)) {
				synchronized (instances) {
					if (!instances.containsKey(listener)) {
						try {
							Constructor<T> constructor = clazz.getConstructor(
									SharedPreferences.class,
									OnSharedPreferenceChangeListener.class);
							constructor.setAccessible(true);
							instances.put(listener,
									constructor.newInstance(prefs, listener));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return (T) instances.get(listener);
		}

		private OnSharedPreferenceChangeListener listener;

		private SharedPreferences prefs;

		public BaseOnSharedPreferenceChangeListener(SharedPreferences prefs,
				OnSharedPreferenceChangeListener listener) {
			this.prefs = prefs;
			this.listener = listener;
		}

		@Override
		public void onSharedPreferenceChanged(
				android.content.SharedPreferences sharedPreferences, String key) {
			listener.onSharedPreferenceChanged(prefs, key);
		}
	}

	public static String setToString(Set<String> set) {
		return new JSONArray(set).toString();
	}

	public static Set<String> stringToSet(String string) {
		try {
			JSONArray array = new JSONArray(string);
			Set<String> set = new HashSet<String>(array.length());
			for (int i = 0; i < array.length(); i++) {
				set.add(array.getString(i));
			}
			return set;
		} catch (JSONException e) {
			return null;
		}
	}

	private final android.content.SharedPreferences prefs;

	public BaseSharedPreferences(android.content.SharedPreferences prefs) {
		this.prefs = prefs;
	}

	@Override
	public boolean contains(String key) {
		return prefs.contains(key);
	}

	@Override
	public Editor edit() {
		return new BaseEditor(prefs.edit());
	}

	@Override
	public Map<String, ?> getAll() {
		return prefs.getAll();
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	@Override
	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}

	@Override
	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	@Override
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	@Override
	public android.content.SharedPreferences getPreferences() {
		return prefs;
	}

	@Override
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	@SuppressLint("NewApi")
	@Override
	public Set<String> getStringSet(String key, Set<String> defValue) {
		if (VERSION.SDK_INT >= 11) {
			return prefs.getStringSet(key, defValue);
		} else {
			String s = prefs.getString(key, null);
			if (s == null) {
				return defValue;
			} else {
				return stringToSet(s);
			}
		}
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		prefs.registerOnSharedPreferenceChangeListener(BaseOnSharedPreferenceChangeListener
				.obtain(this, listener));
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		prefs.unregisterOnSharedPreferenceChangeListener(BaseOnSharedPreferenceChangeListener
				.obtain(this, listener));
	}

}
