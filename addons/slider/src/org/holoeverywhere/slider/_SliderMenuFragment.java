
package org.holoeverywhere.slider;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.ListFragment;

public class _SliderMenuFragment extends ListFragment {
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
        if (menuLayout != 0) {
            return inflater.inflate(menuLayout, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
