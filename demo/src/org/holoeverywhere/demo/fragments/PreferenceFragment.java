
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.sherlock.SPreferenceFragment;

import android.os.Bundle;

public class PreferenceFragment extends SPreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
