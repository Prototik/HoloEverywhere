
package org.holoeverywhere.addon;

import org.holoeverywhere.R;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeController;
import org.holoeverywhere.app.TabSwipeController.TabInfo;
import org.holoeverywhere.app.TabSwipeInterface;
import org.holoeverywhere.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

public class AddonTabber extends IAddon {
    public static class AddonTabberA extends IAddonActivity implements TabSwipeInterface<TabInfo> {
        private TabSwipeController mController;
        private AddonTabberCallback mTabberCallback;

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
            return new TabSwipeController(get(),
                    get().getSupportFragmentManager(), get().getSupportActionBar()) {
                @Override
                protected void onHandleTabs() {
                    AddonTabberA.this.onHandleTabs();
                }
            };
        }

        @Override
        public OnTabSelectedListener getOnTabSelectedListener() {
            return mController.getOnTabSelectedListener();
        }

        public AddonTabberCallback getTabberCallback() {
            return mTabberCallback;
        }

        @Override
        public boolean isSmoothScroll() {
            return mController.isSmoothScroll();
        }

        @Override
        public boolean isSwipeEnabled() {
            return mController.isSwipeEnabled();
        }

        @Override
        protected void onAttach(Activity object) {
            super.onAttach(object);
            if (object instanceof AddonTabberCallback) {
                mTabberCallback = (AddonTabberCallback) object;
            }
        }

        @Override
        public void onContentChanged() {
            final ViewPager viewPager = (ViewPager) get().findViewById(R.id.tabSwipePager);
            if (viewPager == null) {
                throw new IllegalStateException("Add ViewPager with id @+id/tabSwipePager to your "
                        + this + " fragment");
            }
            if (mController == null) {
                mController = createController();
            }
            mController.bind(viewPager);
        }

        protected void onHandleTabs() {
            if (mTabberCallback != null) {
                mTabberCallback.onHandleTabs();
            }
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            if (!get().isDecorViewInited()) {
                get().setContentView(R.layout.tab_swipe);
            }
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

        @Override
        public void setSwipeEnabled(boolean swipeEnabled) {
            mController.setSwipeEnabled(swipeEnabled);
        }

        public void setTabberCallback(AddonTabberCallback tabberCallback) {
            mTabberCallback = tabberCallback;
        }
    }

    public static interface AddonTabberCallback {
        public void onHandleTabs();
    }

    public static class AddonTabberF extends IAddonFragment implements TabSwipeInterface<TabInfo> {
        private TabSwipeController mController;
        private AddonTabberCallback mTabberCallback;

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
            return new TabSwipeController(get().getActivity(),
                    get().getFragmentManager(), get().getSupportActionBar()) {
                @Override
                protected void onHandleTabs() {
                    AddonTabberF.this.onHandleTabs();
                }
            };
        }

        @Override
        public OnTabSelectedListener getOnTabSelectedListener() {
            return mController.getOnTabSelectedListener();
        }

        public AddonTabberCallback getTabberCallback() {
            return mTabberCallback;
        }

        @Override
        public boolean isSmoothScroll() {
            return mController.isSmoothScroll();
        }

        @Override
        public boolean isSwipeEnabled() {
            return mController.isSwipeEnabled();
        }

        @Override
        protected void onAttach(Fragment object) {
            super.onAttach(object);
            if (object instanceof AddonTabberCallback) {
                mTabberCallback = (AddonTabberCallback) object;
            }
        }

        @Override
        public void onDestroyView() {
            if (mController != null) {
                mController.onDestroyView();
            }
        }

        protected void onHandleTabs() {
            if (mTabberCallback != null) {
                mTabberCallback.onHandleTabs();
            }
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
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

        @Override
        public void setSwipeEnabled(boolean swipeEnabled) {
            mController.setSwipeEnabled(swipeEnabled);
        }

        public void setTabberCallback(AddonTabberCallback tabberCallback) {
            mTabberCallback = tabberCallback;
        }
    }

    public AddonTabber() {
        registerActivity(AddonTabberA.class);
        registerFragment(AddonTabberF.class);
    }
}
