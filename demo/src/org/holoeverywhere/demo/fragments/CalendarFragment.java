
package org.holoeverywhere.demo.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.R;

public class CalendarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setSubtitle("Calendar");
    }
}
