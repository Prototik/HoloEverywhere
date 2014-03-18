package org.holoeverywhere.issues.i632;

import android.os.Bundle;

import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;
import org.holoeverywhere.issues.i641.DummyFragment;
import org.holoeverywhere.slider.SliderMenu;

@Addons(AddonSlider.class)
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AddonSlider.AddonSliderA slider = addon(AddonSlider.class);
        final SliderMenu menu = slider.obtainDefaultSliderMenu();
        menu.setAppearanceFlags(0);
        menu.add("Item #1", DummyFragment.class).setCustomLayout(R.layout.i632_item_layout);
        menu.add("Item #2", DummyFragment.class).setCustomLayout(R.layout.i632_item_layout);
        menu.add("Item #3", DummyFragment.class).setCustomLayout(R.layout.i632_item_layout);
    }
}
