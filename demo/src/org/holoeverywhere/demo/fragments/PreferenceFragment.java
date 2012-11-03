
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.demo.R;

import android.os.Bundle;

public class PreferenceFragment extends org.holoeverywhere.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
