
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;

public class DialogsAlertDialogFragment extends DialogFragment {
    public DialogsAlertDialogFragment() {
        setDialogType(DialogType.AlertDialog);
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setTitle(R.string.library_name);
        builder.setIcon(R.drawable.icon);
        prepareBuilder(builder);
        return builder.create();
    }

    protected void prepareBuilder(Builder builder) {
        builder.setMessage("Hello, Dialog!");
    }
}
