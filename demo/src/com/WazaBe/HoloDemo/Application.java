package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.Settings;
import com.WazaBe.HoloEverywhere.ThemeManager;

public class Application extends com.WazaBe.HoloEverywhere.app.Application {
	public Application() {
		LayoutInflater.putToMap("com.WazaBe.HoloDemo.widget",
				"WidgetContainer", "OtherButton");
		Settings.setUseThemeManager(true);
		ThemeManager.THEME_DEFAULT |= ThemeManager.FULLSCREEN;
	}
}
