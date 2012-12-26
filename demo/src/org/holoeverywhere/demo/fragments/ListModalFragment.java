
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ListView.MultiChoiceModeListener;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;

public class ListModalFragment extends ListFragment implements MultiChoiceModeListener {
    private static ListModalFragment instance;

    public static ListModalFragment getInstance() {
        if (ListModalFragment.instance == null) {
            return new ListModalFragment();
        }
        return ListModalFragment.instance;
    }

    private ListView mList;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mList = getListView();
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(this);
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.adjectives,
                R.layout.simple_list_item_multiple_choice));
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.inverse:
                final int count = mList.getCount();
                for (int i = 0; i < count; i++) {
                    mList.setItemChecked(i, !mList.isItemChecked(i));
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle(R.string.library_name);
        getMenuInflater().inflate(R.menu.modal, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        mode.setSubtitle("Checked: " + mList.getCheckedItemCount());
    }
}
