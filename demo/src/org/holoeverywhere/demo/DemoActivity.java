
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
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

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
            onItemSelected(position);
        }

        public void onItemSelected(int position) {
            if (lastSelection != position) {
                lastSelection = position;
                getIntent().putExtra(PAGE, position);
                ((NavigationItem) getItem(position)).onClick();
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

        public void onClick() {
            getSupportActionBar().setSubtitle(title);
            replaceFragment(Fragment.instantiate(clazz));
            requireSlidingMenu().showContent();
        }
    }

    private NavigationAdapter navigationAdapter;
    private static final String PAGE = "page";

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

        final SlidingMenuA sm = requireSlidingMenu();
        sm.setBehindContentView(makeMenuView());
        sm.setContent(R.layout.content);
        sm.getSlidingMenu().setBehindWidth(computeMenuWidth());
    }

    private View makeMenuView() {
        View view = getLayoutInflater().inflate(R.layout.menu);
        navigationAdapter = new NavigationAdapter();
        navigationAdapter.add(MainFragment.class, R.string.demo);
        navigationAdapter.add(SettingsFragment.class, R.string.settings);
        navigationAdapter.add(OtherFragment.class, R.string.other);
        navigationAdapter.add(AboutFragment.class, R.string.about);
        navigationAdapter.onItemSelected(getIntent().getIntExtra(PAGE, 0));
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(navigationAdapter);
        list.setOnItemClickListener(navigationAdapter);
        return view;
    }

    private int computeMenuWidth() {
        return (int) getResources().getFraction(R.dimen.demo_menu_width,
                getResources().getDisplayMetrics().widthPixels, 1);
    }

    public SlidingMenuA requireSlidingMenu() {
        return requireAddon(org.holoeverywhere.addon.SlidingMenu.class).activity(this);
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
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content, fragment);
        if (backStackName != null) {
            ft.addToBackStack(backStackName);
        }
        ft.commit();
    }
}
