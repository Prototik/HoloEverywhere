
package org.holoeverywhere.demo;

import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Activity.Addons;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.fragments.MenuFragment;
import org.holoeverywhere.demo.fragments.MenuFragment.OnMenuClickListener;

import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

@Addons(Activity.ADDON_SLIDER)
public class DemoActivity extends Activity implements OnBackStackChangedListener {
    private MenuFragment mMenuFragment;
    private boolean mStaticMenu;

    public AddonSliderA addonSlider() {
        return addon(AddonSlider.class);
    }

    @Override
    public void onBackStackChanged() {
        if (mStaticMenu) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(
                    getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.library_name);

        final AddonSliderA slider = addonSlider();
        slider.setDrawerLayout(R.layout.content);

        if (slider.isAddonEnabled()) {
            // Phone
            mStaticMenu = false;
            actionbar.setDisplayHomeAsUpEnabled(true);
            slider.setOverlayActionBar(true);
        } else {
            // Tablet
            mStaticMenu = true;
        }

        mMenuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.leftView);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mStaticMenu && getSupportFragmentManager().getBackStackEntryCount() == 0) {
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if (isCreatedByThemeManager() && addonSlider().isAddonEnabled()) {
            addonSlider().openContentView();
        }
        super.onPostCreate(savedInstanceState);
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
        printViewHierarchy((ViewGroup) getWindow().getDecorView(), "");
    }

    public static void printViewHierarchy(ViewGroup vg, String prefix) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            String desc = prefix + "| " + "[" + i + "/" + (vg.getChildCount() - 1) + "] "
                    + v.getClass().getSimpleName() + " " + v.getId();
            Log.v("POPKA", desc);
            if (v instanceof ViewGroup) {
                printViewHierarchy((ViewGroup) v, "  " + prefix);
            }
        }
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        if (mMenuFragment != null) {
            mMenuFragment.setOnMenuClickListener(onMenuClickListener);
        }
    }
}
