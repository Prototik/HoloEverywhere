package com.WazaBe.HoloEverywhere.sherlock;

import com.WazaBe.HoloEverywhere.app.Base;
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

public interface SBase extends Base, OnCreatePanelMenuListener,
		OnPreparePanelListener, OnMenuItemSelectedListener,
		OnActionModeStartedListener, OnActionModeFinishedListener {
	public ActionBarSherlock getSherlock();

	public ActionBar getSupportActionBar();

	public MenuInflater getSupportMenuInflater();

	public boolean onCreateOptionsMenu(Menu menu);

	public boolean onOptionsItemSelected(MenuItem item);

	public boolean onPrepareOptionsMenu(Menu menu);

	public void requestWindowFeature(long featureId);

	public void setSupportProgress(int progress);

	public void setSupportProgressBarIndeterminate(boolean indeterminate);

	public void setSupportProgressBarIndeterminateVisibility(boolean visible);

	public void setSupportProgressBarVisibility(boolean visible);

	public void setSupportSecondaryProgress(int secondaryProgress);

	public ActionMode startActionMode(ActionMode.Callback callback);
}
