package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;

public class Utils {

	public final static int THEME_DARK=0;
	public final static int THEME_LIGHT=1;
	private static int sTheme;

	public static void showDialog(final Activity c) {

		HoloAlertDialogBuilder builder = new HoloAlertDialogBuilder(c);
		View v=c.getLayoutInflater().inflate(R.layout.about,null);
		
		LinearLayout profile1 = (LinearLayout) v.findViewById(R.id.gplus);
		profile1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent profileIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("https://plus.google.com/108315424589085456181"));
				c.startActivity(profileIntent);

			}
		});

		LinearLayout profile2 = (LinearLayout) v.findViewById(R.id.github);
		profile2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent profileIntent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://github.com/ChristopheVersieux/HoloEverywhere"));
				c.startActivity(profileIntent);
			}
		});
		
		builder.setView(v);
		builder.setTitle("About me");
		
		AlertDialog dialog = builder.create();
		


		dialog.show();

	}


	public static void changeToTheme (Activity a,int theme){
		sTheme=theme;
		a.finish();
		a.startActivity(new Intent(a,a.getClass()));
		
	}
	
	public static void onActivityCreateSetTheme(Activity a){
		switch (sTheme){
		default:
		case THEME_DARK:
			a.setTheme(R.style.Theme_HoloEverywhereDark);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Theme_HoloEverywhereLight);
			break;
		}
		
	}
	
	public static void onActivityCreateSetSherlockTheme(Activity a){
		switch (sTheme){
		default:
		case THEME_DARK:
			a.setTheme(R.style.Theme_HoloEverywhereDark_Sherlock);
			break;
		case THEME_LIGHT:
			a.setTheme(R.style.Theme_HoloEverywhereLight_Sherlock);
			break;
		}
		
	}

}