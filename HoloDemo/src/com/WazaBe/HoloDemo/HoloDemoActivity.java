package com.WazaBe.HoloDemo;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class HoloDemoActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.app_name);
		setContentView(R.layout.main);

	}

	public void showDialog(View v) {

		// AlertDialog.Builder builder = new
		// AlertDialog.Builder(this,R.style.HoloDialog);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Work in Progress").setMessage(R.string.hello_dialog);
		// SEE: http://stackoverflow.com/a/9434511/327402
		// .setPositiveButton(android.R.string.ok, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		//
		// }
		// });
		builder.create().show();

	}
}