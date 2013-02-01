
package com.actionbarsherlock.internal.view.menu;

import org.holoeverywhere.HoloEverywhere;

import android.content.Context;
import android.util.Log;
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
        private final String TAG = getClass().getSimpleName();

        public InternalWrapper(ContextMenuListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Listener cannot be null");
            }
            this.listener = listener;
        }

        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            if (HoloEverywhere.DEBUG) {
                Log.v(TAG, "Calling onContextMenuClosed on " + listener);
            }
            listener.onContextMenuClosed((ContextMenu) menu);
        }

        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            if (HoloEverywhere.DEBUG) {
                Log.v(TAG, "Calling onContextItemSelected on " + listener);
            }
            if (menu instanceof ContextMenuBuilder
                    && item instanceof MenuItemImpl) {
                ((MenuItemImpl) item).setMenuInfo(((ContextMenuBuilder) menu)
                        .getContextMenuInfo());
            }
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

    public static View prepareDecorView(Context context, View v,
            ContextMenuListener listener, int decorViewId) {
        if (v != null) {
            v = new ContextMenuDecorView(context, v, listener);
            if (decorViewId > 0) {
                v.setId(decorViewId);
            }
        }
        return v;
    }

    private ContextMenuBuilder contextMenu;
    private final InternalWrapper listener;
    private MenuDialogHelper menuDialogHelper;

    public ContextMenuDecorView(Context context,
            ContextMenuListener listener) {
        super(context);
        this.listener = new InternalWrapper(listener);
    }

    public ContextMenuDecorView(Context context, View view,
            ContextMenuListener listener) {
        this(context, listener);
        attachView(view);
    }

    @Override
    public void addView(View child) {
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        addView(child, params);
    }

    public synchronized void attachView(View view) {
        if (view == null) {
            throw new NullPointerException("View cannot be null");
        }
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        removeAllViews();
        addView(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            return super.showContextMenuForChild(originalView);
        }
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

    public View unwrap() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }
}
