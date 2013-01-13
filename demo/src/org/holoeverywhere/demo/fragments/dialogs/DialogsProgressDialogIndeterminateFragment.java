
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.demo.R;

import android.os.Bundle;

public class DialogsProgressDialogIndeterminateFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getSupportActivity(), getTheme());
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getText(R.string.very_very_long_operation));
        return dialog;
    }
}
