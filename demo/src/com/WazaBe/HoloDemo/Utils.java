package com.WazaBe.HoloDemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.Activity;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.WazaBe.HoloEverywhere.app.DatePickerDialog;
import com.WazaBe.HoloEverywhere.app.DatePickerDialog.OnDateSetListener;
import com.WazaBe.HoloEverywhere.app.ProgressDialog;
import com.WazaBe.HoloEverywhere.app.Toast;
import com.WazaBe.HoloEverywhere.widget.DatePicker;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;

public class Utils {
	public static void onViewCreated(View view) {
		/*
		 * AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)
		 * view .findViewById(R.id.autoCompleteTextView); adapter =
		 * ArrayAdapter.createFromResource(view.getContext(), R.array.countries,
		 * android.R.layout.simple_dropdown_item_1line);
		 * autoCompleteTextView.setAdapter(adapter);
		 */
	}

	private static void setLink(View v, int id, final String link) {
		v.findViewById(id).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
				intent = Intent.createChooser(intent, "Select browser");
				if (intent != null) {
					v.getContext().startActivity(intent);
				}
			}
		});
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

	public static void showDialog(Activity c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		View v = LayoutInflater.inflate(c, R.layout.about);
		setLink(v, R.id.gplus, "https://plus.google.com/108315424589085456181");
		setLink(v, R.id.github,
				"https://github.com/ChristopheVersieux/HoloEverywhere");
		builder.setTitle("About me");
		builder.setView(v);
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

	public static void showToast(Activity activity) {
		Toast.makeText(activity, "Toast example",
				android.widget.Toast.LENGTH_LONG).show();
	}

	public static void showDatePicker(final Activity activity) {
		DatePickerDialog dialog = new DatePickerDialog(activity,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						String s = new SimpleDateFormat("yyyy-MM-dd")
								.format(calendar.getTime());
						s = "You select date: " + s;
						Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
					}
				}, 2012, 12, 21);
		dialog.show();
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
}