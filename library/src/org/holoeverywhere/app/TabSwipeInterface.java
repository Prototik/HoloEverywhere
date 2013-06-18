
package org.holoeverywhere.app;

import org.holoeverywhere.app.TabSwipeInterface.ITabInfo;

import android.os.Bundle;

public interface TabSwipeInterface<T extends ITabInfo> {
    public static interface ITabInfo {
        public Bundle getFragmentArguments();

        public Class<? extends Fragment> getFragmentClass();

        public CharSequence getTitle();

        public void setFragmentArguments(Bundle fragmentArguments);

        public void setFragmentClass(Class<? extends Fragment> fragmentClass);

        public void setTitle(CharSequence title);
    }

    public static interface OnTabSelectedListener {
        public void onTabSelected(int position);
    }

    public T addTab(CharSequence title, Class<? extends Fragment> fragmentClass);

    public T addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments);

    public T addTab(int title, Class<? extends Fragment> fragmentClass);

    public T addTab(int title, Class<? extends Fragment> fragmentClass,
            Bundle fragmentArguments);

    public T addTab(T tabInfo);

    public T addTab(T tabInfo, int position);

    public OnTabSelectedListener getOnTabSelectedListener();

    public boolean isSmoothScroll();

    public boolean isSwipeEnabled();

    public void reloadTabs();

    public void removeAllTabs();

    public T removeTab(int position);

    public T removeTab(T tabInfo);

    public void setCurrentTab(int position);

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener);

    public void setSmoothScroll(boolean smoothScroll);

    public void setSwipeEnabled(boolean swipeEnabled);
}
