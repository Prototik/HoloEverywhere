
package org.holoeverywhere.bug.i418;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListActivity;

import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.view.ContextMenu;

public class MainActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<CharSequence>(this, R.layout.simple_list_item_1,
                new CharSequence[] {
                        "First", "Second", "Third"
                }));
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo) menuInfo;
        menu.add("Item position: #" + adapterContextMenuInfo.position);
        menu.setHeaderTitle("ContextMenu");
    }
}
