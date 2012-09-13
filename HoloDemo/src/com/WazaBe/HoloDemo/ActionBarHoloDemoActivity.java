package com.WazaBe.HoloDemo;

import android.os.Bundle;
import android.view.View;

import com.WazaBe.HoloEverywhere.sherlock.SActivity;

public class ActionBarHoloDemoActivity extends SActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.onActivityCreateSetSherlockTheme(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.app_name);
		Utils.onCreate(this);
	}

	public void setDarkTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_DARK);
	}

	public void setLightTheme(View v) {
		Utils.changeToTheme(this, Utils.THEME_LIGHT);
	}

	public void showAlertDialog(View v) {
		Utils.showAlertDialog(this);
	}

	public void showDialog(View v) {
		Utils.showDialog(this);
	}

	public void showPreferences(View v) {
		Utils.showPreferences(this, true);
	}

	public void showProgressDialog(View v) {
		Utils.showProgressDialog(this);
	}

	public void showToast(View v) {
		Utils.showToast(this);
	}
}