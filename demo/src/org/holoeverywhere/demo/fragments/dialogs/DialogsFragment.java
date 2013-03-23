
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.demo.DemoDialogWhenLargeActivity;
import org.holoeverywhere.demo.fragments.OtherFragment;

public class DialogsFragment extends OtherFragment {
    @Override
    protected CharSequence getTitle() {
        return "Dialogs";
    }

    @Override
    protected void onHandleData() {
        addItem("Dialog", DialogsDialogFragment.class);
        addItemActivity("Dialog when large", DemoDialogWhenLargeActivity.class);
        addItem("AlertDialog", DialogsAlertDialogFragment.class);
        addItem("AlertDialog (with buttons)", DialogsAlertDialogWithButtonsFragment.class);
        addItem("AlertDialog List", DialogsAlertDialogListFragment.class);
        addItem("ProgressDialog", DialogsProgressDialogFragment.class);
        addItem("ProgressDialog (indeterminate)", DialogsProgressDialogIndeterminateFragment.class);
    }
}
