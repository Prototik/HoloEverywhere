
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.ListView.MultiChoiceModeListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ListsChoiceModeModalFragment extends ListsBaseFragment implements
        MultiChoiceModeListener {
    private ActionMode mLastActionMode;
    private ListView mList;

    @Override
    protected CharSequence getTitle() {
        return "Lists: Choice mode: Modal";
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        mLastActionMode = mode;
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
        mLastActionMode = mode;
        mode.setTitle(R.string.library_name);
        updateSubtitle(mode);
        getMenuInflater().inflate(R.menu.lists_choice_mode_modal, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        if (mLastActionMode != null) {
            mLastActionMode.finish();
            mLastActionMode = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mLastActionMode = null;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        updateSubtitle(mLastActionMode = mode);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mLastActionMode = mode;
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = getListView();
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(this);
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.adjectives,
                R.layout.simple_list_item_multiple_choice));
    }

    @SuppressLint("NewApi")
    private void updateSubtitle(ActionMode mode) {
        mode.setSubtitle("Checked: " + mList.getCheckedItemCount());
    }
}
