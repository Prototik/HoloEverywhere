
package org.holoeverywhere.demo;

import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Activity.Addons;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.fragments.MenuFragment;
import org.holoeverywhere.demo.fragments.MenuFragment.OnMenuClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

@Addons(Activity.ADDON_SLIDER)
public class DemoActivity extends Activity implements OnBackStackChangedListener {
    private MenuFragment mMenuFragment;
    private boolean mStaticMenu;

    public AddonSliderA addonSlider() {
        return addon(AddonSlider.class);
    }

    private int computeMenuWidth() {
        return (int) getResources().getFraction(R.dimen.demo_menu_width,
                getResources().getDisplayMetrics().widthPixels, 1);
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

        setContentView(R.layout.content);

        final AddonSliderA slider = addonSlider();

        if (findViewById(R.id.customMenuFrame) == null) {
            // Phone
            mStaticMenu = false;
            actionbar.setDisplayHomeAsUpEnabled(true);
            slider.setLeftViewWidth(computeMenuWidth());
            slider.setDragWithActionBar(true);
        } else {
            // Tablet
            mStaticMenu = true;
            slider.setAddonEnabled(false);
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
        if (isCreatedByThemeManager()) {
            /**
             * Keep slider closed even if was be opened before activity restart
             */
            addonSlider().forceNotRestoreInstance();
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
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        if (mMenuFragment != null) {
            mMenuFragment.setOnMenuClickListener(onMenuClickListener);
        }
    }

    public void startDemoTabsActivity() {
        startActivity(new Intent(this, DemoTabsActivity.class));
    }
}
