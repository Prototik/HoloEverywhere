
package org.holoeverywhere.demo.fragments.pickers;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.NumberPicker;

import android.os.Bundle;
import android.view.View;

public class PickersNumberPickerFragment extends DialogFragment {
    private View makeNumberPicker() {
        View view = getLayoutInflater().inflate(
                R.layout.number_picker_demo);
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(5);
        numberPicker.setMaxValue(15);
        numberPicker.setValue(10);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity(), getTheme());
        builder.setView(makeNumberPicker());
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }
}
