package org.holoeverywhere.issues.i796;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.issues.R;
import org.holoeverywhere.slider.SliderItem;
import org.holoeverywhere.slider.SliderMenu;
import org.holoeverywhere.widget.TextView;

@Addons(AddonSlider.class)
public class MainActivity extends Activity {
    public static final class DummyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.i796_fragment, container, false);
            TextView tv = (TextView) view.findViewById(android.R.id.text1);
            tv.setText(String.format("Group: %d\nChild: %d", getArguments().getInt("group"), getArguments().getInt("child")));
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AddonSlider.AddonSliderA slider = addon(AddonSlider.class);
        final SliderMenu menu = slider.obtainDefaultSliderMenu();

        SliderItem first = menu.add("First group", SliderMenu.RED).setIconAttr(R.attr.i796Icon1);
        first.add("First child", DummyFragment.class, make(0, 0));
        first.add("Second child", DummyFragment.class, make(0, 1));
        first.add("Third child", DummyFragment.class, make(0, 2));

        SliderItem second = menu.add("Second group", SliderMenu.GREEN).setIconAttr(R.attr.i796Icon2);
        second.add("First child", DummyFragment.class, make(1, 0));
        second.add("Second child", DummyFragment.class, make(1, 1));
        second.add("Third child", DummyFragment.class, make(1, 2));

        SliderItem third = menu.add("Third group", SliderMenu.BLUE).setIconAttr(R.attr.i796Icon3);
        third.add("First child", DummyFragment.class, make(2, 0));
        third.add("Second child", DummyFragment.class, make(2, 1));
        third.add("Third child", DummyFragment.class, make(2, 2));
    }

    private Bundle make(int group, int child) {
        final Bundle bundle = new Bundle();
        bundle.putInt("group", group);
        bundle.putInt("child", child);
        return bundle;
    }
}
