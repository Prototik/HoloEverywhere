package com.WazaBe.HoloEverywhere.internal;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.util.Log;

import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public final class BaseSharedPreferences implements SharedPreferences {
	private static final class BaseEditor implements Editor {
		private android.content.SharedPreferences.Editor editor;

		public BaseEditor(android.content.SharedPreferences.Editor editor) {
			if (editor == null) {
				throw new IllegalArgumentException(
						"SharedPreferences.Editor can't be null");
			}
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
		public Editor putFloatSet(String key, Set<Float> value) {
			return putSet(key, value);
		}

		@Override
		public Editor putInt(String key, int value) {
			editor.putInt(key, value);
			return this;
		}

		@Override
		public Editor putIntSet(String key, Set<Integer> value) {
			return putSet(key, value);
		}

		@Override
		public Editor putJSONArray(String key, JSONArray value) {
			editor.putString(key, value.toString());
			return this;
		}

		@Override
		public Editor putJSONObject(String key, JSONObject value) {
			editor.putString(key, value.toString());
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			editor.putLong(key, value);
			return this;
		}

		@Override
		public Editor putLongSet(String key, Set<Long> value) {
			return putSet(key, value);
		}

		private Editor putSet(String key, Set<?> value) {
			editor.putString(key, setToString(value));
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

	private static String setToString(Set<?> set) {
		return new JSONArray(set).toString();
	}

	@SuppressWarnings("unchecked")
	private static <T> Set<T> stringToSet(String string, Class<T> clazz) {
		try {
			JSONArray array = new JSONArray(string);
			Set<T> set = new HashSet<T>(array.length());
			for (int i = 0; i < array.length(); i++) {
				set.add((T) array.getString(i));
			}
			return set;
		} catch (ClassCastException e) {
			Log.e("SupportSharedPreferences", "Error of cast", e);
			return null;
		} catch (JSONException e) {
			return null;
		}
	}

	private final android.content.SharedPreferences prefs;

	public BaseSharedPreferences(android.content.SharedPreferences prefs) {
		if (prefs == null) {
			throw new IllegalArgumentException(
					"SharedPreferences can't be null");
		}
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
	public Set<Float> getFloatSet(String key, Set<Float> defValue) {
		return getSet(key, defValue, Float.class);
	}

	@Override
	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	@Override
	public Set<Integer> getIntegerSet(String key, Set<Integer> defValue) {
		return getSet(key, defValue, Integer.class);
	}

	@Override
	public JSONArray getJSONArray(String key, JSONArray defValue) {
		String s = prefs.getString(key, null);
		try {
			return s == null ? defValue : new JSONArray(s);
		} catch (JSONException e) {
			return new JSONArray();
		}
	}

	@Override
	public JSONObject getJSONObject(String key, JSONObject defValue) {
		String s = prefs.getString(key, null);
		try {
			return s == null ? defValue : new JSONObject(s);
		} catch (JSONException e) {
			return new JSONObject();
		}
	}

	@Override
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	@Override
	public Set<Long> getLongSet(String key, Set<Long> defValue) {
		return getSet(key, defValue, Long.class);
	}

	@Override
	public android.content.SharedPreferences getPreferences() {
		return prefs;
	}

	private <T> Set<T> getSet(String key, Set<T> defValue, Class<T> clazz) {
		String s = prefs.getString(key, null);
		if (s == null) {
			return defValue;
		} else {
			return stringToSet(s, clazz);
		}
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
			return getSet(key, defValue, String.class);
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
