package com.WazaBe.HoloDemo;

import android.os.Bundle;
import android.view.View;

import com.WazaBe.HoloEverywhere.app.Activity;

public class HoloDemoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);
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
		Utils.showPreferences(this, false);
	}

	public void showProgressDialog(View v) {
		Utils.showProgressDialog(this);
	}

	public void showToast(View v) {
		Utils.showToast(this);
	}
}