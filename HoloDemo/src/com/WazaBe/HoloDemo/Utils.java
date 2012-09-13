package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.WazaBe.HoloEverywhere.ArrayAdapter;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.WazaBe.HoloEverywhere.app.ProgressDialog;
import com.WazaBe.HoloEverywhere.app.Toast;

public class Utils {
	private static int sTheme;
	public final static int THEME_DARK = 0;
	public final static int THEME_LIGHT = 1;

	public static void changeToTheme(Activity a, int theme) {
		sTheme = theme;
		a.finish();
		a.startActivity(new Intent(a, a.getClass()));
	}

	public static void onActivityCreateSetSherlockTheme(Activity a) {
		switch (sTheme) {
		default:
		case THEME_DARK:
			a.setTheme(R.style.Holo_Theme_Sherlock);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Holo_Theme_Sherlock_Light);
			break;
		}
	}

	public static void onActivityCreateSetTheme(Activity a) {
		switch (sTheme) {
		default:
		case THEME_DARK:
			a.setTheme(R.style.Holo_Theme);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Holo_Theme_Light);
			break;
		}
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

	public static void showPreferences(Activity activity, boolean abs) {
		activity.startActivity(new Intent(activity,
				abs ? ActionBarHoloPreferenceActivity.class
						: HoloPreferenceActivity.class));
	}

	public static void showProgressDialog(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.setMessage("I can close!");
		dialog.show();
	}

	public static void onCreate(Activity activity) {
		activity.setContentView(R.layout.main);
		Spinner s = (Spinner) activity.findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				activity, R.array.countries,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) activity
				.findViewById(R.id.autoCompleteTextView);
		adapter = ArrayAdapter.createFromResource(activity, R.array.countries,
				android.R.layout.simple_dropdown_item_1line);
		autoCompleteTextView.setAdapter(adapter);
	}

	public static void showAlertDialog(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Work in Progress").setMessage("Dialog")
				.setIcon(R.drawable.icon);
		builder.setPositiveButton("Positive", null);
		builder.setNegativeButton("Negative", null);
		builder.setNeutralButton("Neutral", null);
		builder.show();
	}

	public static void showToast(Activity activity) {
		Toast.makeText(activity, "Toast example", Toast.LENGTH_LONG).show();
	}
}