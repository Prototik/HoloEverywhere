
package org.holoeverywhere.bug.i319;

import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.PreferenceActivity;

import android.os.Bundle;

@SuppressWarnings("deprecation")
public class MainActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().addPreference(createCheckbox());
    }

    private CheckBoxPreference createCheckbox() {
        CheckBoxPreference preference = new CheckBoxPreference(this);
        preference.setTitle("Created via code");
        return preference;
    }
}
