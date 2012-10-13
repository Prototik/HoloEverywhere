package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.Settings;
import com.WazaBe.HoloEverywhere.ThemeManager;

public class Application extends com.WazaBe.HoloEverywhere.app.Application {
	static {
		LayoutInflater.remap("com.WazaBe.HoloDemo.widget", "WidgetContainer",
				"OtherButton");
		Settings.setUseThemeManager(true);
		Settings.setUseParentTheme(true);
		ThemeManager.modify(ThemeManager.FULLSCREEN);
	}
}
