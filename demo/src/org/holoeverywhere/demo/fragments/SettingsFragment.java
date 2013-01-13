
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.preference.PreferenceFragment;

import android.os.Bundle;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setSubtitle("Settings");
    }
}
