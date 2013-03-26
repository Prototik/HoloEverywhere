
package org.holoeverywhere.demo;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;

import android.os.Bundle;

public class DemoDialogWhenLargeActivity extends Activity {
    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        setTheme(ThemeManager.DARK | ThemeManager.DIALOG | ThemeManager.NO_ACTION_BAR, false);
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.dialog_content);
    }
}
