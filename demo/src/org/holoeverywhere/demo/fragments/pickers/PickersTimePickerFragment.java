
package org.holoeverywhere.demo.fragments.pickers;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.app.TimePickerDialog.OnTimeSetListener;
import org.holoeverywhere.widget.TimePicker;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;

public class PickersTimePickerFragment extends DialogFragment implements OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getSupportActivity(), getTheme(), this, 12, 34, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        Toast.makeText(getSupportActivity(),
                String.format("Set time: %1$d:%2$d", hours, minutes), Toast.LENGTH_SHORT)
                .show();
    }
}
