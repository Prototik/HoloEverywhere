
package org.holoeverywhere.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.holoeverywhere.IHoloActivity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

public final class _SharedPreferencesImpl_XML implements SharedPreferences {
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
                editor.putString(key,
                        setToString(value));
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

        @SuppressWarnings("unchecked")
        public static <T extends BaseOnSharedPreferenceChangeListener> T obtain(
                SharedPreferences prefs,
                OnSharedPreferenceChangeListener listener) {
            if (!BaseOnSharedPreferenceChangeListener.instances
                    .containsKey(listener)) {
                synchronized (BaseOnSharedPreferenceChangeListener.instances) {
                    if (!BaseOnSharedPreferenceChangeListener.instances
                            .containsKey(listener)) {
                        try {
                            BaseOnSharedPreferenceChangeListener.instances.put(
                                    listener,
                                    new BaseOnSharedPreferenceChangeListener(prefs, listener));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return (T) BaseOnSharedPreferenceChangeListener.instances
                    .get(listener);
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

    private static <T> Set<T> stringToSet(String string, Class<T> clazz) {
        try {
            JSONArray array = new JSONArray(string);
            Set<T> set = new HashSet<T>(array.length());
            for (int i = 0; i < array.length(); i++) {
                set.add(clazz.cast(array.opt(i)));
            }
            return set;
        } catch (ClassCastException e) {
            Log.e(_SharedPreferencesImpl_XML.class.getSimpleName(), "Error of cast", e);
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    private final android.content.SharedPreferences prefs;

    public _SharedPreferencesImpl_XML(Context context, String name, int mode) {
        if (context instanceof IHoloActivity) {
            prefs = ((IHoloActivity) context).superGetSharedPreferences(name,
                    mode);
        } else if (context instanceof Application) {
            prefs = ((Application) context).superGetSharedPreferences(name,
                    mode);
        } else {
            prefs = context.getSharedPreferences(name, mode);
        }
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
    public Set<Integer> getIntSet(String key, Set<Integer> defValue) {
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
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        registerOnSharedPreferenceChangeListener(BaseOnSharedPreferenceChangeListener
                .obtain(this, listener));
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        unregisterOnSharedPreferenceChangeListener(BaseOnSharedPreferenceChangeListener
                .obtain(this, listener));
    }
}
