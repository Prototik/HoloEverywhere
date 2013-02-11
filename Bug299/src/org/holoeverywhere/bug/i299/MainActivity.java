
package org.holoeverywhere.bug.i299;

import java.util.List;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity {
    static {
        ThemeManager.setDefaultTheme(ThemeManager.MIXED);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    }
}
