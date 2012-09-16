package com.WazaBe.HoloEverywhere.sherlock;

import android.view.View;

import com.WazaBe.HoloEverywhere.app.Base;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public interface SBase extends Base {
	public ActionBarSherlock getSherlock();

	public ActionBar getSupportActionBar();

	public MenuInflater getSupportMenuInflater();

	public boolean onCreateOptionsMenu(Menu menu);

	public boolean onCreatePanelMenu(int featureId, Menu menu);

	public boolean onMenuItemSelected(int featureId, MenuItem item);

	public boolean onOptionsItemSelected(MenuItem item);

	public boolean onPreparePanel(int featureId, View view, Menu menu);

	public ActionMode startActionMode(ActionMode.Callback callback);
}
