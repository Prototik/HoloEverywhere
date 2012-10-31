package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager.ThemedIntentStarter;
import org.holoeverywhere.app.Application.Config;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;
import org.holoeverywhere.preference.SharedPreferences;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.view.MenuInflater;

public interface Base extends ThemedIntentStarter, ContextMenuListener {
	public Config getConfig();

	public SharedPreferences getDefaultSharedPreferences();

	public LayoutInflater getLayoutInflater();

	public SharedPreferences getSharedPreferences(PreferenceImpl impl,
			String name, int mode);

	public SharedPreferences getSharedPreferences(String name, int mode);

	public FragmentManager getSupportFragmentManager();

	public MenuInflater getSupportMenuInflater();

	public boolean isABSSupport();

	public boolean isForceThemeApply();

	public View prepareDecorView(View view);

	public android.content.SharedPreferences superGetSharedPreferences(
			String name, int mode);
}
