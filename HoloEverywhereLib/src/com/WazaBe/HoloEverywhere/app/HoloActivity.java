package com.WazaBe.HoloEverywhere.app;

import com.WazaBe.HoloEverywhere.FontLoader;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public abstract class HoloActivity extends Activity {
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		FontLoader.loadFont(view);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		FontLoader.loadFont(view);
	}
}
