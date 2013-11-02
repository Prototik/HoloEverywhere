package org.holoeverywhere.issues.i655;

import android.view.Menu;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.i655_menu, menu);
        return true;
    }
}
