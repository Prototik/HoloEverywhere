
package org.holoeverywhere.demo;

import java.lang.ref.WeakReference;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;
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
import org.holoeverywhere.widget.ListPopupWindow;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DemoActivity extends Activity {
    private final class NavigationAdapter extends ArrayAdapter<NavigationItem> implements
            OnItemClickListener {
        private int lastSelection;

        private ListPopupWindow popupWindow;

        public NavigationAdapter() {
            super(DemoActivity.this, 0);
        }

        public void add(Class<? extends Fragment> clazz, int title) {
            add(new NavigationItem(clazz, title));
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemViewType(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(convertView, position == lastSelection);
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            onItemSelected(position);
        }

        public void onItemSelected(int position) {
            if (getItem(position).onClick()) {
                lastSelection = position;
            }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }

        public void setListPopupWindow(ListPopupWindow popupWindow) {
            this.popupWindow = popupWindow;
        }
    }

    private class NavigationItem {
        public final Class<? extends Fragment> clazz;
        public final int title;

        public NavigationItem(Class<? extends Fragment> clazz, int title) {
            this.clazz = clazz;
            this.title = title;
        }

        public Fragment createFragment(Context context) {
            return (Fragment) android.support.v4.app.Fragment.instantiate(context, clazz.getName());
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public View getView(View convertView, boolean selected) {
            DemoNavigationItem view;
            if (convertView == null) {
                view = new DemoNavigationItem(DemoActivity.this);
                view.setSelectionHandlerColorResource(R.color.holo_orange_dark);
            } else {
                view = (DemoNavigationItem) convertView;
            }
            view.setLabel(title);
            view.setSelectionHandlerVisiblity(selected ? View.VISIBLE : View.INVISIBLE);
            return view;
        }

        public boolean onClick() {
            getSupportActionBar().setSubtitle(title);
            replaceFragment(createFragment(DemoActivity.this));
            return true;
        }
    }

    private final class ThemeClickListener implements OnClickListener {
        private int theme;

        public ThemeClickListener(int theme) {
            this.theme = theme;
        }

        @Override
        public void onClick(View view) {
            PlaybackService.ignore();
            ThemeManager.restartWithTheme(DemoActivity.this, theme);
        }
    }

    private final class ThemeNavigationItem extends NavigationItem {
        public ThemeNavigationItem() {
            super(null, 0);
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public View getView(View convertView, boolean selected) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.theme_chooser);
            }
            TextView dark = (TextView) convertView.findViewById(R.id.dark);
            TextView light = (TextView) convertView.findViewById(R.id.light);
            TextView mixed = (TextView) convertView.findViewById(R.id.mixed);
            switch (ThemeManager.getTheme(getIntent())) {
                case ThemeManager.DARK:
                    setSelectionFlags(dark);
                    break;
                case ThemeManager.LIGHT:
                    setSelectionFlags(light);
                    break;
                case ThemeManager.MIXED:
                    setSelectionFlags(mixed);
                    break;
            }
            dark.setOnClickListener(new ThemeClickListener(ThemeManager.DARK));
            light.setOnClickListener(new ThemeClickListener(ThemeManager.LIGHT));
            mixed.setOnClickListener(new ThemeClickListener(ThemeManager.MIXED));
            return convertView;
        }

        @Override
        public boolean onClick() {
            return false;
        }

        private void setSelectionFlags(TextView text) {
            text.setBackgroundResource(R.drawable.theme_chooser_button_background);
            text.setTextColor(0xEE222222);
        }
    }

    private WeakReference<AlertDialogFragment> alertDialog;

    private View bottomView;

    private NavigationAdapter navigationAdapter;

    private ListPopupWindow navigationPopupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlaybackService.onCreate();

        setContentView(R.layout.content);
        bottomView = findViewById(R.id.bottom);

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.library_name);
        ab.setDisplayHomeAsUpEnabled(true);

        navigationAdapter = new NavigationAdapter();
        navigationAdapter.add(MainFragment.class, R.string.demo);
        navigationAdapter.add(SettingsFragment.class, R.string.settings);
        navigationAdapter.add(OtherFragment.class, R.string.other);
        navigationAdapter.add(AboutFragment.class, R.string.about);
        navigationAdapter.add(new ThemeNavigationItem());
        navigationAdapter.onItemSelected(0);
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
                if (navigationPopupWindow == null) {
                    navigationPopupWindow = new ListPopupWindow(this);
                    navigationPopupWindow.setAdapter(navigationAdapter);
                    navigationPopupWindow.setOnItemClickListener(navigationAdapter);
                    navigationAdapter.setListPopupWindow(navigationPopupWindow);
                }
                showListPopupWindow(navigationPopupWindow);
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
        AlertDialogFragment fragment;
        if (alertDialog == null || (fragment = alertDialog.get()) == null) {
            alertDialog = new WeakReference<AlertDialogFragment>(
                    fragment = new AlertDialogFragment());
        }
        fragment.show(this);
    }

    public void showCalendar(View v) {
        replaceFragment(CalendarFragment.getInstance(),
                "calendar");
    }

    public void showContextMenu(View v) {
        OtherFragment.getInstance().showContextMenu(v);
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
        OtherFragment.getInstance().showPopupMenu(v);
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
