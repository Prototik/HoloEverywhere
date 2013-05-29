
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.widget.DemoThemePicker;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment extends ListFragment {
    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getSupportActionBarContext());
    }

    @Override
    public DemoActivity getSupportActivity() {
        return (DemoActivity) super.getSupportActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DemoThemePicker) view.findViewById(R.id.themePicker)).setActivity(getSupportActivity());
    }
}
