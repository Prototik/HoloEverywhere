package com.WazaBe.HoloEverywhere.app;

import android.support.v4.app.FragmentManager;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager.ThemedIntentStarter;
import com.WazaBe.HoloEverywhere.app.Application.Config;
import com.WazaBe.HoloEverywhere.app.Application.Config.PreferenceImpl;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;

public interface Base extends ThemedIntentStarter, ContextMenuListener {
	public Config getConfig();

	public SharedPreferences getDefaultSharedPreferences();

	public LayoutInflater getLayoutInflater();

	public SharedPreferences getSharedPreferences(PreferenceImpl impl,
			String name, int mode);

	public SharedPreferences getSharedPreferences(String name, int mode);

	public FragmentManager getSupportFragmentManager();

	public boolean isABSSupport();

	public boolean isForceThemeApply();

	public android.content.SharedPreferences superGetSharedPreferences(
			String name, int mode);
}
