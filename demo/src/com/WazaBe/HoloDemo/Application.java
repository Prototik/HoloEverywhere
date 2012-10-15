package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;

public class Application extends com.WazaBe.HoloEverywhere.app.Application {
	static {
		Application.getConfig().setUseThemeManager(true)
				.setAlwaysUseParentTheme(true).setDebugMode(true);
		LayoutInflater.remap("com.WazaBe.HoloDemo.widget", "WidgetContainer",
				"OtherButton");
		ThemeManager.modify(ThemeManager.FULLSCREEN);
	}
}
