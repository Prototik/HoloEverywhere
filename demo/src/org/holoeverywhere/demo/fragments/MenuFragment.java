
package org.holoeverywhere.demo.fragments;

import java.lang.ref.WeakReference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.fragments.about.AboutFragment;
import org.holoeverywhere.demo.widget.DemoThemePicker;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuFragment extends ListFragment {
    private final class MenuAdapter extends ArrayAdapter<MenuItem> {
        private final int mTextAppearance, mTextAppearanceInverse;

        public MenuAdapter(Context context) {
            super(context, 0);
            TypedArray a = context.obtainStyledAttributes(new int[] {
                    android.R.attr.textAppearanceMedium, android.R.attr.textAppearanceMediumInverse
            });
            mTextAppearance = a.getResourceId(0, R.style.Holo_TextAppearance_Medium);
            mTextAppearanceInverse = a.getResourceId(1, R.style.Holo_TextAppearance_Medium_Inverse);
            a.recycle();
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item, parent,
                        false);
            }
            final MenuItem menuItem = getItem(position);
            TextView labelView = (TextView) convertView.findViewById(android.R.id.text1);
            View selectionHandler = convertView.findViewById(R.id.selectionHandler);
            labelView.setText(menuItem.label);
            if (mCurrentPage == position) {
                labelView.setBackgroundColor(menuItem.backgroundColor);
                selectionHandler.setBackgroundColor(menuItem.selectionHandlerColor);
                if (ThemeManager.getThemeType(getSupportActivity()) != ThemeManager.LIGHT) {
                    labelView.setTextAppearance(getContext(), mTextAppearanceInverse);
                } else {
                    labelView.setTextAppearance(getContext(), mTextAppearance);
                }
            } else {
                labelView.setBackgroundDrawable(null);
                selectionHandler.setBackgroundDrawable(null);
                labelView.setTextAppearance(getContext(), mTextAppearance);
            }
            return convertView;
        }
    }

    private static final class MenuItem {
        public int backgroundColor;
        public Class<? extends Fragment> fragmentClass;
        public CharSequence label;
        private WeakReference<Fragment> lastFragment;
        private Fragment.SavedState lastFragmentState;
        public int selectionHandlerColor;
    }

    private final class MenuListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setMenuSelection(position, false);
        }
    }

    public static interface OnMenuClickListener {
        public void onMenuClick(int position);
    }

    private static final String KEY_CURRENT_PAGE = ":menu:currentPage";
    private MenuAdapter mAdapter;
    private int mCurrentPage = -1;
    private OnMenuClickListener mOnMenuClickListener;

    private void add(Class<? extends Fragment> clazz, int title, int selectionHandlerColor,
            int backgroundColor) {
        MenuItem item = new MenuItem();
        item.fragmentClass = clazz;
        final Resources res = getSupportActivity().getResources();
        item.selectionHandlerColor = res.getColor(selectionHandlerColor);
        item.backgroundColor = res.getColor(backgroundColor) | 0xFF000000 & 0x50000000;
        item.label = res.getText(title);
        mAdapter.add(item);
    }

    private void addPages() {
        add(MainFragment.class, R.string.demo,
                R.color.holo_blue_dark, R.color.holo_blue_light);
        add(SettingsFragment.class, R.string.settings,
                R.color.holo_green_dark, R.color.holo_green_light);
        add(OtherFragment.class, R.string.other,
                R.color.holo_orange_dark, R.color.holo_orange_light);
        add(AboutFragment.class, R.string.about,
                R.color.holo_red_dark, R.color.holo_red_light);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getSupportActionBarContext());
    }

    @Override
    public DemoActivity getSupportActivity() {
        return (DemoActivity) super.getSupportActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DemoThemePicker) view.findViewById(R.id.themePicker)).setActivity(getSupportActivity());
        setListAdapter(mAdapter = new MenuAdapter(getSupportActionBarContext()));
        getListView().setOnItemClickListener(new MenuListener());
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE, -1);
        }
        addPages();
        if (mCurrentPage < 0) {
            setMenuSelection(0);
        }
    }

    public void setMenuSelection(int position) {
        setMenuSelection(position, true);
    }

    public void setMenuSelection(int position, boolean force) {
        final FragmentManager fm = getFragmentManager();
        final AddonSliderA slider = getSupportActivity().addonSlider();
        if (force || mCurrentPage != position || fm.getBackStackEntryCount() > 0) {
            if (mCurrentPage >= 0) {
                final MenuItem item = mAdapter.getItem(mCurrentPage);
                final WeakReference<Fragment> ref = item.lastFragment;
                final Fragment fragment = ref == null ? null : ref.get();
                if (fragment != null) {
                    if (!fragment.isDetached()) {
                        fm.beginTransaction().detach(fragment).commit();
                        fm.executePendingTransactions();
                    }
                    item.lastFragmentState = fm.saveFragmentInstanceState(fragment);
                }
            }
            mCurrentPage = position;
            mAdapter.notifyDataSetInvalidated();
            while (fm.popBackStackImmediate()) {
            }
            final MenuItem item = mAdapter.getItem(position);
            final Fragment fragment = Fragment.instantiate(item.fragmentClass);
            if (item.lastFragmentState != null) {
                fragment.setInitialSavedState(item.lastFragmentState);
            }
            item.lastFragment = new WeakReference<Fragment>(fragment);
            getSupportActivity().replaceFragment(fragment);
        }
        if (slider.isAddonEnabled()) {
            slider.openContentViewDelayed(40);
        }
        if (mOnMenuClickListener != null) {
            mOnMenuClickListener.onMenuClick(position);
        }
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }
}
