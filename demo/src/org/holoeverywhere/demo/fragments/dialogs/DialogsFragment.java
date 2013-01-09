
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.demo.fragments.BaseOtherFragment;

public class DialogsFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Dialog", DialogsDialogFragment.class);
    }
}
