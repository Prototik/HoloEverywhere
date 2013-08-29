
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.demo.DemoDialogActivity;
import org.holoeverywhere.demo.fragments.OtherFragment;

public class DialogsFragment extends OtherFragment {
    @Override
    protected CharSequence getTitle() {
        return "Dialogs";
    }

    @Override
    protected void onHandleData() {
        addItem("Dialog", DialogsDialogFragment.class);
        addItem("Dialog activity", DemoDialogActivity.obtainIntent(getSupportActivity(), false));
        addItem("Dialog when large", DemoDialogActivity.obtainIntent(getSupportActivity(), true));
        addItem("AlertDialog", DialogsAlertDialogFragment.class);
        addItem("AlertDialog (with buttons)", DialogsAlertDialogWithButtonsFragment.class);
        addItem("AlertDialog List", DialogsAlertDialogListFragment.class);
        addItem("ProgressDialog", DialogsProgressDialogFragment.class);
        addItem("ProgressDialog (indeterminate)", DialogsProgressDialogIndeterminateFragment.class);
    }
}
