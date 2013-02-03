
package org.holoeverywhere.demo;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.SlidingMenu.SlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.fragments.AboutFragment;
import org.holoeverywhere.demo.fragments.MainFragment;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.demo.fragments.SettingsFragment;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;
import org.holoeverywhere.demo.widget.DemoListRowView;
import org.holoeverywhere.widget.ListView;

import android.annotation.SuppressLint;
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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

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
            if (mCurrentPage != position || mStaticSlidingMenu
                    && getSupportFragmentManager().getBackStackEntryCount() > 0) {
                mCurrentPage = position;
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
                    requireSlidingMenu().showContent();
                }
            }, 100);
        }
    }

    private static final String KEY_DISABLE_MUSIC = "disableMusic";
    private static final String KEY_PAGE = "page";
    private int mCurrentPage = -1;
    private Handler mHandler;
    private NavigationAdapter mNavigationAdapter;
    private boolean mStaticSlidingMenu, mCreatedByThemeManager = false, mDisableMusic = false,
            mFirstRun;

    private int computeMenuWidth() {
        return (int) getResources().getFraction(R.dimen.demo_menu_width,
                getResources().getDisplayMetrics().widthPixels, 1);
    }

    private View makeMenuView(Bundle savedInstanceState) {
        return prepareMenuView(getLayoutInflater().inflate(R.layout.menu), savedInstanceState);
    }

    @Override
    @SuppressLint("NewApi")
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            PlaybackService.pause(true);
            finish();
        }
    }

    @Override
    public void onBackStackChanged() {
        if (!mStaticSlidingMenu) {
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
            mDisableMusic = savedInstanceState.getBoolean(KEY_DISABLE_MUSIC, false);
            mCurrentPage = savedInstanceState.getInt(KEY_PAGE, 0);
        }

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.library_name);

        setContentView(R.layout.content);

        final SlidingMenuA addonSM = requireSlidingMenu();
        final SlidingMenu sm = addonSM.getSlidingMenu();

        View menu = findViewById(R.id.menu);
        if (menu == null) {
            // Phone
            mStaticSlidingMenu = true;
            ab.setDisplayHomeAsUpEnabled(true);
            addonSM.setBehindContentView(makeMenuView(savedInstanceState));
            addonSM.setSlidingActionBarEnabled(true);
            sm.setBehindWidth(computeMenuWidth());
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            sm.setSlidingEnabled(true);
        } else {
            // Tablet
            mStaticSlidingMenu = false;
            addonSM.setBehindContentView(new View(this)); // dummy view
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            sm.setSlidingEnabled(false);
            prepareMenuView(menu, savedInstanceState);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected Holo onCreateConfig(Bundle savedInstanceState) {
        Holo config = super.onCreateConfig(savedInstanceState);
        config.requireSlidingMenu = true;
        return config;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        if (mDisableMusic) {
            menu.findItem(R.id.disableMusic).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disableMusic:
                if (!mDisableMusic) {
                    mDisableMusic = true;
                    PlaybackService.pause(true);
                    supportInvalidateOptionsMenu();
                }
                break;
            case android.R.id.home:
                if (mStaticSlidingMenu && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    requireSlidingMenu().toggle();
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
    protected void onPause() {
        PlaybackService.pause(false);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if (mCreatedByThemeManager) {
            savedInstanceState = instanceState(savedInstanceState);
            savedInstanceState.putBoolean("SlidingActivityHelper.open", false);
            savedInstanceState.putBoolean("SlidingActivityHelper.secondary", false);
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mDisableMusic) {
            PlaybackService.play();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DISABLE_MUSIC, mDisableMusic);
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

    public SlidingMenuA requireSlidingMenu() {
        return requireAddon(org.holoeverywhere.addon.SlidingMenu.class).activity(this);
    }
}
