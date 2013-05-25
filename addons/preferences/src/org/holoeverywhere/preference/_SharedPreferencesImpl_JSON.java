
package org.holoeverywhere.preference;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.holoeverywhere.HoloEverywhere;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

public class _SharedPreferencesImpl_JSON extends _SharedPreferencesBase {
    private class ClearJSONManipulate implements FutureJSONManipulate {
        @Override
        public boolean onJSONManipulate(JSONObject object) {
            @SuppressWarnings("unchecked")
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                object.remove(key);
                notifyOnChange(key);
            }
            return true;
        }
    }

    private final class CouldNotCreateStorage extends RuntimeException {
        private static final long serialVersionUID = -8602981054023098742L;

        public CouldNotCreateStorage(File file, String message) {
            super("File \"" + file.getAbsolutePath() + "\": " + message);
        }
    }

    private final class EditorImpl extends _BaseEditor {
        private final List<FutureJSONManipulate> manipulate = new ArrayList<FutureJSONManipulate>();

        private EditorImpl add(FutureJSONManipulate t) {
            manipulate.add(t);
            return this;
        }

        @Override
        public void apply() {
            JSONObject data = getData();
            synchronized (data) {
                try {
                    for (FutureJSONManipulate m : manipulate) {
                        if (!m.onJSONManipulate(data)) {
                            throw new RuntimeException(m.getClass()
                                    .getSimpleName() + ": Manipulate failed");
                        }
                    }
                    saveDataToFile(file, data);
                } catch (Exception e) {
                    Log.e(TAG, "Error while save preferences data", e);
                } finally {
                    manipulate.clear();
                }
            }
        }

        @Override
        public Editor clear() {
            return add(new ClearJSONManipulate());
        }

        @Override
        public boolean commit() {
            try {
                apply();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putFloat(String key, float value) {
            return add(new PutValueJSONManipulate(key, (double) value));
        }

        @Override
        public Editor putFloatSet(String key, Set<Float> value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putInt(String key, int value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putIntSet(String key, Set<Integer> value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putJSONArray(String key, JSONArray value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putJSONObject(String key, JSONObject value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putLong(String key, long value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putLongSet(String key, Set<Long> value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putString(String key, String value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor putStringSet(String key, Set<String> value) {
            return add(new PutValueJSONManipulate(key, value));
        }

        @Override
        public Editor remove(String key) {
            return add(new RemoveValueJSONManipulate(key));
        }

    }

    private static interface FutureJSONManipulate {
        public boolean onJSONManipulate(JSONObject object);
    }

    private static final class ImplReference {
        private JSONObject data;
        private Set<OnSharedPreferenceChangeListener> listeners;
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

    private static final Map<String, ImplReference> refs = new HashMap<String, ImplReference>();
    private static final String TAG = _SharedPreferencesImpl_JSON.class.getSimpleName();
    private String charset;
    private File file;
    private final String fileTag;

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
                    if (VERSION.SDK_INT >= 9) {
                        tempFile.setWritable(true);
                        tempFile.setReadable(true);
                    }
                }
            } else {
                if (!tempFile.mkdirs()) {
                    throw new CouldNotCreateStorage(tempFile,
                            "Сann't create a storage for the preferences.");
                }
                if (VERSION.SDK_INT >= 9) {
                    tempFile.setWritable(true);
                    tempFile.setReadable(true);
                }
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
            fileTag = file.getAbsolutePath().intern();
            if (getReference().data == null) {
                getReference().data = readDataFromFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    @Override
    public synchronized boolean contains(String key) {
        return getData().has(key);
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    @Override
    public synchronized Map<String, ?> getAll() {
        Map<String, Object> map = new HashMap<String, Object>(getData()
                .length());
        Iterator<?> i = getData().keys();
        while (i.hasNext()) {
            Object o = i.next();
            String key = o instanceof String ? (String) o : o.toString();
            try {
                map.put(key, getData().get(key));
            } catch (JSONException e) {
            }
        }
        return map;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return getData().optBoolean(key, d().getBoolean(key, defValue));
    }

    public String getCharset() {
        return charset;
    }

    protected JSONObject getData() {
        return getReference().data;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) getData().optDouble(key, d().getFloat(key, defValue));
    }

    @Override
    public Set<Float> getFloatSet(String key, Set<Float> defValue) {
        return getSet(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return getData().optInt(key, d().getInt(key, defValue));
    }

    @Override
    public Set<Integer> getIntSet(String key, Set<Integer> defValue) {
        return getSet(key, defValue);
    }

    @Override
    public JSONArray getJSONArray(String key, JSONArray defValue) {
        JSONArray a = getData().optJSONArray(key);
        return a == null ? defValue : a;
    }

    @Override
    public JSONObject getJSONObject(String key, JSONObject defValue) {
        JSONObject a = getData().optJSONObject(key);
        return a == null ? defValue : a;
    }

    @Override
    public long getLong(String key, long defValue) {
        return getData().optLong(key, d().getLong(key, defValue));
    }

    @Override
    public Set<Long> getLongSet(String key, Set<Long> defValue) {
        return getSet(key, defValue);
    }

    protected synchronized ImplReference getReference() {
        ImplReference ref = refs.get(fileTag);
        if (ref == null) {
            ref = new ImplReference();
            refs.put(fileTag, ref);
        }
        return ref;
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> getSet(String key, Set<T> defValue) {
        JSONArray a = getData().optJSONArray(key);
        if (a == null) {
            try {
                Object o = d().get(key);
                if (o != null) {
                    return new HashSet<T>(Arrays.asList((T[]) o));
                }
            } catch (Exception e) {
            }
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
        String defValue2 = d().getString(key);
        return getData().optString(key, defValue2 == null ? defValue : defValue2);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return getSet(key, defValue);
    }

    public void notifyOnChange(String key) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            return;
        }
        synchronized (listeners) {
            for (OnSharedPreferenceChangeListener listener : listeners) {
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
                if (HoloEverywhere.DEBUG) {
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
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException(
                "android.content.SharedPreferences.OnSharedPreferenceChangeListener don't supported on JSON impl");
    }

    @Override
    public synchronized void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            getReference().listeners = listeners = new HashSet<SharedPreferences.OnSharedPreferenceChangeListener>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void saveDataToFile(File file, JSONObject data) {
        String s;
        if (HoloEverywhere.DEBUG) {
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
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException(
                "android.content.SharedPreferences.OnSharedPreferenceChangeListener don't supported on JSON impl");
    }

    @Override
    public synchronized void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            return;
        }
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
        if (listeners.size() == 0) {
            getReference().listeners = null;
        }
    }
}
