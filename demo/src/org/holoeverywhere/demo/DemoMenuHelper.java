
package org.holoeverywhere.demo;

import android.util.SparseBooleanArray;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class DemoMenuHelper {
    private static final int[] checkboxIds = {
            R.id.item4, R.id.item5
    };
    private static SparseBooleanArray checkboxState = new SparseBooleanArray(checkboxIds.length);
    private static final OnMenuItemClickListener LISTENER = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            final int id = menuItem.getItemId();
            for (int i : radioIds) {
                if (i == id) {
                    radioState = id;
                    return true;
                }
            }
            for (int i : checkboxIds) {
                if (i == id) {
                    checkboxState.put(id, !checkboxState.get(id, false));
                    return true;
                }
            }
            return false;
        }
    };
    private static final int[] radioIds = {
            R.id.item1, R.id.item2
    };
    private static int radioState = radioIds[0];

    public static void makeMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(LISTENER);
        }
        menu.findItem(radioState).setChecked(true);
        for (int i : checkboxIds) {
            menu.findItem(i).setChecked(checkboxState.get(i, false));
        }
    }
}
