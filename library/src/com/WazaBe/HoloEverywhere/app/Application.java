package com.WazaBe.HoloEverywhere.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import com.WazaBe.HoloEverywhere.Settings;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.ThemeManager.ThemedIntentStarter;

public class Application extends android.app.Application implements
		ThemedIntentStarter {
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

	@Override
	@SuppressLint("NewApi")
	public void holoStartThemedActivity(Intent intent, Bundle options) {
		if (VERSION.SDK_INT >= 16) {
			super.startActivity(intent, options);
		} else {
			super.startActivity(intent);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		if (Settings.isUseParentTheme()) {
			ThemeManager.startActivity(this, intent);
		} else {
			holoStartThemedActivity(intent, null);
		}
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		if (Settings.isUseParentTheme()) {
			ThemeManager.startActivity(this, intent, options);
		} else {
			holoStartThemedActivity(intent, options);
		}
	}
}
