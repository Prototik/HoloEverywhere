
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.app.AlertDialog.Builder;

public class DialogsAlertDialogWithButtonsFragment extends DialogsAlertDialogFragment {
    @Override
    protected void prepareBuilder(Builder builder) {
        super.prepareBuilder(builder);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);
    }
}
