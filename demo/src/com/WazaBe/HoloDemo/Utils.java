package com.WazaBe.HoloDemo;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.Activity;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.WazaBe.HoloEverywhere.app.DatePickerDialog;
import com.WazaBe.HoloEverywhere.app.Fragment;
import com.WazaBe.HoloEverywhere.app.ProgressDialog;
import com.WazaBe.HoloEverywhere.app.TimePickerDialog;
import com.WazaBe.HoloEverywhere.app.Toast;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;

public class Utils {
	public static void onViewCreated(View view) {

	}

	public static void showAlertDialog(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("AlertDialog")
				.setMessage(
						"Is fully-working port of AlertDialog from Android Jelly Bean\n"
								+ "Yes, I know it's a long text. At the same time check that part.")
				.setIcon(R.drawable.icon);
		builder.setPositiveButton("Positive", null);
		builder.setNegativeButton("Negative", null);
		builder.setNeutralButton("Neutral", null);
		builder.show();
	}

	public static void showDatePicker(final Activity activity) {
		new DatePickerDialog(activity, null, 2012, 11, 21).show();
	}

	public static void showNumberPicker(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Select number");
		View view = LayoutInflater.inflate(activity,
				R.layout.number_picker_demo);
		NumberPicker picker = (NumberPicker) view
				.findViewById(R.id.numberPicker);
		picker.setMinValue(1);
		picker.setMaxValue(15);
		picker.setValue(3);
		builder.setView(view);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	public static void showPreferences(Activity activity) {
		Intent intent = new Intent(activity, HoloPreferenceActivity.class);
		intent.putExtra(ThemeManager.THEME_TAG, ThemeManager.getTheme(activity));
		activity.startActivity(intent);
	}

	public static void showProgressDialog(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.setMessage("I can close!");
		dialog.show();
	}

	public static void showTimePicker(Activity activity) {
		new TimePickerDialog(activity, null, 12, 34, false).show();
	}

	public static void showToast(Activity activity) {
		Toast.makeText(activity, "Toast example", Toast.LENGTH_LONG).show();
	}

	public static void showCalendar(Activity activity) {
		replaceFragment(android.R.id.content, CalendarFragment.getInstance(),
				activity);
	}

	public static void closeCalendar(Activity activity) {
		replaceFragment(android.R.id.content, MainFragment.getInstance(),
				activity);
	}

	public static void replaceFragment(int resId, Fragment fragment,
			Activity activity) {
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(resId, fragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}
}