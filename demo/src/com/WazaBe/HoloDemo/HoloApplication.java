package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.app.Application;

public class HoloApplication extends Application {
	public HoloApplication() {
		LayoutInflater
				.putToMap("com.WazaBe.HoloDemo.widget", "WidgetContainer");
	}
}
