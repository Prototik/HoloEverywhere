
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

public class ListsChoiceModeSingleFragment extends ListsBaseFragment {
    private ListView mList;

    @Override
    protected CharSequence getTitle() {
        return "Lists: Choice mode: Single";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = getListView();
        mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.adjectives,
                R.layout.simple_list_item_single_choice));
    }
}
