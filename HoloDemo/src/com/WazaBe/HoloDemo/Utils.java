package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.WazaBe.HoloEverywhere.AlertDialog;
import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.ProgressDialog;

public class Utils {
	public final static int THEME_CLASSIC = -1;
	public final static int THEME_DARK = 0;
	public final static int THEME_LIGHT = 1;
	private static int sTheme;

	public static void showDialog(final Activity c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		View v = FontLoader.inflate(c, R.layout.about);
		setLink(v, R.id.gplus, "https://plus.google.com/108315424589085456181");
		setLink(v, R.id.github,
				"https://github.com/ChristopheVersieux/HoloEverywhere");
		builder.setView(v);
		builder.setTitle("About me");
		builder.show();
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

	public static void changeToTheme(Activity a, int theme) {
		sTheme = theme;
		a.finish();
		a.startActivity(new Intent(a, a.getClass()));
	}

	public static void onActivityCreateSetTheme(Activity a) {
		switch (sTheme) {
		default:
		case THEME_CLASSIC:
			a.setTheme(android.R.style.Theme_Black);
			break;
		case THEME_DARK:
			a.setTheme(R.style.Holo_Theme);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Holo_Theme_Light);
			break;
		}
	}

	public static void onActivityCreateSetSherlockTheme(Activity a) {
		switch (sTheme) {
		default:
		case THEME_CLASSIC:
			a.setTheme(R.style.Holo_Theme_Default_Sherlock);
			break;
		case THEME_DARK:
			a.setTheme(R.style.Holo_Theme_Sherlock);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Holo_Theme_Sherlock_Light);
			break;
		}
	}

	public static void showProgressDialog(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.setMessage("I can close!");
		dialog.show();
	}

	public static void showPreferences(Activity activity, boolean abs) {
		activity.startActivity(new Intent(activity,
				abs ? ActionBarHoloPreferenceActivity.class
						: HoloPreferenceActivity.class));
	}
}