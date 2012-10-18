package com.actionbarsherlock.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.actionbarsherlock.view.Menu;

public interface ContextMenu extends Menu {
	public void clearHeader();

	public ContextMenu setHeaderIcon(Drawable icon);

	public ContextMenu setHeaderIcon(int iconRes);

	public ContextMenu setHeaderTitle(CharSequence title);

	public ContextMenu setHeaderTitle(int titleRes);

	public ContextMenu setHeaderView(View view);
}