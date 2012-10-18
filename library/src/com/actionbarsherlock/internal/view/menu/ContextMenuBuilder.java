package com.actionbarsherlock.internal.view.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.util.EventLog;
import android.view.View;

public class ContextMenuBuilder extends MenuBuilder implements ContextMenu {
	public static interface ContextMenuCreator {
		public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
				View view);
	}

	private final ContextMenuCreator creator;

	public ContextMenuBuilder(Context context) {
		super(context);
		if (!(context instanceof ContextMenuCreator)) {
			throw new IllegalArgumentException(
					"Context must be implement ContextMenuCreator interface");
		}
		creator = (ContextMenuCreator) context;
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
		creator.createContextMenu(this, originalView);
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