
package org.holoeverywhere.demo.fragments.lists;

import android.view.View;

import org.holoeverywhere.widget.ListView;

public class ListsFastScrollLeftSideFragment extends ListsFastScrollFragment {
    @Override
    protected CharSequence getTitle() {
        return super.getTitle() + " (left)";
    }

    @Override
    protected void onPrepareList(ListView list) {
        list.setFastScrollAlwaysVisible(true);
        list.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
    }
}
