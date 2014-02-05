package org.holoeverywhere.issues.i780;

import android.os.Bundle;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;

public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeManager.DIALOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i780_dialog);
    }
}
