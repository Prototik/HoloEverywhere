
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ListView;

import android.view.View;

public class ListsFastScrollFragment extends ListFragment {
    private ListView mList;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mList = getListView();
        mList.setFastScrollEnabled(true);
        mList.setFastScrollAlwaysVisible(true);
        mList.setCropDividersByScroller(true);
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.countries,
                R.layout.simple_list_item_1));
    }
}
