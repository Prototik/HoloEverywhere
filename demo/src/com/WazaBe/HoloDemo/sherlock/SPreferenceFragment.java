package com.WazaBe.HoloDemo.sherlock;

import android.os.Bundle;

import com.WazaBe.HoloDemo.R;

public class SPreferenceFragment extends
		com.WazaBe.HoloEverywhere.sherlock.SPreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public String toString() {
		return "Preferences";
	}
}
