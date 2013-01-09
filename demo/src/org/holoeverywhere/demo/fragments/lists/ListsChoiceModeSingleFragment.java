
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ListView;

import android.view.View;
import android.widget.AbsListView;

public class ListsChoiceModeSingleFragment extends ListFragment {
    private ListView mList;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mList = getListView();
        mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.adjectives,
                R.layout.simple_list_item_single_choice));
    }
}
