package com.WazaBe.HoloDemo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.HoloAlertDialogBuilder;

public class HoloDemoActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.app_name);
		setContentView(R.layout.main);

	}

	public void showDialog(View v) {

		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		HoloAlertDialogBuilder builder = new HoloAlertDialogBuilder(this);

		builder.setTitle("Work in Progress").setMessage(R.string.hello_dialog);
		
		builder.create().show();
		
		// BUTTONS ARE NOT SO EASY TO THEME
		// SEE: http://stackoverflow.com/a/9434511/327402

		// .setPositiveButton(android.R.string.ok, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		//
		// }
		// });
		
	}
}