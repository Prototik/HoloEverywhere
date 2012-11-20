
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
    private static MainFragment instance;

    public static MainFragment getInstance() {
        if (MainFragment.instance == null) {
            return new MainFragment();
        }
        return MainFragment.instance;
    }

    public MainFragment() {
        MainFragment.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main);
    }

}
