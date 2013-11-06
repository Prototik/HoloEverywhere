package org.holoeverywhere.issues.i681;

import android.view.Menu;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.i681_menu, menu);
        return true;
    }
}
