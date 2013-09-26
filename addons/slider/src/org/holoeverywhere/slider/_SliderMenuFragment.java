
package org.holoeverywhere.slider;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Fragment;

public class _SliderMenuFragment extends Fragment {
    private AddonSliderA addonSlider() {
        return getSupportActivity().addon(AddonSlider.class);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(addonSlider().getMenuContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int menuLayout = addonSlider().getMenuLayout();
        return inflater.inflate(menuLayout != 0 ? menuLayout : R.layout.slider_default_list_layout, container, false);
    }
}
