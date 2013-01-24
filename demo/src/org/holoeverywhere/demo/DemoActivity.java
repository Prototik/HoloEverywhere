
package org.holoeverywhere.demo;

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

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class DemoActivity extends Activity {
    private final class NavigationAdapter extends DemoAdapter implements
            OnItemClickListener {
        private int lastSelection = -1;

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
            if (lastSelection != position) {
                lastSelection = position;
                getIntent().putExtra(PAGE, position);
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
            view.setSelectionHandlerVisiblity(position == navigationAdapter.lastSelection);
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

    public void postDelayed(Runnable runnable, long delay) {
        if (handler == null) {
            handler = new Handler(getMainLooper());
        }
        handler.postDelayed(runnable, delay);
    }

    private Handler handler;
    private static final String PAGE = "page";
    private NavigationAdapter navigationAdapter;

    private int computeMenuWidth() {
        return (int) getResources().getFraction(R.dimen.demo_menu_width,
                getResources().getDisplayMetrics().widthPixels, 1);
    }

    private View makeMenuView(Bundle savedInstanceState) {
        return prepareMenuView(getLayoutInflater().inflate(R.layout.menu), savedInstanceState);
    }

    private View prepareMenuView(View view, Bundle savedInstanceState) {
        navigationAdapter = new NavigationAdapter();
        navigationAdapter.add(MainFragment.class, R.string.demo);
        navigationAdapter.add(SettingsFragment.class, R.string.settings);
        navigationAdapter.add(OtherFragment.class, R.string.other);
        navigationAdapter.add(AboutFragment.class, R.string.about);
        navigationAdapter.onItemSelected(getIntent().getIntExtra(PAGE, 0),
                savedInstanceState == null);
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(navigationAdapter);
        list.setOnItemClickListener(navigationAdapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Holo config = Holo.defaultConfig();
        config.requireSlidingMenu = true;
        init(config);

        super.onCreate(savedInstanceState);

        PlaybackService.onCreate();

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.library_name);
        ab.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.content);

        final SlidingMenuA addonSM = requireSlidingMenu();
        final SlidingMenu sm = addonSM.getSlidingMenu();

        View menu = findViewById(R.id.menu);
        if (menu == null) {
            // Phone
            addonSM.setBehindContentView(makeMenuView(savedInstanceState));
            sm.setBehindWidth(computeMenuWidth());
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            sm.setSlidingEnabled(true);
        } else {
            // Tablet
            addonSM.setBehindContentView(new View(this)); // dummy view
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            sm.setSlidingEnabled(false);
            
            prepareMenuView(menu, savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        if (PlaybackService.isDisable()) {
            menu.findItem(R.id.disableMusic).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disableMusic:
                PlaybackService.disable();
                supportInvalidateOptionsMenu();
                break;
            case android.R.id.home:
                requireSlidingMenu().toggle();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onPause() {
        PlaybackService.onPause();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        PlaybackService.ignore();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlaybackService.onResume(this);
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
