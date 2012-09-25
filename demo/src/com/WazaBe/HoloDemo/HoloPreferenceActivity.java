package com.WazaBe.HoloDemo;

import android.os.Bundle;

import com.WazaBe.HoloEverywhere.sherlock.SPreferenceActivity;

public class HoloPreferenceActivity extends SPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setForceThemeApply(true);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
