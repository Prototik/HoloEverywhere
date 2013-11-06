
package org.holoeverywhere.demo.fragments.pickers;

import android.os.Bundle;

import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PickersDatePickerFragment extends DatePickerDialog implements DatePickerDialog.OnDateSetListener {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("d/M/y");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(this, 1996, Calendar.APRIL, 14);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Toast.makeText(getSupportActivity(), "Set date: " + DATE_FORMAT.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

    }
}
