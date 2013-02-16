
package org.holoeverywhere.demo.test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestUtils {
    private static final Comparator<JSONArray> JSON_ARRAY_COMPARATOR = new Comparator<JSONArray>() {
        @Override
        public int compare(JSONArray o1, JSONArray o2) {
            if (o1.length() != o2.length()) {
                return -1;
            }
            final int count = o1.length();
            for (int i = 0; i < count; i++) {
                if (different(o1.opt(i), o2.opt(i))) {
                    return -1;
                }
            }
            return 0;
        }
    };

    @SuppressWarnings("unchecked")
    private static final Comparator<JSONObject> JSON_OBJECT_COMPARATOR = new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            if (o1.length() != o2.length()) {
                return -1;
            }
            final Iterator<String> keys = o1.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (different(o1.opt(key), o2.opt(key))) {
                    return -1;
                }
            }
            return 0;
        }
    };

    public static boolean different(Object o1, Object o2) {
        return !equals(o1, o2);
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 instanceof JSONObject && o2 instanceof JSONObject) {
            return JSON_OBJECT_COMPARATOR.compare((JSONObject) o1, (JSONObject) o2) == 0;
        }
        if (o1 instanceof JSONArray && o2 instanceof JSONArray) {
            return JSON_ARRAY_COMPARATOR.compare((JSONArray) o1, (JSONArray) o2) == 0;
        }
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static JSONObject makeRandomJson() {
        try {
            Random random = new Random();
            JSONObject json = new JSONObject();
            final int size = random.nextInt(20) + 5;
            for (int i = 0; i < size; i++) {
                json.put("id:" + i, random.nextInt(100000) + 500);
            }
            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
