
package org.holoeverywhere.bug.i299;

import org.holoeverywhere.preference.PreferenceFragment;

import android.os.Bundle;

public class Fragment2 extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen_2);
    }
}
