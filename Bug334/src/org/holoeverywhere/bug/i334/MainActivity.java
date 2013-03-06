
package org.holoeverywhere.bug.i334;

import org.holoeverywhere.app.Activity;

import com.actionbarsherlock.view.Menu;

public class MainActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Item");
        menu.addSubMenu(0, 0, 0, "Hello").getItem().setIcon(R.drawable.abs__ic_clear_holo_light);
        return true;
    }
}
