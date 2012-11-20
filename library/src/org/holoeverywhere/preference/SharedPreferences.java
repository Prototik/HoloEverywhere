
package org.holoeverywhere.preference;

import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public interface SharedPreferences extends android.content.SharedPreferences {
    public static interface Editor extends
            android.content.SharedPreferences.Editor {
        @Override
        public void apply();

        @Override
        public Editor clear();

        @Override
        public boolean commit();

        @Override
        public Editor putBoolean(String key, boolean value);

        @Override
        public Editor putFloat(String key, float value);

        public Editor putFloatSet(String key, Set<Float> value);

        @Override
        public Editor putInt(String key, int value);

        public Editor putIntSet(String key, Set<Integer> value);

        public Editor putJSONArray(String key, JSONArray value);

        public Editor putJSONObject(String key, JSONObject value);

        @Override
        public Editor putLong(String key, long value);

        public Editor putLongSet(String key, Set<Long> value);

        @Override
        public Editor putString(String key, String value);

        @Override
        public Editor putStringSet(String key, Set<String> value);

        @Override
        public Editor remove(String key);
    }

    public static interface OnSharedPreferenceChangeListener {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key);
    }

    @Override
    public boolean contains(String key);

    @Override
    public Editor edit();

    @Override
    public Map<String, ?> getAll();

    @Override
    public boolean getBoolean(String key, boolean defValue);

    @Override
    public float getFloat(String key, float defValue);

    public Set<Float> getFloatSet(String key, Set<Float> defValue);

    @Override
    public int getInt(String key, int defValue);

    public Set<Integer> getIntSet(String key, Set<Integer> defValue);

    public JSONArray getJSONArray(String key, JSONArray defValue);

    public JSONObject getJSONObject(String key, JSONObject defValue);

    @Override
    public long getLong(String key, long defValue);

    public Set<Long> getLongSet(String key, Set<Long> defValue);

    @Override
    public String getString(String key, String defValue);

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue);

    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener);

    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener);
}
