
package org.holoeverywhere.demo.fragments.dialogs;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class DialogsDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle("HoloEverywhere: Dialog");
        return inflater.inflate(R.layout.dialog_content, container, false);
    }
}
