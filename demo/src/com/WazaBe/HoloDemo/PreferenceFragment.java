package com.WazaBe.HoloDemo;

import android.os.Bundle;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.sherlock.SPreferenceFragment;

public class PreferenceFragment extends SPreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
