
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.PopupMenu;
import org.holoeverywhere.widget.PopupMenu.OnMenuItemClickListener;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class OtherFragmentOld extends PreferenceFragment implements OnMenuItemClickListener {
    private static final class ContextMenuState {
        public static final int[] TOGGLE_ITEM_IDS = {
                R.id.item1,
                R.id.item2,
                R.id.item3
        };
        public boolean checkedItemState = true;
        public int selectedItemId = 0;
    }

    private final ContextMenuState contextMenuState = new ContextMenuState();

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String text;
        switch (item.getItemId()) {
            case R.id.item1:
                text = "Toggle to first item";
                contextMenuState.selectedItemId = 0;
                break;
            case R.id.item2:
                text = "Toggle to second item";
                contextMenuState.selectedItemId = 1;
                break;
            case R.id.item3:
                text = "Toggle to third item";
                contextMenuState.selectedItemId = 2;
                break;
            case R.id.item4:
                text = "Simple item";
                break;
            case R.id.item5:
                if (contextMenuState.checkedItemState = !contextMenuState.checkedItemState) {
                    text = "Item checked";
                } else {
                    text = "Item unchecked";
                }
                break;
            default:
                return super.onContextItemSelected(item);
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu, menu);
        prepareMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.other);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onContextItemSelected(item);
    }

    private void prepareMenu(Menu menu) {
        menu.findItem(ContextMenuState.TOGGLE_ITEM_IDS[contextMenuState.selectedItemId])
                .setChecked(true);
        menu.findItem(R.id.item5).setChecked(contextMenuState.checkedItemState);
    }

    public void showContextMenu(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
        unregisterForContextMenu(v);
    }

    public void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(getActivity(), v);
        menu.inflate(R.menu.menu);
        prepareMenu(menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

}
