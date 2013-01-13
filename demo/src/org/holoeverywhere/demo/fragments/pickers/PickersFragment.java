
package org.holoeverywhere.demo.fragments.pickers;

import org.holoeverywhere.demo.fragments.OtherFragment;

public class PickersFragment extends OtherFragment {
    @Override
    protected CharSequence getTitle() {
        return "Pickers";
    }

    @Override
    protected void onHandleData() {
        addItem("NumberPicker", PickersNumberPickerFragment.class);
        addItem("TimePicker", PickersTimePickerFragment.class);
        addItem("DatePicker", PickersDatePickerFragment.class);
    }
}
