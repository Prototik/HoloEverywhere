
package org.holoeverywhere.demo;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.SlidingMenu.SlidingMenuA;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DatePickerDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.demo.fragments.AboutFragment;
import org.holoeverywhere.demo.fragments.AlertDialogFragment;
import org.holoeverywhere.demo.fragments.CalendarFragment;
import org.holoeverywhere.demo.fragments.ListModalFragment;
import org.holoeverywhere.demo.fragments.MainFragment;
import org.holoeverywhere.demo.fragments.OtherFragmentOld;
import org.holoeverywhere.demo.fragments.SettingsFragment;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;
import org.holoeverywhere.demo.widget.DemoListRowView;
import org.holoeverywhere.widget.ListPopupWindow;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
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

    private View bottomView;
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

        bottomView = findViewById(R.id.bottom);

    }

    private View makeMenuView() {
        View view = getLayoutInflater().inflate(R.layout.menu);
        navigationAdapter = new NavigationAdapter();
        navigationAdapter.add(MainFragment.class, R.string.demo);
        navigationAdapter.add(SettingsFragment.class, R.string.settings);
        navigationAdapter.add(OtherFragmentOld.class, R.string.other);
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

    public void showListViewModal(View v) {
        replaceFragment(ListModalFragment.getInstance(), "listviewmodal");
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

    public void setDarkTheme(View v) {
        PlaybackService.ignore();
        ThemeManager.restartWithDarkTheme(this);
    }

    public void setLightTheme(View v) {
        PlaybackService.ignore();
        ThemeManager.restartWithLightTheme(this);
    }

    public void setMixedTheme(View v) {
        PlaybackService.ignore();
        ThemeManager.restartWithMixedTheme(this);
    }

    public void showAlertDialog(View v) {
        Fragment.instantiate(AlertDialogFragment.class).show(this);
    }

    public void showCalendar(View v) {
        replaceFragment(Fragment.instantiate(CalendarFragment.class), "calendar");
    }

    public void showContextMenu(View v) {
        // OtherFragment.getInstance().showContextMenu(v);
    }

    public void showDatePicker(View v) {
        new DatePickerDialog(this, null, 2012, 11, 21).show();
    }

    public void showListPopupWindow(ListPopupWindow listPopupWindow) {
        listPopupWindow.setAnchorView(bottomView);
        listPopupWindow.show();
    }

    @SuppressLint("NewApi")
    public void showListPopupWindow(View v) {
        final ListPopupWindow window = new ListPopupWindow(this);
        window.setAdapter(ArrayAdapter.createFromResource(this, R.array.countries,
                R.layout.simple_list_item_1));
        window.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                window.dismiss();
            }
        });
        showListPopupWindow(window);
    }

    public void showNumberPicker(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_number);
        View view = LayoutInflater.inflate(this, R.layout.number_picker_demo);
        NumberPicker picker = (NumberPicker) view
                .findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(15);
        picker.setValue(3);
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    public void showPopupMenu(View v) {
        // OtherFragment.getInstance().showPopupMenu(v);
    }

    public void showProgressDialog(View v) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setIndeterminate(true);
        dialog.setMessage(getText(R.string.very_very_long_operation));
        dialog.show();
    }

    public void showTimePicker(View v) {
        new TimePickerDialog(this, null, 12, 34, false).show();
    }

    public void showToast(View v) {
        Toast.makeText(this, "Toast example", Toast.LENGTH_LONG).show();
    }
}
