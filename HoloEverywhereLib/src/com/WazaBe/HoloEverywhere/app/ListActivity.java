package com.WazaBe.HoloEverywhere.app;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;

public abstract class ListActivity extends android.app.ListActivity {
	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.loadFont(view), params);
	}

	@Override
	public void setContentView(int layoutResID) {
		setContentView(FontLoader.inflate(this, layoutResID));
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
