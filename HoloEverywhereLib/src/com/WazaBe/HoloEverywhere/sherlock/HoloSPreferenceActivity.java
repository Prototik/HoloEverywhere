package com.WazaBe.HoloEverywhere.sherlock;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public abstract class HoloSPreferenceActivity extends
		SherlockPreferenceActivity {
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
