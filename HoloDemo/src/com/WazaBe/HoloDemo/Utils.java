package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;

public class Utils {


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

}