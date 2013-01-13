
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ListView;

import android.view.View;
import android.widget.ListAdapter;

public class ListsFastScrollFragment extends ListsBaseFragment {
    private ListView mList;

    @Override
    protected CharSequence getTitle() {
        return "Lists: FastScroll";
    }

    protected ListAdapter onObtainData() {
        return ArrayAdapter.createFromResource(getActivity(), R.array.countries,
                R.layout.simple_list_item_1);
    }

    protected void onPrepareList(ListView list) {
        list.setFastScrollAlwaysVisible(true);
        list.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mList = getListView();
        mList.setFastScrollEnabled(true);
        onPrepareList(mList);
        setListAdapter(onObtainData());
        mList.setOnItemClickListener(null);
        mList.setClickable(false);
    }
}
