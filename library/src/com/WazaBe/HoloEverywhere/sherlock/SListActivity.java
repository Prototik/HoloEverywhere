package com.WazaBe.HoloEverywhere.sherlock;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.WazaBe.HoloEverywhere.app.ListActivity;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class SListActivity extends ListActivity implements SBase {
	private ActionBarSherlock mSherlock;

	@Override
	public void addContentView(View view, LayoutParams params) {
		if (isABSSupport()) {
			getSherlock().addContentView(view, params);
		} else {
			super.addContentView(view, params);
		}
	}

	@Override
	public void closeOptionsMenu() {
		if (!isABSSupport() || !getSherlock().dispatchCloseOptionsMenu()) {
			super.closeOptionsMenu();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (isABSSupport() && getSherlock().dispatchKeyEvent(event)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public final ActionBarSherlock getSherlock() {
		if (!isABSSupport()) {
			return null;
		}
		if (mSherlock == null) {
			mSherlock = ActionBarSherlock.wrap(this,
					ActionBarSherlock.FLAG_DELEGATE);
		}
		return mSherlock;
	}

	@Override
	public ActionBar getSupportActionBar() {
		return isABSSupport() ? getSherlock().getActionBar() : null;
	}

	@Override
	public MenuInflater getSupportMenuInflater() {
		return isABSSupport() ? getSherlock().getMenuInflater() : null;
	}

	@Override
	public void invalidateOptionsMenu() {
		if (isABSSupport()) {
			getSherlock().dispatchInvalidateOptionsMenu();
		} else if (VERSION.SDK_INT >= 11) {
			super.invalidateOptionsMenu();
		}
	}

	@Override
	public boolean isABSSupport() {
		return VERSION.SDK_INT >= 7;
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
		if (isABSSupport()) {
			getSherlock().dispatchConfigurationChanged(newConfig);
		}
	}

	@Override
	public final boolean onCreateOptionsMenu(android.view.Menu menu) {
		if (isABSSupport()) {
			return getSherlock().dispatchCreateOptionsMenu(menu);
		} else {
			return super.onCreateOptionsMenu(menu);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isABSSupport()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		if (isABSSupport() && featureId == Window.FEATURE_OPTIONS_PANEL) {
			return onCreateOptionsMenu(menu);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		if (isABSSupport()) {
			getSherlock().dispatchDestroy();
		}
		super.onDestroy();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (isABSSupport() && featureId == Window.FEATURE_OPTIONS_PANEL) {
			return onOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
		if (isABSSupport() && getSherlock().dispatchMenuOpened(featureId, menu)) {
			return true;
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (isABSSupport()) {
			return getSherlock().dispatchOptionsItemSelected(item);
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onPanelClosed(int featureId, android.view.Menu menu) {
		if (isABSSupport()) {
			getSherlock().dispatchPanelClosed(featureId, menu);
		}
		super.onPanelClosed(featureId, menu);
	}

	@Override
	protected void onPause() {
		if (isABSSupport()) {
			getSherlock().dispatchPause();
		}
		super.onPause();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		if (isABSSupport()) {
			getSherlock().dispatchPostCreate(savedInstanceState);
		}
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (isABSSupport()) {
			getSherlock().dispatchPostResume();
		}
	}

	@Override
	public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
		if (isABSSupport()) {
			return getSherlock().dispatchPrepareOptionsMenu(menu);
		} else {
			return super.onPrepareOptionsMenu(menu);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		if (isABSSupport() && featureId == Window.FEATURE_OPTIONS_PANEL) {
			return onPrepareOptionsMenu(menu);
		}
		return false;
	}

	@Override
	protected void onStop() {
		if (isABSSupport()) {
			getSherlock().dispatchStop();
		}
		super.onStop();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		if (isABSSupport()) {
			getSherlock().dispatchTitleChanged(title, color);
		}
		super.onTitleChanged(title, color);
	}

	@Override
	public void openOptionsMenu() {
		if (!isABSSupport() || !getSherlock().dispatchOpenOptionsMenu()) {
			super.openOptionsMenu();
		}
	}

	@Override
	public void requestWindowFeature(long featureId) {
		if (isABSSupport()) {
			getSherlock().requestFeature((int) featureId);
		} else {
			try {
				requestWindowFeature((int) featureId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setContentView(int layoutResId) {
		if (isABSSupport()) {
			getSherlock().setContentView(layoutResId);
		} else {
			super.setContentView(layoutResId);
		}
	}

	@Override
	public void setContentView(View view) {
		if (isABSSupport()) {
			getSherlock().setContentView(view);
		} else {
			super.setContentView(view);
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (isABSSupport()) {
			getSherlock().setContentView(view, params);
		} else {
			super.setContentView(view, params);
		}
	}

	@Override
	public void setSupportProgress(int progress) {
		if (isABSSupport()) {
			getSherlock().setProgress(progress);
		} else {
			setProgress(progress);
		}
	}

	@Override
	public void setSupportProgressBarIndeterminate(boolean indeterminate) {
		if (isABSSupport()) {
			getSherlock().setProgressBarIndeterminate(indeterminate);
		} else {
			setProgressBarIndeterminate(indeterminate);
		}
	}

	@Override
	public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
		if (isABSSupport()) {
			getSherlock().setProgressBarIndeterminateVisibility(visible);
		} else {
			setProgressBarIndeterminateVisibility(visible);
		}
	}

	@Override
	public void setSupportProgressBarVisibility(boolean visible) {
		if (isABSSupport()) {
			getSherlock().setProgressBarVisibility(visible);
		} else {
			setProgressBarVisibility(visible);
		}
	}

	@Override
	public void setSupportSecondaryProgress(int secondaryProgress) {
		if (isABSSupport()) {
			getSherlock().setSecondaryProgress(secondaryProgress);
		} else {
			setSecondaryProgress(secondaryProgress);
		}
	}

	@Override
	public ActionMode startActionMode(ActionMode.Callback callback) {
		return isABSSupport() ? getSherlock().startActionMode(callback) : null;
	}

	@SuppressLint("NewApi")
	@Override
	public void supportInvalidateOptionsMenu() {
		if (isABSSupport() || VERSION.SDK_INT >= 11) {
			invalidateOptionsMenu();
		}
	}
}