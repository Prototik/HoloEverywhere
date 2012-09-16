package com.WazaBe.HoloEverywhere.sherlock;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.preference.PreferenceActivity;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class SPreferenceActivity extends PreferenceActivity implements
		OnCreatePanelMenuListener, OnPreparePanelListener,
		OnMenuItemSelectedListener, OnActionModeStartedListener,
		OnActionModeFinishedListener, SBase {
	private ActionBarSherlock mSherlock;

	@Override
	public void addContentView(View view, LayoutParams params) {
		getSherlock().addContentView(view, params);
	}

	@Override
	public void closeOptionsMenu() {
		if (!getSherlock().dispatchCloseOptionsMenu()) {
			super.closeOptionsMenu();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (getSherlock().dispatchKeyEvent(event)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public final ActionBarSherlock getSherlock() {
		if (mSherlock == null) {
			mSherlock = ActionBarSherlock.wrap(this,
					ActionBarSherlock.FLAG_DELEGATE);
		}
		return mSherlock;
	}

	@Override
	public ActionBar getSupportActionBar() {
		return getSherlock().getActionBar();
	}

	@Override
	public MenuInflater getSupportMenuInflater() {
		return getSherlock().getMenuInflater();
	}

	@Override
	public void invalidateOptionsMenu() {
		getSherlock().dispatchInvalidateOptionsMenu();
	}

	@Override
	public boolean isABSSupport() {
		return true;
	}

	@Override
	public void onActionModeFinished(ActionMode mode) {
	}

	@Override
	public void onActionModeStarted(ActionMode mode) {
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getSherlock().dispatchConfigurationChanged(newConfig);
	}

	@Override
	public final boolean onCreateOptionsMenu(android.view.Menu menu) {
		return getSherlock().dispatchCreateOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		if (featureId == android.view.Window.FEATURE_OPTIONS_PANEL) {
			return onCreateOptionsMenu(menu);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		getSherlock().dispatchDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (featureId == android.view.Window.FEATURE_OPTIONS_PANEL) {
			return onOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
		if (getSherlock().dispatchMenuOpened(featureId, menu)) {
			return true;
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		return getSherlock().dispatchOptionsItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onPanelClosed(int featureId, android.view.Menu menu) {
		getSherlock().dispatchPanelClosed(featureId, menu);
		super.onPanelClosed(featureId, menu);
	}

	@Override
	protected void onPause() {
		getSherlock().dispatchPause();
		super.onPause();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		getSherlock().dispatchPostCreate(savedInstanceState);
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getSherlock().dispatchPostResume();
	}

	@Override
	public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
		return getSherlock().dispatchPrepareOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		if (featureId == android.view.Window.FEATURE_OPTIONS_PANEL) {
			return onPrepareOptionsMenu(menu);
		}
		return false;
	}

	@Override
	protected void onStop() {
		getSherlock().dispatchStop();
		super.onStop();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		getSherlock().dispatchTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}

	@Override
	public void openOptionsMenu() {
		if (!getSherlock().dispatchOpenOptionsMenu()) {
			super.openOptionsMenu();
		}
	}

	public void requestWindowFeature(long featureId) {
		getSherlock().requestFeature((int) featureId);
	}

	@Override
	public void setContentView(int layoutResId) {
		getSherlock().setContentView(layoutResId);
	}

	@Override
	public void setContentView(View view) {
		getSherlock().setContentView(view);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		getSherlock().setContentView(view, params);
	}

	public void setSupportProgress(int progress) {
		getSherlock().setProgress(progress);
	}

	public void setSupportProgressBarIndeterminate(boolean indeterminate) {
		getSherlock().setProgressBarIndeterminate(indeterminate);
	}

	public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
		getSherlock().setProgressBarIndeterminateVisibility(visible);
	}

	public void setSupportProgressBarVisibility(boolean visible) {
		getSherlock().setProgressBarVisibility(visible);
	}

	public void setSupportSecondaryProgress(int secondaryProgress) {
		getSherlock().setSecondaryProgress(secondaryProgress);
	}

	@Override
	public ActionMode startActionMode(ActionMode.Callback callback) {
		return getSherlock().startActionMode(callback);
	}

	@SuppressLint("NewApi")
	@Override
	public void supportInvalidateOptionsMenu() {
		invalidateOptionsMenu();
	}
}