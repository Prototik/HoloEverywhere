package com.WazaBe.HoloEverywhere.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

import com.WazaBe.HoloEverywhere.app.Application;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public class _SharedPreferencesImpl_JSON implements SharedPreferences {
	private final class CouldNotCreateStorage extends RuntimeException {
		private static final long serialVersionUID = -8602981054023098742L;

		public CouldNotCreateStorage(File file, String message) {
			super("File \"" + file.getAbsolutePath() + "\": " + message);
		}
	}

	private final class EditorImpl implements Editor {
		private final List<FutureJSONManipulate> manipulate = new ArrayList<FutureJSONManipulate>();

		private void add(FutureJSONManipulate t) {
			manipulate.add(t);
		}

		@Override
		public void apply() {
			try {
				synchronized (data) {
					for (FutureJSONManipulate m : manipulate) {
						if (!m.onJSONManipulate(data)) {
							throw new RuntimeException(m.getClass()
									.getSimpleName() + ": Manipulate failed");
						}
					}
					saveDataToFile(file, data);
				}
				manipulate.clear();
			} catch (RuntimeException e) {
				manipulate.clear();
				throw e;
			}
		}

		@Override
		public Editor clear() {
			manipulate.clear();
			return this;
		}

		@Override
		public boolean commit() {
			try {
				apply();
				return true;
			} catch (RuntimeException e) {
				return false;
			}
		}

		@Override
		public Editor putBoolean(String key, boolean value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			add(new PutValueJSONManipulate(key, (double) value));
			return this;
		}

		@Override
		public Editor putFloatSet(String key, Set<Float> value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putIntSet(String key, Set<Integer> value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putJSONArray(String key, JSONArray value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putJSONObject(String key, JSONObject value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putLongSet(String key, Set<Long> value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putString(String key, String value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor putStringSet(String key, Set<String> value) {
			add(new PutValueJSONManipulate(key, value));
			return this;
		}

		@Override
		public Editor remove(String key) {
			add(new RemoveValueJSONManipulate(key));
			return this;
		}

	}

	private static interface FutureJSONManipulate {
		public boolean onJSONManipulate(JSONObject object);
	}

	private class PutValueJSONManipulate implements FutureJSONManipulate {
		private String key;
		private Object t;

		public PutValueJSONManipulate(String key, Object t) {
			this.key = key;
			this.t = t;
		}

		@Override
		public boolean onJSONManipulate(JSONObject object) {
			try {
				if (t instanceof Set) {
					t = new JSONArray((Set<?>) t);
				}
				object.put(key, t);
				notifyOnChange(key);
				return true;
			} catch (JSONException e) {
				return false;
			}
		}
	}

	private class RemoveValueJSONManipulate implements FutureJSONManipulate {
		private String key;

		public RemoveValueJSONManipulate(String key) {
			this.key = key;
		}

		@Override
		public boolean onJSONManipulate(JSONObject object) {
			if (object.has(key)) {
				object.remove(key);
				notifyOnChange(key);
				return true;
			} else {
				return false;
			}
		}
	}

	private static final Map<SharedPreferences, Set<OnSharedPreferenceChangeListener>> listeners = new HashMap<SharedPreferences, Set<OnSharedPreferenceChangeListener>>();
	private static final String TAG = _SharedPreferencesImpl_JSON.class
			.getSimpleName();
	private String charset;
	private final JSONObject data;
	private final boolean DEBUG = Application.isDebugMode();
	private File file;

	@SuppressLint("NewApi")
	public _SharedPreferencesImpl_JSON(Context context, String name, int mode) {
		setCharset("utf-8");
		try {
			File tempFile = new File(context.getApplicationInfo().dataDir
					+ "/shared_prefs");
			if (tempFile.exists()) {
				if (!tempFile.isDirectory()) {
					if (!tempFile.delete() && !tempFile.mkdirs()) {
						throw new CouldNotCreateStorage(tempFile,
								"Сann't create a storage for the preferences.");
					}
					tempFile.setWritable(true);
					tempFile.setReadable(true);
				}
			} else {
				if (!tempFile.mkdirs()) {
					throw new CouldNotCreateStorage(tempFile,
							"Сann't create a storage for the preferences.");
				}
				tempFile.setWritable(true);
				tempFile.setReadable(true);
			}
			tempFile = new File(tempFile, name + ".json");
			if (!tempFile.exists() && !tempFile.createNewFile()) {
				throw new CouldNotCreateStorage(tempFile,
						"Сann't create a storage for the preferences.");
			}
			if (VERSION.SDK_INT >= 9) {
				switch (mode) {
				case Context.MODE_WORLD_WRITEABLE:
					tempFile.setWritable(true, false);
					tempFile.setReadable(true, false);
					break;
				case Context.MODE_WORLD_READABLE:
					tempFile.setWritable(true, true);
					tempFile.setReadable(true, false);
					break;
				case Context.MODE_PRIVATE:
				default:
					tempFile.setWritable(true, true);
					tempFile.setReadable(true, true);
					break;
				}
			}
			file = tempFile;
			data = readDataFromFile(file);
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
	}

	@Override
	public boolean contains(String key) {
		synchronized (data) {
			return data.has(key);
		}
	}

	@Override
	public Editor edit() {
		return new EditorImpl();
	}

	@Override
	public Map<String, ?> getAll() {
		synchronized (data) {
			Map<String, Object> map = new HashMap<String, Object>(data.length());
			Iterator<?> i = data.keys();
			while (i.hasNext()) {
				Object o = i.next();
				String key = o instanceof String ? (String) o : o.toString();
				try {
					map.put(key, data.get(key));
				} catch (JSONException e) {
				}
			}
			return map;
		}
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return data.optBoolean(key, defValue);
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public float getFloat(String key, float defValue) {
		return (float) data.optDouble(key, defValue);
	}

	@Override
	public Set<Float> getFloatSet(String key, Set<Float> defValue) {
		return getSet(key, defValue);
	}

	@Override
	public int getInt(String key, int defValue) {
		return data.optInt(key, defValue);
	}

	@Override
	public Set<Integer> getIntSet(String key, Set<Integer> defValue) {
		return getSet(key, defValue);
	}

	@Override
	public JSONArray getJSONArray(String key, JSONArray defValue) {
		JSONArray a = data.optJSONArray(key);
		return a == null ? defValue : a;
	}

	@Override
	public JSONObject getJSONObject(String key, JSONObject defValue) {
		JSONObject a = data.optJSONObject(key);
		return a == null ? defValue : a;
	}

	@Override
	public long getLong(String key, long defValue) {
		return data.optLong(key, defValue);
	}

	@Override
	public Set<Long> getLongSet(String key, Set<Long> defValue) {
		return getSet(key, defValue);
	}

	@SuppressWarnings("unchecked")
	private <T> Set<T> getSet(String key, Set<T> defValue) {
		JSONArray a = data.optJSONArray(key);
		if (a == null) {
			return defValue;
		}
		Set<T> set = new HashSet<T>(Math.max(a.length(), 0));
		for (int i = 0; i < a.length(); i++) {
			set.add((T) a.opt(i));
		}
		return set;
	}

	@Override
	public String getString(String key, String defValue) {
		return data.optString(key, defValue);
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defValue) {
		return getSet(key, defValue);
	}

	public void notifyOnChange(String key) {
		synchronized (listeners) {
			if (!listeners.containsKey(this)) {
				return;
			}
			for (OnSharedPreferenceChangeListener listener : listeners
					.get(this)) {
				listener.onSharedPreferenceChanged(this, key);
			}
		}
	}

	protected JSONObject readDataFromFile(File file) {
		try {
			InputStream is = new FileInputStream(file);
			Reader reader;
			try {
				reader = new InputStreamReader(is, charset);
			} catch (UnsupportedEncodingException e) {
				if (DEBUG) {
					Log.w(TAG, "Encoding unsupport: " + charset);
				}
				reader = new InputStreamReader(is);
			}
			reader = new BufferedReader(reader, 1024);
			StringBuilder builder = new StringBuilder(Math.max(is.available(),
					0));
			char[] buffer = new char[8192];
			int c;
			while ((c = reader.read(buffer)) > 0) {
				builder.append(buffer, 0, c);
			}
			reader.close();
			is.close();
			return new JSONObject(builder.toString());
		} catch (Exception e) {
			return new JSONObject();
		}
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		synchronized (listeners) {
			if (!listeners.containsKey(this)) {
				listeners.put(this,
						new HashSet<OnSharedPreferenceChangeListener>());
			}
			Set<OnSharedPreferenceChangeListener> set = listeners.get(this);
			if (!set.contains(listener)) {
				set.add(listener);
			}
		}
	}

	public void saveDataToFile(File file, JSONObject data) {
		String s;
		if (DEBUG) {
			try {
				s = data.toString(2);
			} catch (JSONException e) {
				Log.e(TAG, "JSONException", e);
				s = data.toString();
			}
		} else {
			s = data.toString();
		}
		byte[] b;
		try {
			b = s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			b = s.getBytes();
		}
		try {
			OutputStream os = new FileOutputStream(file);
			os.write(b);
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new RuntimeException("IOException", e);
		}
	}

	public void setCharset(String charset) {
		if (charset == null || !Charset.isSupported(charset)) {
			throw new RuntimeException("Illegal charset: " + charset);
		}
		this.charset = charset;
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		synchronized (listeners) {
			if (listeners.containsKey(this)) {
				Set<OnSharedPreferenceChangeListener> set = listeners.get(this);
				if (set.contains(listener)) {
					set.remove(listener);
				}
				if (set.size() == 0) {
					listeners.remove(this);
				}
			}
		}
	}

}
