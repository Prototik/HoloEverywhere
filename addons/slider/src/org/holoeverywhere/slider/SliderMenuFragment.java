package org.holoeverywhere.slider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.app.Fragment;

public class SliderMenuFragment extends Fragment {
    protected Context mMenuContext;

    private AddonSlider.AddonSliderA addonSlider() {
        return getSupportActivity().addon(AddonSlider.class);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (mMenuContext == null) {
            mMenuContext = addonSlider().getMenuContext();
        }
        if (mMenuContext == null) {
            mMenuContext = addonSlider().get();
        }
        return LayoutInflater.from(mMenuContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int menuLayout = addonSlider().getMenuLayout();
        return inflater.inflate(menuLayout != 0 ? menuLayout : R.layout.slider_default_menu_stub, container, false);
    }
}

