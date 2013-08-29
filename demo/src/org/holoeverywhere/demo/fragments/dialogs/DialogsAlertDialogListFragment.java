
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ArrayAdapter;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogsAlertDialogListFragment extends DialogsAlertDialogFragment {
    private static final OnClickListener LIST_CLICK_LISTENER = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    @Override
    protected void prepareBuilder(Builder builder) {
        builder.setAdapter(ArrayAdapter.createFromResource(getSupportActivity(), R.array.countries,
                R.layout.simple_list_item_1), LIST_CLICK_LISTENER);
    }
}
