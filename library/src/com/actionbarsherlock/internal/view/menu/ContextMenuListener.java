package com.actionbarsherlock.internal.view.menu;

import android.view.View;

import com.actionbarsherlock.view.MenuItem;

public interface ContextMenuListener {
	public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
			View view);

	public boolean onContextItemSelected(MenuItem item);

	public void onContextMenuClosed(ContextMenu menu);
}