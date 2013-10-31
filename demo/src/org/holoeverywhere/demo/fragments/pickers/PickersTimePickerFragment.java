
package org.holoeverywhere.demo.fragments.pickers;

import android.os.Bundle;
import android.text.format.DateFormat;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.holoeverywhere.widget.Toast;

import java.util.Calendar;

public class PickersTimePickerFragment extends TimePickerDialog implements TimePickerDialog.OnTimeSetListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        initialize(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Toast.makeText(getSupportActivity(), String.format("Set time: %1$d:%2$d", hourOfDay, minute), Toast.LENGTH_SHORT).show();
    }
}
