
package org.holoeverywhere.demo.test;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.test.app.TestApplication;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONObject;

import android.content.Context;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class ApplicationTest extends ApplicationTestCase<TestApplication> {
    public ApplicationTest() {
        super(TestApplication.class);
    }

    @Override
    public TestApplication getApplication() {
        TestApplication application = super.getApplication();
        if (application == null) {
            createApplication();
            application = super.getApplication();
        }
        return application;
    }

    private void testSharedPreferencesImpl(PreferenceImpl impl) {
        SharedPreferences prefs = getApplication().getDefaultSharedPreferences(impl);

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
    public void testSharedPreferencesJSON() {
        testSharedPreferencesImpl(PreferenceImpl.JSON);
    }

    @MediumTest
    public void testSharedPreferencesXML() {
        testSharedPreferencesImpl(PreferenceImpl.XML);
    }

    @SmallTest
    public void testSystemService() {
        assertTrue(getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE) instanceof LayoutInflater);
    }
}
