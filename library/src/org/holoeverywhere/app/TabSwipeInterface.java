
package org.holoeverywhere.app;

import org.holoeverywhere.app.TabSwipeInterface.ITabInfo;

import android.os.Bundle;

public interface TabSwipeInterface<T extends ITabInfo<T>> {
    public static interface ITabInfo<T extends ITabInfo<T>> {
        public Bundle getFragmentArguments();

        public Class<? extends Fragment> getFragmentClass();

        public CharSequence getTitle();

        public T setFragmentArguments(Bundle fragmentArguments);

        public T setFragmentClass(Class<? extends Fragment> fragmentClass);

        public T setTitle(CharSequence title);
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
