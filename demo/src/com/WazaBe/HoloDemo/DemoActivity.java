package com.WazaBe.HoloDemo;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;

import com.WazaBe.HoloDemo.fragments.AboutFragment;
import com.WazaBe.HoloDemo.fragments.CalendarFragment;
import com.WazaBe.HoloDemo.fragments.MainFragment;
import com.WazaBe.HoloDemo.fragments.PreferenceFragment;
import com.WazaBe.HoloEverywhere.ArrayAdapter;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.WazaBe.HoloEverywhere.app.DatePickerDialog;
import com.WazaBe.HoloEverywhere.app.Fragment;
import com.WazaBe.HoloEverywhere.app.ProgressDialog;
import com.WazaBe.HoloEverywhere.app.TimePickerDialog;
import com.WazaBe.HoloEverywhere.app.Activity.Holo;
import com.WazaBe.HoloEverywhere.sherlock.SActivity;
import com.WazaBe.HoloEverywhere.widget.ListPopupWindow;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;
import com.WazaBe.HoloEverywhere.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

@Holo(forceThemeApply = true, layout = R.layout.content)
public class DemoActivity extends SActivity {
	private final class FragmentListener implements TabListener {
		private final Class<? extends Fragment> clazz;
		private Fragment fragment;

		public FragmentListener(Class<? extends Fragment> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (fragment == null) {
				try {
					fragment = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ft.replace(android.R.id.content, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}
	}

	private void addTab(Class<? extends Fragment> clazz, String title) {
		Tab tab = getSupportActionBar().newTab();
		tab.setText(title);
		tab.setTabListener(new FragmentListener(clazz));
		getSupportActionBar().addTab(tab);
	}

	public void closeCalendar(View v) {
		replaceFragment(android.R.id.content, MainFragment.getInstance());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isABSSupport()) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_TABS);
			addTab(MainFragment.class, "Holo Demo");
			addTab(PreferenceFragment.class, "Settings");
			addTab(AboutFragment.class, "About");
		} else {
			replaceFragment(android.R.id.content, MainFragment.getInstance());
		}
	}

	public void replaceFragment(int resId, Fragment fragment) {
		replaceFragment(resId, fragment, null);
	}

	public void replaceFragment(int resId, Fragment fragment,
			String backStackName) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(resId, fragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (backStackName != null) {
			ft.addToBackStack(backStackName);
		}
		ft.commit();
	}

	public void setDarkTheme(View v) {
		ThemeManager.restartWithDarkTheme(this);
	}

	public void setLightTheme(View v) {
		ThemeManager.restartWithLightTheme(this);
	}

	public void setMixedTheme(View v) {
		ThemeManager.restartWithLightWithDarkActionBarTheme(this);
	}

	public void showAbout(View v) {
		replaceFragment(android.R.id.content, new AboutFragment(), "about");
	}

	public void showAlertDialog(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("AlertDialog");
		builder.setIcon(R.drawable.alert_dialog);
		builder.setMessage("Is fully-working port of AlertDialog from Android Jelly Bean\n"
				+ "Yes, I know it's a long text. At the same time check that part.");
		builder.setPositiveButton("Positive", null);
		builder.setNegativeButton("Negative", null);
		builder.setNeutralButton("Neutral", null);
		builder.show();
	}

	public void showCalendar(View v) {
		replaceFragment(android.R.id.content, CalendarFragment.getInstance(),
				isABSSupport() ? null : "calendar");
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
		w.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				w.dismiss();
			}
		});
		w.show();
	}

	public void showNumberPicker(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select number");
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

	public void showPopupWindow(View v) {
		View content = LayoutInflater.inflate(this, R.layout.popup_window);
		final PopupWindow w = new PopupWindow(content,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		w.setBackgroundDrawable(getResources().getDrawable(
				ThemeManager.isLight(this) ? R.drawable.dialog_full_holo_light
						: R.drawable.dialog_full_holo_dark));
		content.findViewById(R.id.imageButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						w.dismiss();
					}
				});
		w.setFocusable(true);
		w.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	public void showPreferences(View v) {
		replaceFragment(android.R.id.content, new PreferenceFragment(), "prefs");
	}

	public void showProgressDialog(View v) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.setMessage("I can close!");
		dialog.show();
	}

	public void showTimePicker(View v) {
		new TimePickerDialog(this, null, 12, 34, false).show();
	}

	public void showToast(View v) {
		Toast.makeText(this, "Toast example", Toast.LENGTH_LONG).show();
	}
}