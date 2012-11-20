
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.preference.PreferenceFragment;

import android.os.Bundle;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        addPreferencesFromResource(R.xml.preferences);
    }
}
