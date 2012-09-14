package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.Settings;

public class Application extends android.app.Application {
	public Application() {
		Settings.init();
	}
}
