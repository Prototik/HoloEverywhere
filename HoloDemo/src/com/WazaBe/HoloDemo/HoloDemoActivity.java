package com.WazaBe.HoloDemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;
import com.WazaBe.HoloEverywhere.HoloToast;
import com.WazaBe.HoloEverywhere.app.HoloListActivity;

public class HoloDemoActivity extends HoloListActivity {
	static final String[] itemsCheckedTextView = { "List: CheckedTextView",
			"List: Other CheckedTextView" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		String[] items = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
		Spinner s = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items) {

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				return FontLoader.loadFont(super.getDropDownView(position,
						convertView, parent));
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return FontLoader.loadFont(super.getView(position, convertView,
						parent));
			}
		};
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (s != null) {
			s.setAdapter(adapter);
		}

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.custom_checkedtextview_holo_dark, itemsCheckedTextView));

	}

	public void setClassicTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_CLASSIC);
	}

	public void setDarkTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_DARK);
	}

	public void setLightTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_LIGHT);
	}

	public void showAlertDialog(View v) {

		HoloAlertDialogBuilder builder = new HoloAlertDialogBuilder(this);

		builder.setTitle("Work in Progress").setMessage(R.string.hello_dialog)
				.setIcon(R.drawable.ic_launcher);

		// BUTTONS ARE NOT SO EASY TO THEME
		// SEE: http://stackoverflow.com/a/9434511/327402

		Boolean buttonDebug = true;
		if (buttonDebug) {
			builder.setPositiveButton("Positive",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setNegativeButton("Negative",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

						}
					});

			builder.setNeutralButton("Neutral",
					new DialogInterface.OnClickListener() {
						@Override
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

	public void showToast(View v) {
		HoloToast.makeText(this, R.string.hello_toast, Toast.LENGTH_LONG)
				.show();
	}

}