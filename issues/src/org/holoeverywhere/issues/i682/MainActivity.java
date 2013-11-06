package org.holoeverywhere.issues.i682;

import android.os.Bundle;

import org.holoeverywhere.issues.R;
import org.holoeverywhere.preference.PreferenceActivity;


public class MainActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.i682_prefs);
    }
}
