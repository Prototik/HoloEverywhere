package org.holoeverywhere.demo;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;

public class DemoApplication extends Application {
	static {
		getConfig().setUseThemeManager(true).setAlwaysUseParentTheme(true)
				.setDebugMode(true);
		LayoutInflater.remap(getConfig().getHoloEverywherePackage()
				+ ".demo.widget", "WidgetContainer", "OtherButton");
		ThemeManager.modify(ThemeManager.FULLSCREEN);
	}
}
