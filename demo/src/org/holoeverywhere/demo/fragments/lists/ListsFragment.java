
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.demo.fragments.BaseOtherFragment;

public class ListsFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Modal", ListsModalFragment.class);
        addItem("Fast scroll", ListsFastScrollFragment.class);
    }
}
