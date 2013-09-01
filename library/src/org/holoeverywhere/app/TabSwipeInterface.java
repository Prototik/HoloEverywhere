
package org.holoeverywhere.app;

import android.os.Bundle;

import org.holoeverywhere.app.TabSwipeInterface.ITabInfo;

public interface TabSwipeInterface<T extends ITabInfo<T>> {
    public T addTab(CharSequence title, Class<? extends Fragment> fragmentClass);

    public T addTab(CharSequence title, Class<? extends Fragment> fragmentClass,
                    Bundle fragmentArguments);

    public T addTab(int title, Class<? extends Fragment> fragmentClass);

    public T addTab(int title, Class<? extends Fragment> fragmentClass,
                    Bundle fragmentArguments);

    public T addTab(T tabInfo);

    public T addTab(T tabInfo, int position);

    public OnTabSelectedListener getOnTabSelectedListener();

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener);

    public int getCurrentTab();

    public void setCurrentTab(int position);

    public T getTabAt(int position);

    public boolean isSmoothScroll();

    public void setSmoothScroll(boolean smoothScroll);

    public boolean isSwipeEnabled();

    public void setSwipeEnabled(boolean swipeEnabled);

    public void reloadTabs();

    public void removeAllTabs();

    public T removeTab(int position);

    public T removeTab(T tabInfo);

    public static interface ITabInfo<T extends ITabInfo<T>> {
        public Bundle getFragmentArguments();

        public T setFragmentArguments(Bundle fragmentArguments);

        public Class<? extends Fragment> getFragmentClass();

        public T setFragmentClass(Class<? extends Fragment> fragmentClass);

        public CharSequence getTitle();

        public T setTitle(CharSequence title);
    }

    public static interface OnTabSelectedListener {
        public void onTabSelected(int position);
    }
}
