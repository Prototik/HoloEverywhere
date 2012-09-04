package com.WazaBe.HoloDemo;

import android.os.Bundle;

import com.WazaBe.HoloEverywhere.sherlock.SPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class ActionBarHoloPreferenceActivity extends SPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setSubtitle("HoloEverywhere");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
