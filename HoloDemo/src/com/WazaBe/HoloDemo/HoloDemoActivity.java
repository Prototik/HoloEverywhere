package com.WazaBe.HoloDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;

public class HoloDemoActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		String [] items={"Item 1","Item 2","Item 3","Item 4","Item 5"};
		Spinner s = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items) {

	         public View getView(int position, View convertView, ViewGroup parent) {
	                 View v = super.getView(position, convertView, parent);

	                 Typeface externalFont=Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
	                 ((TextView) v).setTypeface(externalFont);
	                 return v;
	         }


	         public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
	                  View v =super.getDropDownView(position, convertView, parent);

	                 Typeface externalFont=Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
	                 ((TextView) v).setTypeface(externalFont);
	                 return v;
	         }
	 };
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		



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

	public void setDarkTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_DARK);
	}

	public void setLightTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_LIGHT);
	}

}