
package org.holoeverywhere.demo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DatePickerDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.demo.fragments.AboutFragment;
import org.holoeverywhere.demo.fragments.AlertDialogFragment;
import org.holoeverywhere.demo.fragments.CalendarFragment;
import org.holoeverywhere.demo.fragments.MainFragment;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.demo.fragments.SettingsFragment;
import org.holoeverywhere.demo.widget.DemoNavigationItem;
import org.holoeverywhere.demo.widget.DemoNavigationWidget;
import org.holoeverywhere.slidingmenu.SlidingActivity;
import org.holoeverywhere.slidingmenu.SlidingMenu;
import org.holoeverywhere.slidingmenu.SlidingMenu.CanvasTransformer;
import org.holoeverywhere.widget.ListPopupWindow;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DemoActivity extends SlidingActivity {
    private final class ListNavigationAdapter extends ArrayAdapter<NavigationItem> implements
            android.widget.AdapterView.OnItemClickListener {

        private int lastSelectedItem = 0;

        public ListNavigationAdapter() {
            this(new ArrayList<NavigationItem>());
        }

        public ListNavigationAdapter(List<NavigationItem> list) {
            super(DemoActivity.this, android.R.id.text1, list);
        }

        public void add(Class<? extends Fragment> clazz, int title) {
            add(new NavigationItem(clazz, title));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            NavigationItem item = getItem(position);
            DemoNavigationItem view;
            if (convertView == null) {
                view = new DemoNavigationItem(DemoActivity.this);
                view.setSelectionHandlerColorResource(R.color.holo_blue_light);
            } else {
                view = (DemoNavigationItem) convertView;
            }
            view.setLabel(item.title);
            view.setSelectionHandlerVisiblity(lastSelectedItem == position ? View.VISIBLE
                    : View.INVISIBLE);
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition,
                long itemId) {
            NavigationItem item = getItem(itemPosition);

            lastSelectedItem = itemPosition;
            notifyDataSetInvalidated();
            getIntent().putExtra(LIST_NAVIGATION_PAGE, itemPosition);

            replaceFragment(R.id.content, item.getFragment());

            getSupportActionBar().setSubtitle(item.title);

            getSlidingMenu().showAbove(true);
        }
    }

    private static final class NavigationItem {
        public final Class<? extends Fragment> clazz;
        private Fragment fragment;
        public final int title;

        public NavigationItem(Class<? extends Fragment> clazz, int title) {
            this.clazz = clazz;
            this.title = title;
        }

        public Fragment getFragment() {
            if (fragment == null) {
                try {
                    fragment = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return fragment;
        }
    }

    private static final String LIST_NAVIGATION_PAGE = "listNavigationPage";

    private WeakReference<AlertDialogFragment> alertDialog;

    private ListNavigationAdapter lastListNavigationAdapter;

    public void closeCalendar(View v) {
        replaceFragment(R.id.content, MainFragment.getInstance(), "content");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlaybackService.onCreate();

        lastListNavigationAdapter = new ListNavigationAdapter();
        lastListNavigationAdapter.add(MainFragment.class,
                R.string.demo);
        lastListNavigationAdapter.add(SettingsFragment.class, R.string.settings);
        lastListNavigationAdapter.add(OtherFragment.class, R.string.other);
        lastListNavigationAdapter.add(AboutFragment.class,
                R.string.about);

        DemoNavigationWidget navigationWidget = new DemoNavigationWidget(this);
        ListView list = navigationWidget.getListView();
        list.setAdapter(lastListNavigationAdapter);
        list.setOnItemClickListener(lastListNavigationAdapter);
        list.performItemClick(null, 0, 0);
        final int themePicker;
        int theme = ThemeManager.getTheme(this);
        if (ThemeManager.isDark(theme)) {
            themePicker = R.id.themePickerDark;
        } else if (ThemeManager.isLight(this)) {
            themePicker = R.id.themePickerLight;
        } else {
            themePicker = R.id.themePickerMixed;
        }
        ((DemoNavigationItem) navigationWidget.findViewById(themePicker))
                .setSelectionHandlerColorResource(R.color.holo_orange_light);
        setBehindContentView(navigationWidget);

        setContentView(R.layout.content);

        final SlidingMenu si = getSlidingMenu();
        si.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        si.setBehindWidthRes(R.dimen.demo_menu_width);
        si.setShadowWidth(0);
        si.setBehindCanvasTransformer(new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {

            }
        });

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.library_name);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        PlaybackService.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restartMusic:
                PlaybackService.restart(this);
                break;
            case android.R.id.home:
                toggle();
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

    public void replaceFragment(int resId, Fragment fragment) {
        replaceFragment(resId, fragment, null);
    }

    public void replaceFragment(int resId, Fragment fragment,
            String backStackName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resId, fragment);
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

    public void showAbout(View v) {
        replaceFragment(R.id.content, new AboutFragment(), "about");
    }

    public void showAlertDialog(View v) {
        AlertDialogFragment fragment;
        if (alertDialog == null || (fragment = alertDialog.get()) == null) {
            alertDialog = new WeakReference<AlertDialogFragment>(
                    fragment = new AlertDialogFragment());
        }
        fragment.show(this);
    }

    public void showCalendar(View v) {
        replaceFragment(R.id.content, CalendarFragment.getInstance(),
                "calendar");
    }

    public void showContextMenu(View v) {
        MainFragment.getInstance().showContextMenu(v);
    }

    public void showDatePicker(View v) {
        new DatePickerDialog(this, null, 2012, 11, 21).show();
    }

    @SuppressLint("NewApi")
    public void showListPopupWindow(View v) {
        final ListPopupWindow w = new ListPopupWindow(this);
        w.setAnchorView(v);
        w.setAdapter(ArrayAdapter.createFromResource(this, R.array.countries,
                R.layout.list_popup_window_row));
        w.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                w.dismiss();
            }
        });
        w.show();
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
        MainFragment.getInstance().showPopupMenu(v);
    }

    public void showPreferences(View v) {
        replaceFragment(R.id.content, new SettingsFragment(), "prefs");
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
