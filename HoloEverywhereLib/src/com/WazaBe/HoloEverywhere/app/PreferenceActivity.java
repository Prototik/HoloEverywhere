package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.FontLoader;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class PreferenceActivity extends android.preference.PreferenceActivity {
	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.loadFont(view), params);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(FontLoader.inflate(this, layoutResID));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(FontLoader.loadFont(view));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(FontLoader.loadFont(view), params);
	}
}
