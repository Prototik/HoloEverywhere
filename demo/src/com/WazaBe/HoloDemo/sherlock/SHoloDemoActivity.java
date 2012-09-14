package com.WazaBe.HoloDemo.sherlock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloDemo.Utils;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.sherlock.SActivity;

public class SHoloDemoActivity extends SActivity {
	private final class SDemoAdapter extends FragmentPagerAdapter {
		public SDemoAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new SMainFragment();
			case 1:
				return new SPreferenceFragment();
			case 2:
				return new SCalendarFragment();
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Main";
			case 1:
				return "Preference";
			case 2:
				return "Calendar";
			}
			return super.getPageTitle(position);
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setForceThemeApply(true);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.app_name);
		setContentView(R.layout.content);
		ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(new SDemoAdapter());
	}

	public void setDarkTheme(View v) {
		ThemeManager.restartWithTheme(this, ThemeManager.HOLO_DARK);
	}

	public void setLightTheme(View v) {
		ThemeManager.restartWithTheme(this, ThemeManager.HOLO_LIGHT);
	}

	public void showAlertDialog(View v) {
		Utils.showAlertDialog(this);
	}

	public void showDialog(View v) {
		Utils.showDialog(this);
	}

	public void showProgressDialog(View v) {
		Utils.showProgressDialog(this);
	}

	public void showToast(View v) {
		Utils.showToast(this);
	}

}