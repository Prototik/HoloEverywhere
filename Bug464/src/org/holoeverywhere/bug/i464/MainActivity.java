
package org.holoeverywhere.bug.i464;

import org.holoeverywhere.preference.PreferenceActivity;

import android.os.Bundle;

public class MainActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
