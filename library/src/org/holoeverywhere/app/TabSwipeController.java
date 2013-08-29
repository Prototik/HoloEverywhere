
package org.holoeverywhere.app;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.TabSwipeController.TabInfo;
import org.holoeverywhere.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

public abstract class TabSwipeController implements TabSwipeInterface<TabInfo> {
    public static class TabInfo implements TabSwipeInterface.ITabInfo<TabInfo> {
        private Bundle mFragmentArguments;
        private Class<? extends Fragment> mFragmentClass;
        private CharSequence mTitle;

        @Override
        public Bundle getFragmentArguments() {
            return mFragmentArguments;
        }

        @Override
        public Class<? extends Fragment> getFragmentClass() {
            return mFragmentClass;
        }

        @Override
        public CharSequence getTitle() {
            return mTitle;
        }

        @Override
        public TabInfo setFragmentArguments(Bundle fragmentArguments) {
            mFragmentArguments = fragmentArguments;
            return this;
        }

        @Override
        public TabInfo setFragmentClass(Class<? extends Fragment> fragmentClass) {
            mFragmentClass = fragmentClass;
            return this;
        }

        @Override
        public TabInfo setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }
    }

    private final class TabSwipeAdapter extends FragmentStatePagerAdapter implements
            OnPageChangeListener, TabListener {
        public TabSwipeAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            final TabInfo info = mTabs.get(position);
            return Fragment.instantiate(info.mFragmentClass, info.mFragmentArguments);
        }

        @Override
        public void onPageScrolled(int position, float percent, int pixels) {
            // Do nothing
        }

        @Override
        public void onPageScrollStateChanged(int scrollState) {
            // Do nothing
        }

        @Override
        public void onPageSelected(int position) {
            dispatchTabSelected(position);
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // Do nothing
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            dispatchTabSelected(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // Do nothing
        }
    }

    private final ActionBar mActionBar;
    private TabSwipeAdapter mAdapter;
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private OnTabSelectedListener mOnTabSelectedListener;
    private int mPrevNavigationMode = ActionBar.NAVIGATION_MODE_STANDARD;
    private boolean mSmoothScroll = true;
    private boolean mSwipeEnabled = true;
    private List<TabInfo> mTabs = new ArrayList<TabInfo>();

    private ViewPager mViewPager;

    public TabSwipeController(Context context, FragmentManager fragmentManager, ActionBar actionBar) {
        if (context == null || fragmentManager == null || actionBar == null) {
            throw new NullPointerException();
        }
        mContext = context;
        mFragmentManager = fragmentManager;
        mActionBar = actionBar;

        if (mActionBar.getTabCount() > 0) {
            throw new IllegalStateException(
                    "TabSwipeController doesn't support multiplue tab controllers");
        }

        mPrevNavigationMode = mActionBar.getNavigationMode();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass) {
        return addTab(title, fragmentClass, null);
    }

    @Override
    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        TabInfo info = new TabInfo();
        info.mTitle = title;
        info.mFragmentClass = fragmentClass;
        info.mFragmentArguments = fragmentArguments;
        return addTab(info);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass) {
        return addTab(mContext.getText(title), fragmentClass, null);
    }

    @Override
    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        return addTab(mContext.getText(title), fragmentClass, fragmentArguments);
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo) {
        mTabs.add(tabInfo);
        mActionBar.addTab(makeActionBarTab(tabInfo));
        notifyChanged();
        return tabInfo;
    }

    @Override
    public TabInfo addTab(TabInfo tabInfo, int position) {
        mTabs.add(position, tabInfo);
        mActionBar.addTab(makeActionBarTab(tabInfo), position);
        notifyChanged();
        return tabInfo;
    }

    public void bind(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.setAdapter(null);
            mViewPager.setOnPageChangeListener(null);
        }
        mViewPager = viewPager;
        if (mViewPager != null) {
            if (mAdapter == null) {
                mAdapter = new TabSwipeAdapter(mFragmentManager);
            }
            reloadTabs();
            mViewPager.setAdapter(mAdapter);
            mViewPager.setOnPageChangeListener(mAdapter);
            mViewPager.setSwipeEnabled(mSwipeEnabled);
        }
    }

    private void dispatchTabSelected(int position) {
        boolean notify = false;
        if (mViewPager.getCurrentItem() != position) {
            mViewPager.setCurrentItem(position, mSmoothScroll);
            notify = true;
        }
        if (mActionBar.getSelectedNavigationIndex() != position) {
            mActionBar.selectTab(mActionBar.getTabAt(position));
            notify = true;
        }
        if (notify) {
            onTabSelected(position);
        }
    }

    @Override
    public OnTabSelectedListener getOnTabSelectedListener() {
        return mOnTabSelectedListener;
    }

    @Override
    public boolean isSmoothScroll() {
        return mSmoothScroll;
    }

    @Override
    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    protected Tab makeActionBarTab(TabInfo tabInfo) {
        Tab tab = mActionBar.newTab();
        tab.setText(tabInfo.mTitle);
        tab.setTabListener(mAdapter);
        return tab;
    }

    private void notifyChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onDestroyView() {
        mActionBar.removeAllTabs();
        mActionBar.setNavigationMode(mPrevNavigationMode);
    }

    protected abstract void onHandleTabs();

    public void onTabSelected(int position) {
        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(position);
        }
    }

    @Override
    public void reloadTabs() {
        removeAllTabs();
        onHandleTabs();
    }

    @Override
    public void removeAllTabs() {
        mActionBar.removeAllTabs();
        mTabs.clear();
        notifyChanged();
    }

    @Override
    public TabInfo removeTab(int position) {
        TabInfo tabInfo = mTabs.remove(position);
        mActionBar.removeTabAt(position);
        notifyChanged();
        return tabInfo;
    }

    @Override
    public TabInfo removeTab(TabInfo tabInfo) {
        for (int i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i) == tabInfo) {
                return removeTab(i);
            }
        }
        return tabInfo;
    }

    @Override
    public void setCurrentTab(int position) {
        dispatchTabSelected(Math.max(0, Math.min(position, mTabs.size() - 1)));
    }

    @Override
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    /**
     * Smooth scroll of ViewPager when user click on tab
     */
    @Override
    public void setSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }

    @Override
    public void setSwipeEnabled(boolean swipeEnabled) {
        if (mSwipeEnabled == swipeEnabled) {
            return;
        }
        mSwipeEnabled = swipeEnabled;
        if (mViewPager != null) {
            mViewPager.setSwipeEnabled(swipeEnabled);
        }
    }
}
