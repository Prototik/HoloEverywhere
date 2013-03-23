
package org.holoeverywhere.demo;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.AddonSlidingMenu;
import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Activity.Addons;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.fragments.AboutFragment;
import org.holoeverywhere.demo.fragments.MainFragment;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.demo.fragments.SettingsFragment;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;
import org.holoeverywhere.demo.widget.DemoListRowView;
import org.holoeverywhere.widget.ListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

@Addons(Activity.ADDON_SLIDING_MENU)
public class DemoActivity extends Activity implements OnBackStackChangedListener {
    private final class NavigationAdapter extends DemoAdapter implements
            OnItemClickListener {
        public NavigationAdapter() {
            super(DemoActivity.this);
        }

        public void add(Class<? extends Fragment> clazz, int title) {
            add(new NavigationItem(clazz, title));
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            onItemSelected(position, true);
        }

        public void onItemSelected(int position, boolean setData) {
            if (position < 0) {
                position = 0;
            }
            if (mCurrentPage != position || setData && getSupportFragmentManager()
                    .getBackStackEntryCount() > 0) {
                mCurrentPage = position;
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onMenuClick(position);
                }
                if (setData) {
                    ((NavigationItem) getItem(position)).onClick(null);
                }
                notifyDataSetInvalidated();
            }
        }
    }

    private class NavigationItem extends DemoItem {
        public final Class<? extends Fragment> clazz;
        public final int title;

        public NavigationItem(Class<? extends Fragment> clazz, int title) {
            this.clazz = clazz;
            this.title = title;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DemoListRowView view = makeView(convertView, parent);
            view.setLabel(title);
            view.setSelectionHandlerColorResource(R.color.holo_blue_dark);
            view.setSelectionHandlerVisiblity(position == mCurrentPage);
            return view;
        }

        public void onClick(View view) {
            FragmentManager fm = getSupportFragmentManager();
            while (fm.popBackStackImmediate()) {
                fm.executePendingTransactions();
            }
            replaceFragment(Fragment.instantiate(clazz));
            fm.executePendingTransactions();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    addonSlidingMenu().showContent();
                }
            }, 100);
        }
    }

    public static interface OnMenuClickListener {
        public void onMenuClick(int position);
    }

    private static final String KEY_PAGE = "page";
    private static final int TABS_ACTIVITY = 1;
    private boolean mCreatedByThemeManager = false;
    private int mCurrentPage = -1;
    private boolean mFirstRun;
    private Handler mHandler;
    private NavigationAdapter mNavigationAdapter;
    private OnMenuClickListener mOnMenuClickListener;
    private boolean mStaticSlidingMenu;

    public AddonSlidingMenuA addonSlidingMenu() {
        return addon(AddonSlidingMenu.class);
    }

    private int computeMenuWidth() {
        return (int) getResources().getFraction(R.dimen.demo_menu_width,
                getResources().getDisplayMetrics().widthPixels, 1);
    }

    private View makeMenuView(Bundle savedInstanceState) {
        return prepareMenuView(getLayoutInflater().inflate(R.layout.menu), savedInstanceState);
    }

    @Override
    public void onBackStackChanged() {
        if (mStaticSlidingMenu) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(
                    getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFirstRun = savedInstanceState == null;
        // This line restore instance state when we are change theme and
        // activity restarts
        savedInstanceState = instanceState(savedInstanceState);
        super.onCreate(savedInstanceState);

        mCreatedByThemeManager = getIntent().getBooleanExtra(
                ThemeManager.KEY_CREATED_BY_THEME_MANAGER, false);
        if (mCreatedByThemeManager) {
            mFirstRun = false;
        }

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_PAGE, 0);
        }

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.library_name);

        setContentView(R.layout.content);

        final AddonSlidingMenuA addonSM = addonSlidingMenu();
        final SlidingMenu sm = addonSM.getSlidingMenu();

        View menu = findViewById(R.id.menu);
        if (menu == null) {
            // Phone
            mStaticSlidingMenu = false;
            ab.setDisplayHomeAsUpEnabled(true);
            addonSM.setBehindContentView(makeMenuView(savedInstanceState));
            addonSM.setSlidingActionBarEnabled(true);
            sm.setBehindWidth(computeMenuWidth());
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            sm.setSlidingEnabled(true);
        } else {
            // Tablet
            mStaticSlidingMenu = true;
            addonSM.setBehindContentView(new View(this)); // dummy view
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            sm.setSlidingEnabled(false);
            prepareMenuView(menu, savedInstanceState);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mStaticSlidingMenu
                        && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    addonSlidingMenu().toggle();
                } else {
                    onBackPressed();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        savedInstanceState = instanceState(savedInstanceState);
        if (mCreatedByThemeManager && savedInstanceState != null) {
            savedInstanceState.putBoolean("SlidingActivityHelper.open", false);
            savedInstanceState.putBoolean("SlidingActivityHelper.secondary", false);
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PAGE, mCurrentPage);
    }

    public void postDelayed(Runnable runnable, long delay) {
        if (mHandler == null) {
            mHandler = new Handler(getMainLooper());
        }
        mHandler.postDelayed(runnable, delay);
    }

    private View prepareMenuView(View view, Bundle savedInstanceState) {
        mNavigationAdapter = new NavigationAdapter();
        mNavigationAdapter.add(MainFragment.class, R.string.demo);
        mNavigationAdapter.add(SettingsFragment.class, R.string.settings);
        mNavigationAdapter.add(OtherFragment.class, R.string.other);
        mNavigationAdapter.add(AboutFragment.class, R.string.about);
        mNavigationAdapter.onItemSelected(mCurrentPage, mFirstRun);
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(mNavigationAdapter);
        list.setOnItemClickListener(mNavigationAdapter);
        return view;
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    public void replaceFragment(Fragment fragment,
            String backStackName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        if (backStackName != null) {
            ft.addToBackStack(backStackName);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    public void startDemoTabsActivity() {
        startActivityForResult(new Intent(this, DemoTabsActivity.class), TABS_ACTIVITY);
    }
}
