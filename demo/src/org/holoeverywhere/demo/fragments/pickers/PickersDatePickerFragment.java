
package org.holoeverywhere.demo.fragments.pickers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.holoeverywhere.app.DatePickerDialog;
import org.holoeverywhere.app.DatePickerDialog.OnDateSetListener;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.DatePicker;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;

public class PickersDatePickerFragment extends DialogFragment implements OnDateSetListener {
    private static final Calendar CALENDAR = Calendar.getInstance();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d/M/y");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getSupportActivity(), getTheme(), this, 2012,
                Calendar.DECEMBER, 21);
    }

    @Override
    public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, monthOfYear);
        CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Toast.makeText(getSupportActivity(), "Set date: " + DATE_FORMAT.format(CALENDAR.getTime()),
                Toast.LENGTH_SHORT).show();
    }
}
