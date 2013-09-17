package org.holoeverywhere.issues.i625;

import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import org.holoeverywhere.issues.R;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;


public class MainActivity extends ListActivity implements ListView.MultiChoiceModeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<CharSequence>(this, R.layout.i625_row,
                new CharSequence[]{"Item #1", "Item #2", "Item #3", "Item #4", "Item #5", "Item #6"}));
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle("Check something");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
