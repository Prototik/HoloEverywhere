
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.demo.fragments.BaseOtherFragment;

public class ListsFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Choice mode: Modal", ListsChoiceModeModalFragment.class);
        addItem("Choice mode: Multiplue", ListsChoiceModeMultiplueFragment.class);
        addItem("Choice mode: Single", ListsChoiceModeSingleFragment.class);
        addItem("Fast scroll", ListsFastScrollFragment.class);
        addItem("Fast scroll (left)", ListsFastScrollLeftSideFragment.class);
        addItem("Fast scroll with sections", ListsFastScrollWithSectionsFragment.class);
        addItem("Fast scroll with sections (left)",
                ListsFastScrollWithSectionsLeftSideFragment.class);
    }
}
