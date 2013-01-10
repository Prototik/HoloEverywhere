
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.demo.fragments.BaseOtherFragment;

public class DialogsFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Dialog", DialogsDialogFragment.class);
        addItem("AlertDialog", DialogsAlertDialogFragment.class);
        addItem("AlertDialog (with buttons)", DialogsAlertDialogWithButtonsFragment.class);
        addItem("AlertDialog List", DialogsAlertDialogListFragment.class);
    }
}
