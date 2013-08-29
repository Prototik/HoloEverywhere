
package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;
import org.holoeverywhere.addon.AddonTabber;
import org.holoeverywhere.addon.AddonTabber.AddonTabberCallback;
import org.holoeverywhere.addon.AddonTabber.AddonTabberF;
import org.holoeverywhere.app.TabSwipeController.TabInfo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment class implement tabs + swipe navigation pattern<br />
 * <br />
 * Part of HoloEverywhere
 */
public abstract class TabSwipeFragment extends Fragment
        implements TabSwipeInterface<TabInfo>, AddonTabberCallback {
    private AddonTabberF mTabber;

    protected AddonTabberF addonTabber() {
        if (mTabber == null) {
            mTabber = addon(AddonTabber.class);
        }
        return mTabber;
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
    public boolean isSmoothScroll() {
        return addonTabber().isSmoothScroll();
    }

    @Override
    public boolean isSwipeEnabled() {
        return addonTabber().isSwipeEnabled();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        addonTabber();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_swipe, container, false);
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

    @Override
    public void setCurrentTab(int position) {
        addonTabber().setCurrentTab(position);
    }

    @Override
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        addonTabber().setOnTabSelectedListener(onTabSelectedListener);
    }

    @Override
    public void setSmoothScroll(boolean smoothScroll) {
        addonTabber().setSmoothScroll(smoothScroll);
    }

    @Override
    public void setSwipeEnabled(boolean swipeEnabled) {
        addonTabber().setSwipeEnabled(swipeEnabled);
    }
}
