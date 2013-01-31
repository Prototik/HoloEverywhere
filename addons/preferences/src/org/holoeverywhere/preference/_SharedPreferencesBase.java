
package org.holoeverywhere.preference;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

public abstract class _SharedPreferencesBase implements SharedPreferences {
    public abstract class _BaseEditor implements Editor {
        @Override
        public Editor putBoolean(int id, boolean value) {
            return putBoolean(makeNameById(id), value);
        }

        @Override
        public Editor putFloat(int id, float value) {
            return putFloat(makeNameById(id), value);
        }

        @Override
        public Editor putFloatSet(int id, Set<Float> value) {
            return putFloatSet(makeNameById(id), value);
        }

        @Override
        public Editor putInt(int id, int value) {
            return putInt(makeNameById(id), value);
        }

        @Override
        public Editor putIntSet(int id, Set<Integer> value) {
            return putIntSet(makeNameById(id), value);
        }

        @Override
        public Editor putJSONArray(int id, JSONArray value) {
            return putJSONArray(makeNameById(id), value);
        }

        @Override
        public Editor putJSONObject(int id, JSONObject value) {
            return putJSONObject(makeNameById(id), value);
        }

        @Override
        public Editor putLong(int id, long value) {
            return putLong(makeNameById(id), value);
        }

        @Override
        public Editor putLongSet(int id, Set<Long> value) {
            return putLongSet(makeNameById(id), value);
        }

        @Override
        public Editor putString(int id, String value) {
            return putString(makeNameById(id), value);
        }

        @Override
        public Editor putStringSet(int id, Set<String> value) {
            return putStringSet(makeNameById(id), value);
        }

        @Override
        public Editor remove(int id) {
            return remove(makeNameById(id));
        }
    }

    private Bundle bundle;

    @Override
    public boolean contains(int id) {
        return contains(makeNameById(id));
    }

    protected Bundle d() {
        if (bundle == null) {
            bundle = new Bundle();
        }
        return bundle;
    }

    @Override
    public boolean getBoolean(int id, boolean defValue) {
        return getBoolean(makeNameById(id), defValue);
    }

    @Override
    public float getFloat(int id, float defValue) {
        return getFloat(makeNameById(id), defValue);
    }

    @Override
    public Set<Float> getFloatSet(int id, Set<Float> defValue) {
        return getFloatSet(makeNameById(id), defValue);
    }

    @Override
    public int getInt(int id, int defValue) {
        return getInt(makeNameById(id), defValue);
    }

    @Override
    public Set<Integer> getIntSet(int id, Set<Integer> defValue) {
        return getIntSet(makeNameById(id), defValue);
    }

    @Override
    public JSONArray getJSONArray(int id, JSONArray defValue) {
        return getJSONArray(makeNameById(id), defValue);
    }

    @Override
    public JSONObject getJSONObject(int id, JSONObject defValue) {
        return getJSONObject(makeNameById(id), defValue);
    }

    @Override
    public long getLong(int id, long defValue) {
        return getLong(makeNameById(id), defValue);
    }

    @Override
    public Set<Long> getLongSet(int id, Set<Long> defValue) {
        return getLongSet(makeNameById(id), defValue);
    }

    @Override
    public String getString(int id, String defValue) {
        return getString(makeNameById(id), defValue);
    }

    @Override
    public Set<String> getStringSet(int id, Set<String> defValue) {
        return getStringSet(makeNameById(id), defValue);
    }

    @Override
    public String makeNameById(int id) {
        return PreferenceManager.makeNameById(id);
    }

    @Override
    public void setDefaultValues(Bundle bundle) {
        this.bundle = bundle;
    }
}
