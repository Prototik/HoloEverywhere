
package org.holoeverywhere.bug.i299;

import java.util.List;

import org.holoeverywhere.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    }
}
