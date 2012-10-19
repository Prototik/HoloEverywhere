package com.actionbarsherlock.internal.view.menu;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public final class ContextMenuDecorView extends FrameLayout {
	private static final class InternalWrapper implements
			MenuPresenter.Callback, MenuBuilder.Callback {
		private final ContextMenuListener listener;

		public InternalWrapper(ContextMenuListener listener) {
			if (listener == null) {
				throw new IllegalArgumentException("Listener is null",
						new NullPointerException());
			}
			this.listener = listener;
		}

		@Override
		public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
			listener.onContextMenuClosed((ContextMenu) menu);
		}

		@Override
		public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
			return listener.onContextItemSelected(item);
		}

		@Override
		public void onMenuModeChange(MenuBuilder menu) {

		}

		@Override
		public boolean onOpenSubMenu(MenuBuilder subMenu) {
			return false;
		}

		public ContextMenuListener unwrap() {
			return listener;
		}
	}

	private ContextMenuBuilder contextMenu;
	private final InternalWrapper listener;
	private MenuDialogHelper menuDialogHelper;

	public ContextMenuDecorView(View view, ContextMenuListener listener) {
		super(view.getContext());
		this.listener = new InternalWrapper(listener);
		ViewParent parent = view.getParent();
		if (parent != null && parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(view);
		}
		addView(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public boolean showContextMenuForChild(View originalView) {
		if (contextMenu == null) {
			contextMenu = new ContextMenuBuilder(getContext(),
					listener.unwrap());
			contextMenu.setCallback(listener);
		} else {
			contextMenu.clearAll();
		}
		final MenuDialogHelper helper = contextMenu.show(originalView,
				originalView.getWindowToken());
		if (helper != null) {
			helper.setPresenterCallback(listener);
		}
		menuDialogHelper = helper;
		return menuDialogHelper != null;
	}
}
