package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;

public class HoloDemoActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	public void showAlertDialog(View v) {

		HoloAlertDialogBuilder builder = new HoloAlertDialogBuilder(this);

		builder.setTitle("Work in Progress").setMessage(R.string.hello_dialog)
				.setIcon(R.drawable.ic_launcher);

		// BUTTONS ARE NOT SO EASY TO THEME
		// SEE: http://stackoverflow.com/a/9434511/327402

		Boolean buttonDebug = false;
		if (buttonDebug) {
			builder.setPositiveButton("Positive",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setNegativeButton("Negative",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setNeutralButton("Neutral",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
		}

		AlertDialog alert = builder.create();
		alert.show();

	}

	public void showDialog(View v) {
		Utils.showDialog(this);
	}

}