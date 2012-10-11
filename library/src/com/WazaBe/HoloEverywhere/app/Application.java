package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.Settings;

public class Application extends android.app.Application {
	private static Application lastInstance;

	static {
		Settings.init();
	}

	public static Application getLastInstance() {
		return lastInstance;
	}

	public Application() {
		lastInstance = this;
	}
}
