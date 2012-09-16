package com.WazaBe.HoloDemo.sherlock;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloDemo.Utils;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.Fragment;
import com.WazaBe.HoloEverywhere.sherlock.SActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

public class SHoloDemoActivity extends SActivity {
	private final class FragmentListener implements TabListener {
		private final Class<? extends Fragment> clazz;
		private Fragment fragment;

		public FragmentListener(Class<? extends Fragment> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (fragment == null) {
				try {
					fragment = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ft.replace(android.R.id.content, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}
	}

	private void addTab(Class<? extends Fragment> clazz, String title) {
		Tab tab = getSupportActionBar().newTab();
		tab.setText(title);
		tab.setTabListener(new FragmentListener(clazz));
		getSupportActionBar().addTab(tab);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setForceThemeApply(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		addTab(SMainFragment.class, "Holo Demo");
		addTab(SPreferenceFragment.class, "Settings");
		addTab(SCalendarFragment.class, "Calendar");
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