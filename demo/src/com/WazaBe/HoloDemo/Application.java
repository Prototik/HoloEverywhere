package com.WazaBe.HoloDemo;

import com.WazaBe.HoloEverywhere.LayoutInflater;

public class Application extends com.WazaBe.HoloEverywhere.app.Application {
	public Application() {
		LayoutInflater.putToMap("com.WazaBe.HoloDemo.widget",
				"WidgetContainer", "OtherButton");
	}
}
