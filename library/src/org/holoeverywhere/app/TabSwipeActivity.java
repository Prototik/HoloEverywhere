
package org.holoeverywhere.app;

import org.holoeverywhere.R;
import org.holoeverywhere.app.TabSwipeController.TabInfo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

/**
 * This activity class implement tabs + swipe navigation pattern<br />
 * <br />
 * Part of HoloEverywhere
 */
public abstract class TabSwipeActivity extends Activity implements TabSwipeInterface<TabInfo> {
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
        return new TabSwipeController(this, getSupportFragmentManager(), getSupportActionBar()) {
            @Override
            public void onHandleTabs() {
                TabSwipeActivity.this.onHandleTabs();
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
    public void onContentChanged() {
        super.onContentChanged();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabSwipePager);
        if (viewPager == null) {
            throw new IllegalStateException("Add ViewPager with id @+id/tabSwipePager to your "
                    + this + " fragment");
        }
        if (mController == null) {
            mController = createController();
        }
        mController.bind(viewPager);
    }

    protected abstract void onHandleTabs();

    @Override
    protected void onPostCreate(Bundle sSavedInstanceState) {
        if (!isDecorViewInited()) {
            setContentView(R.layout.tab_swipe);
        }
        super.onPostCreate(sSavedInstanceState);
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
