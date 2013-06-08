
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class FontPalette extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.font_palette, container, false);
    }
}
