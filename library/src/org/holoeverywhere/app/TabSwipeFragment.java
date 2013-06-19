
package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;
import org.holoeverywhere.app.TabSwipeController.TabInfo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment class implement tabs + swipe navigation pattern<br />
 * <br />
 * Part of HoloEverywhere
 */
public abstract class TabSwipeFragment extends Fragment implements TabSwipeInterface<TabInfo> {
    private TabSwipeController mController;

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass) {
        return mController.addTab(title, fragmentClass);
    }

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        return mController.addTab(title, fragmentClass, fragmentArguments);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass) {
        return mController.addTab(title, fragmentClass);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        return mController.addTab(title, fragmentClass, fragmentArguments);
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo) {
        return mController.addTab(tabInfo);
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo, int position) {
        return mController.addTab(tabInfo, position);
    }

    protected TabSwipeController createController() {
        return new TabSwipeController(getActivity(), getChildFragmentManager(),
                getSupportActionBar()) {
            @Override
            protected void onHandleTabs() {
                TabSwipeFragment.this.onHandleTabs();
            }
        };
    }

    @Override
    public OnTabSelectedListener getOnTabSelectedListener() {
        return mController.getOnTabSelectedListener();
    }

    @Override
    public boolean isSmoothScroll() {
        return mController.isSmoothScroll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_swipe, container, false);
    }

    @Override
    public void onDestroyView() {
        if (mController != null) {
            mController.onDestroyView();
        }
        super.onDestroyView();
    }

    protected abstract void onHandleTabs();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.tabSwipePager);
        if (viewPager == null) {
            throw new IllegalStateException("Add ViewPager with id @+id/tabSwipePager to your "
                    + this + " fragment");
        }
        mController = createController();
        mController.bind(viewPager);
    }

    @Override
    public void reloadTabs() {
        mController.reloadTabs();
    }

    @Override
    public void removeAllTabs() {
        mController.removeAllTabs();
    }

    @Override
    public TabInfo removeTab(int position) {
        return mController.removeTab(position);
    }

    @Override
    public TabInfo removeTab(TabInfo tabInfo) {
        return mController.removeTab(tabInfo);
    }

    @Override
    public void setCurrentTab(int position) {
        mController.setCurrentTab(position);
    }

    @Override
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mController.setOnTabSelectedListener(onTabSelectedListener);
    }

    @Override
    public void setSmoothScroll(boolean smoothScroll) {
        mController.setSmoothScroll(smoothScroll);
    }
}
