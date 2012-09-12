package com.WazaBe.HoloDemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.WazaBe.HoloEverywhere.AlertDialog;
import com.WazaBe.HoloEverywhere.ArrayAdapter;
import com.WazaBe.HoloEverywhere.Toast;
import com.WazaBe.HoloEverywhere.app.Activity;

public class HoloDemoActivity extends Activity {
	static final String[] itemsCheckedTextView = { "List: CheckedTextView",
			"List: Other CheckedTextView" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String[] items = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
		Spinner s = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Work in Progress").setMessage("Dialog")
				.setIcon(R.drawable.icon);
		builder.setPositiveButton("Positive", null);
		builder.setNegativeButton("Negative", null);
		builder.setNeutralButton("Neutral", null);
		builder.show();
	}

	public void showDialog(View v) {
		Utils.showDialog(this);
	}

	public void showPreferences(View v) {
		Utils.showPreferences(this, false);
	}

	public void showProgressDialog(View v) {
		Utils.showProgressDialog(this);
	}

	public void showToast(View v) {
		Toast.makeText(this, "Toast", android.widget.Toast.LENGTH_LONG).show();
	}

}