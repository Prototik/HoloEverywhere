package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager.ThemedIntentStarter;
import com.WazaBe.HoloEverywhere.app.Application.Config;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;

public interface Base extends ThemedIntentStarter {
	public LayoutInflater getLayoutInflater();

	public Config getSettings();

	public SharedPreferences getSupportSharedPreferences(String name, int mode);

	public boolean isABSSupport();

	public boolean isForceThemeApply();
}
