
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.widget.ListView;

import android.view.View;

public class ListsFastScrollLeftSideFragment extends ListsFastScrollFragment {
    @Override
    protected void onPrepareList(ListView list) {
        list.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
    }
}
