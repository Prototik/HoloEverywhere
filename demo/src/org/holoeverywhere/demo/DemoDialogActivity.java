
package org.holoeverywhere.demo;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DemoDialogActivity extends Activity {
    public static Intent obtainIntent(Context context, boolean onlyWhenLarge) {
        Intent intent = new Intent(context, DemoDialogActivity.class);
        intent.putExtra("onlyWhenLarge", onlyWhenLarge);
        return intent;
    }

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        /**
         * Disable flag HoloEverywhere.ALWAYS_USE_PARENT_THEME for this activity
         */
        ThemeManager.removeTheme(this);
        if (getIntent().getBooleanExtra("onlyWhenLarge", false)) {
            setTheme(ThemeManager.DIALOG_WHEN_LARGE | ThemeManager.NO_ACTION_BAR, false);
        } else {
            setTheme(ThemeManager.DIALOG, false);
        }
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.dialog_content);
    }
}
