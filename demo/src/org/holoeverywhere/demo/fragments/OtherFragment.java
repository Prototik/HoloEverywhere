
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.demo.fragments.dialogs.DialogsFragment;
import org.holoeverywhere.demo.fragments.lists.ListsFragment;

public class OtherFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Lists", ListsFragment.class);
        addItem("Dialogs", DialogsFragment.class);
    }
}
