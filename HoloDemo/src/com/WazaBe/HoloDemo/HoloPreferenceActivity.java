package com.WazaBe.HoloDemo;

import android.os.Bundle;

import com.WazaBe.HoloEverywhere.preference.PreferenceActivity;

public class HoloPreferenceActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
