package org.holoeverywhere.issues.i723;

import android.os.Bundle;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Holo_Issues_I723_Theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i723_main);
    }
}
