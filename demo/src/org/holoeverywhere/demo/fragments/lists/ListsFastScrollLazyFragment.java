
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.widget.ListView;

import android.view.View;

public class ListsFastScrollLazyFragment extends ListsFastScrollFragment {
    @Override
    protected CharSequence getTitle() {
        return super.getTitle() + ": Lazy";
    }

    @Override
    protected void onPrepareList(ListView list) {
        list.setFastScrollAlwaysVisible(false);
        list.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);
    }
}
