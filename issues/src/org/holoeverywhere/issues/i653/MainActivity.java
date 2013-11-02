package org.holoeverywhere.issues.i653;

import android.os.Bundle;
import android.view.Menu;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.i653_menu, menu);
        return true;
    }
}
