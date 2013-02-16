
package org.holoeverywhere.demo.test;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.test.app.TestActivity;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONObject;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class ActivityTest extends ActivityInstrumentationTestCase2<TestActivity> {
    public ActivityTest() {
        super(TestActivity.class);
    }

    private void testSharedPreferencesImpl(PreferenceImpl impl) {
        SharedPreferences prefs = getActivity().getDefaultSharedPreferences(impl);

        prefs.edit().putBoolean("key", true).commit();
        assertEquals(true, prefs.getBoolean("key", false));

        prefs.edit().putInt("key", 100500).commit();
        assertEquals(100500, prefs.getInt("key", -1));

        prefs.edit().putString("key", "I'll be back").commit();
        assertEquals("I'll be back", prefs.getString("key", ""));

        JSONObject json = TestUtils.makeRandomJson();
        prefs.edit().putJSONObject("key", json).commit();
        assertTrue(TestUtils.equals(json, prefs.getJSONObject("key", null)));
    }

    @MediumTest
    @UiThreadTest
    public void testSharedPreferencesJSON() {
        testSharedPreferencesImpl(PreferenceImpl.JSON);
    }

    @MediumTest
    @UiThreadTest
    public void testSharedPreferencesXML() {
        testSharedPreferencesImpl(PreferenceImpl.XML);
    }

    @SmallTest
    @UiThreadTest
    public void testSystemService() {
        assertTrue(getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) instanceof LayoutInflater);
    }
}
