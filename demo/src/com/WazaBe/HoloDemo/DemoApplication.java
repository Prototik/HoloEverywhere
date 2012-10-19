package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.Application;

public class DemoApplication extends Application {
	static {
		getConfig().setUseThemeManager(true).setAlwaysUseParentTheme(true)
				.setDebugMode(true);
		LayoutInflater.remap("com.WazaBe.HoloDemo.widget", "WidgetContainer",
				"OtherButton");
		ThemeManager.modify(ThemeManager.FULLSCREEN);
	}
}
