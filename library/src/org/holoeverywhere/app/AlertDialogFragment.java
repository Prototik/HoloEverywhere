
package org.holoeverywhere.app;

import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setDialogType(DialogType.AlertDialog);
        return super.onCreateDialog(savedInstanceState);
    }
}
