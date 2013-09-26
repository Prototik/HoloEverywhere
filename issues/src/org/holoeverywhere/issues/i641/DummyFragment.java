package org.holoeverywhere.issues.i641;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

public class DummyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView view = new TextView(inflater.getContext());
        view.setText("Dummy content " + Integer.toHexString(hashCode()));
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
