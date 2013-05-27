
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.fragments.about.AboutFragment;
import org.holoeverywhere.demo.widget.DemoListRowView;
import org.holoeverywhere.demo.widget.DemoThemePicker;
import org.holoeverywhere.widget.LinearLayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MenuFragment extends Fragment {
    private final class NavigationClickListener implements OnClickListener {
        private Class<? extends Fragment> mClass;
        private int mPosition;

        public NavigationClickListener(Class<? extends Fragment> clazz, int position) {
            mClass = clazz;
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            final FragmentManager fm = getFragmentManager();
            if (mCurrentPage != mPosition || fm.getBackStackEntryCount() > 0) {
                mCurrentPage = mPosition;
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onMenuClick(mPosition);
                }
                while (fm.popBackStackImmediate()) {
                }
                getSupportActivity().replaceFragment(Fragment.instantiate(mClass));
                final AddonSliderA slider = getSupportActivity().addonSlider();
                if (slider.isAddonEnabled() && mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            slider.openContentView();
                        }
                    }, 100);
                }
                refresh();
            }
        }
    }

    public static interface OnMenuClickListener {
        public void onMenuClick(int position);
    }

    private Handler mHandler;
    private static final String KEY_PAGE = "page";
    private int mCurrentPage = 0;
    private LinearLayout mMenuList;
    private OnMenuClickListener mOnMenuClickListener;

    @Override
    public void onAttach(Activity activity) {
        mHandler = new Handler(activity.getMainLooper());
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mHandler = null;
        super.onDetach();
    }

    private void add(Class<? extends Fragment> clazz, int title) {
        final int position = mMenuList.getChildCount();
        DemoListRowView view = FontLoader.apply(new DemoListRowView(getSupportActionBarContext()));
        view.setLabel(title);
        view.setSelectionHandlerColorResource(R.color.holo_blue_dark);
        view.setSelectionHandlerVisiblity(position == mCurrentPage);
        view.setOnClickListener(new NavigationClickListener(clazz, position));
        mMenuList.addView(view);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_PAGE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PAGE, mCurrentPage);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMenuList = (LinearLayout) view.findViewById(R.id.menuList);
        ((DemoThemePicker) view.findViewById(R.id.themePicker)).setActivity(getSupportActivity());
        refresh();
        if (getSupportActivity().isFirstRun()) {
            getSupportActivity().replaceFragment(Fragment.instantiate(MainFragment.class));
        }
    }

    private void refresh() {
        mMenuList.removeAllViews();
        add(MainFragment.class, R.string.demo);
        add(SettingsFragment.class, R.string.settings);
        add(OtherFragment.class, R.string.other);
        add(AboutFragment.class, R.string.about);
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

}
