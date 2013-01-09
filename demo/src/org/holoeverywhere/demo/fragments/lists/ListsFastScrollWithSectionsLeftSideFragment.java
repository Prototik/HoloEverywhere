
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.widget.ListView;

import android.view.View;

public class ListsFastScrollWithSectionsLeftSideFragment extends
        ListsFastScrollWithSectionsFragment {
    @Override
    protected void onPrepareList(ListView list) {
        list.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
    }
}
