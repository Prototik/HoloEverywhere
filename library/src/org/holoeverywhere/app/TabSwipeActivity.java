
package org.holoeverywhere.app;

import android.os.Bundle;

import org.holoeverywhere.addon.AddonTabber;
import org.holoeverywhere.addon.AddonTabber.AddonTabberA;
import org.holoeverywhere.addon.AddonTabber.AddonTabberCallback;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.TabSwipeController.TabInfo;

/**
 * This activity class implement tabs + swipe navigation pattern<br />
 * <br />
 * Part of HoloEverywhere
 */
@Addons(AddonTabber.class)
public abstract class TabSwipeActivity extends Activity
        implements TabSwipeInterface<TabInfo>, AddonTabberCallback {
    private AddonTabberA mTabber;

    protected AddonTabberA addonTabber() {
        if (mTabber == null) {
            mTabber = addon(AddonTabber.class);
        }
        return mTabber;
    }

    public TabSwipeActivity() {
        super();
        addonTabber();
    }

    @Override
    public int getCurrentTab() {
        return addonTabber().getCurrentTab();
    }

    @Override
    public void setCurrentTab(int position) {
        addonTabber().setCurrentTab(position);
    }

    @Override
    public TabInfo getTabAt(int position) {
        return addonTabber().getTabAt(position);
    }

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass) {
        return addonTabber().addTab(title, fragmentClass);
    }

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments) {
        return addonTabber().addTab(title, fragmentClass, fragmentArguments);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass) {
        return addonTabber().addTab(title, fragmentClass);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments) {
        return addonTabber().addTab(title, fragmentClass, fragmentArguments);
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo) {
        return addonTabber().addTab(tabInfo);
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo, int position) {
        return addonTabber().addTab(tabInfo, position);
    }

    @Override
    public OnTabSelectedListener getOnTabSelectedListener() {
        return addonTabber().getOnTabSelectedListener();
    }

    @Override
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        addonTabber().setOnTabSelectedListener(onTabSelectedListener);
    }

    @Override
    public boolean isSmoothScroll() {
        return addonTabber().isSmoothScroll();
    }

    @Override
    public void setSmoothScroll(boolean smoothScroll) {
        addonTabber().setSmoothScroll(smoothScroll);
    }

    @Override
    public boolean isSwipeEnabled() {
        return addonTabber().isSwipeEnabled();
    }

    @Override
    public void setSwipeEnabled(boolean swipeEnabled) {
        addonTabber().setSwipeEnabled(swipeEnabled);
    }

    @Override
    public void reloadTabs() {
        addonTabber().reloadTabs();
    }

    @Override
    public void removeAllTabs() {
        addonTabber().removeAllTabs();
    }

    @Override
    public TabInfo removeTab(int position) {
        return addonTabber().removeTab(position);
    }

    @Override
    public TabInfo removeTab(TabInfo tabInfo) {
        return addonTabber().removeTab(tabInfo);
    }
}
