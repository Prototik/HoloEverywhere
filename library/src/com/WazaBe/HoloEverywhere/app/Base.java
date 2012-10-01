package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public interface Base {
	public LayoutInflater getLayoutInflater();

	public SharedPreferences getSupportSharedPreferences(String name, int mode);

	public boolean isABSSupport();

	public boolean isForceThemeApply();

	public void onSupportBackPressed();
}
