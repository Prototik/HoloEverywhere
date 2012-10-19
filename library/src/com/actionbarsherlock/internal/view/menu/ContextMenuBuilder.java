package com.actionbarsherlock.internal.view.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.util.EventLog;
import android.view.View;

import com.actionbarsherlock.view.ContextMenu;

public class ContextMenuBuilder extends MenuBuilder implements ContextMenu {
	private final ContextMenuListener listener;

	public ContextMenuBuilder(Context context, ContextMenuListener listener) {
		super(context);
		if (listener == null) {
			throw new IllegalArgumentException(
					"ContextMenuListener can't be null");
		}
		this.listener = listener;
	}

	@Override
	public ContextMenu setHeaderIcon(Drawable icon) {
		return (ContextMenu) super.setHeaderIconInt(icon);
	}

	@Override
	public ContextMenu setHeaderIcon(int iconRes) {
		return (ContextMenu) super.setHeaderIconInt(iconRes);
	}

	@Override
	public ContextMenu setHeaderTitle(CharSequence title) {
		return (ContextMenu) super.setHeaderTitleInt(title);
	}

	@Override
	public ContextMenu setHeaderTitle(int titleRes) {
		return (ContextMenu) super.setHeaderTitleInt(titleRes);
	}

	@Override
	public ContextMenu setHeaderView(View view) {
		return (ContextMenu) super.setHeaderViewInt(view);
	}

	@SuppressLint("NewApi")
	public MenuDialogHelper show(View originalView, IBinder token) {
		listener.createContextMenu(this, originalView, listener);
		if (getVisibleItems().size() > 0) {
			if (VERSION.SDK_INT >= 8) {
				EventLog.writeEvent(50001, 1);
			}
			MenuDialogHelper helper = new MenuDialogHelper(this);
			helper.show(token);
			return helper;
		}
		return null;
	}
}