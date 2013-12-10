package org.holoeverywhere.issues.i720;

import android.os.Bundle;
import android.view.Window;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.drawable.ColorDrawable;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i720_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x40101010));
    }
}
