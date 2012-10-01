package com.WazaBe.HoloEverywhere.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app._ActionBarSherlockTrojanHorse;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.Settings;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.internal.BaseSharedPreferences;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public abstract class Activity extends _ActionBarSherlockTrojanHorse implements
		Base {
	private boolean forceThemeApply = false;

	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.apply(view), params);
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this);
	}

	@Override
	public SharedPreferences getSupportSharedPreferences(String name, int mode) {
		return new BaseSharedPreferences(getSharedPreferences(name, mode));
	}

	@Override
	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(super.getSystemService(name));
	}

	@Override
	public boolean isABSSupport() {
		return false;
	}

	@Override
	public boolean isForceThemeApply() {
		return forceThemeApply;
	}

	@Override
	public void onBackPressed() {
		if (!getSupportFragmentManager().popBackStackImmediate()) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Settings.isUseThemeManager()) {
			ThemeManager.applyTheme(this);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	@SuppressLint("NewApi")
	public void onSupportBackPressed() {
		onBackPressed();
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

	public void setForceThemeApply(boolean forceThemeApply) {
		this.forceThemeApply = forceThemeApply;
	}
}
