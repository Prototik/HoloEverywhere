
package org.holoeverywhere.app;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.R;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

/**
 * This activity class implement tabs + swipe navigation pattern<br />
 * <br />
 * Part of HoloEverywhere
 */
public abstract class TabSwipeActivity extends Activity {
    public static class TabInfo {
        public Bundle fragmentArguments;
        public Class<? extends Fragment> fragmentClass;
        public CharSequence title;

        protected Tab makeActionBarTab(TabSwipeActivity tabSwipeActivity) {
            Tab tab = tabSwipeActivity.getSupportActionBar().newTab();
            tab.setText(title);
            tab.setTabListener(tabSwipeActivity.mAdapter);
            return tab;
        }
    }

    private final class TabSwipeAdapter extends FragmentStatePagerAdapter implements
            OnPageChangeListener, TabListener {
        public TabSwipeAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            final TabInfo info = mTabs.get(position);
            return Fragment.instantiate(info.fragmentClass, info.fragmentArguments);
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

    private TabSwipeAdapter mAdapter;
    private int mCustomLayout = -1;
    private boolean mSmoothScroll = true;
    private List<TabInfo> mTabs = new ArrayList<TabInfo>();
    private ViewPager mViewPager;

    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass) {
        return addTab(title, fragmentClass, null);
    }

    public TabInfo addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        TabInfo info = new TabInfo();
        info.title = title;
        info.fragmentClass = fragmentClass;
        info.fragmentArguments = fragmentArguments;
        return addTab(info);
    }

    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass) {
        return addTab(getText(title), fragmentClass, null);
    }

    public TabInfo addTab(int title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments) {
        return addTab(getText(title), fragmentClass, fragmentArguments);
    }

    public TabInfo addTab(TabInfo tabInfo) {
        mTabs.add(tabInfo);
        getSupportActionBar().addTab(tabInfo.makeActionBarTab(this));
        notifyChanged();
        return tabInfo;
    }

    public TabInfo addTab(TabInfo tabInfo, int position) {
        mTabs.add(position, tabInfo);
        getSupportActionBar().addTab(tabInfo.makeActionBarTab(this), position);
        notifyChanged();
        return tabInfo;
    }

    private void dispatchTabSelected(int position) {
        boolean notify = false;
        if (mViewPager.getCurrentItem() != position) {
            mViewPager.setCurrentItem(position, mSmoothScroll);
            notify = true;
        }
        if (getSupportActionBar().getSelectedNavigationIndex() != position) {
            getSupportActionBar().selectTab(getSupportActionBar().getTabAt(position));
            notify = true;
        }
        if (notify) {
            onTabSelected(position);
        }
    }

    public boolean isSmoothScroll() {
        return mSmoothScroll;
    }

    private void notifyChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mCustomLayout > 0 ? mCustomLayout : R.layout.tab_swipe);
        mViewPager = (ViewPager) findViewById(R.id.tabSwipePager);
        if (mViewPager == null) {
            throw new IllegalStateException(
                    "Add ViewPager to your custom layout with id @id/tabSwipePager");
        }
        mAdapter = new TabSwipeAdapter();
        onHandleTabs();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mAdapter);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    /**
     * Add your tabs here
     */
    protected abstract void onHandleTabs();

    protected void onTabSelected(int position) {

    }

    public TabInfo removeTab(int position) {
        TabInfo tabInfo = mTabs.remove(position);
        getSupportActionBar().removeTabAt(position);
        notifyChanged();
        return tabInfo;
    }

    public TabInfo removeTab(TabInfo tabInfo) {
        for (int i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i) == tabInfo) {
                return removeTab(i);
            }
        }
        return tabInfo;
    }

    /**
     * If you want custom layout for this activity - call this method before
     * super.onCreate<br />
     * Your layout should be contains ViewPager with id @id/tabSwipePager
     */
    public void setCustomLayout(int customLayout) {
        mCustomLayout = customLayout;
    }

    /**
     * Smooth scroll of ViewPager when user click on tab
     */
    public void setSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }
}
