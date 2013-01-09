
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;

public class DialogsDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.dialog_content);
        return dialog;
    }
}
