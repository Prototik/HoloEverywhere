package org.holoeverywhere.issues.i605;

import android.os.Bundle;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i605_main);
        findViewById(R.id.layout1).setSelected(true);
        findViewById(R.id.layout2).setSelected(true);
    }
}
