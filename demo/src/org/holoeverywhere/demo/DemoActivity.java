
package org.holoeverywhere.demo;

import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Activity.Addons;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.fragments.MainFragment;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.demo.fragments.SettingsFragment;
import org.holoeverywhere.demo.fragments.about.AboutFragment;
import org.holoeverywhere.slider.SliderMenu;

import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.MenuItem;

@Addons(Activity.ADDON_SLIDER)
public class DemoActivity extends Activity implements OnBackStackChangedListener {
    public AddonSliderA addonSlider() {
        return addon(AddonSlider.class);
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(addonSlider().isAddonEnabled() ? true :
                getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AddonSliderA slider = addonSlider();
        slider.setDrawerLayout(R.layout.content);
        slider.setOverlayActionBar(true);

        final SliderMenu sliderMenu = slider.obtainSliderMenu();

        sliderMenu.add(R.string.demo, MainFragment.class, SliderMenu.BLUE);
        sliderMenu.add(R.string.settings, SettingsFragment.class, SliderMenu.GREEN);
        sliderMenu.add(R.string.other, OtherFragment.class, SliderMenu.ORANGE);
        sliderMenu.add(R.string.about, AboutFragment.class, SliderMenu.PURPLE);

        sliderMenu.makeDefaultMenu(getSupportActionBarContext());

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        onBackStackChanged();

        getSupportActionBar().setTitle(R.string.library_name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (addonSlider().isAddonEnabled()
                        && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    addonSlider().toggle();
                } else {
                    onBackPressed();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    public void replaceFragment(Fragment fragment,
            String backStackName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentView, fragment);
        if (backStackName != null) {
            ft.addToBackStack(backStackName);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
