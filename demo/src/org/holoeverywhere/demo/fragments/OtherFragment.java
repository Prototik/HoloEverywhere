
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.GridFragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class OtherFragment extends GridFragment {
    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.other, container, false);
    }
}
