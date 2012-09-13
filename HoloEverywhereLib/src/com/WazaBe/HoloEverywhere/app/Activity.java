package com.WazaBe.HoloEverywhere.app;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.WazaBe.HoloEverywhere.util.BaseSharedPreferences;

public abstract class Activity extends android.support.v4.app.FragmentActivity {
	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.apply(view), params);
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this);
	}

	public SharedPreferences getSupportSharedPreferences(String name, int mode) {
		return new BaseSharedPreferences(getSharedPreferences(name, mode));
	}

	@Override
	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(super.getSystemService(name));
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(FontLoader.inflate(this, layoutResID));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(FontLoader.apply(view));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(FontLoader.apply(view), params);
	}

	@Override
	public void onBackPressed() {
		if (!getSupportFragmentManager().popBackStackImmediate()) {
			finish();
		}
	}

	@SuppressLint("NewApi")
	public void onSupportBackPressed() {
		onBackPressed();
	}
}
