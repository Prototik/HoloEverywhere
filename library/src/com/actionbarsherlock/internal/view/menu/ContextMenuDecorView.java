
package com.actionbarsherlock.internal.view.menu;

import org.holoeverywhere.HoloEverywhere;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public final class ContextMenuDecorView extends FrameLayout implements
        MenuPresenter.Callback, MenuBuilder.Callback {
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

    private MenuDialogHelper menuDialogHelper;

    private final ContextMenuListener mListener;

    private final String TAG = getClass().getSimpleName();

    public ContextMenuDecorView(Context context,
            ContextMenuListener listener) {
        super(context);
        mListener = listener;
    }

    public ContextMenuDecorView(Context context, View view,
            ContextMenuListener listener) {
        this(context, listener);
        attachView(view);
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

    public ContextMenuListener getContextMenuListener() {
        return mListener;
    }

    @Override
    @ExportedProperty(deepExport = true, prefix = "layout_")
    public ViewGroup.LayoutParams getLayoutParams() {
        if (getChildCount() == 0) {
            return super.getLayoutParams();
        }
        final View child = unwrap();
        ViewGroup.LayoutParams params = super.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ViewGroup.LayoutParams childParams = child.getLayoutParams();
        boolean modified = false;
        if (params.width != childParams.width) {
            params.width = childParams.width;
            modified = true;
        }
        if (params.height != childParams.height) {
            params.height = childParams.height;
            modified = true;
        }
        if (modified) {
            setLayoutParams(params);
        }
        return params;
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (mListener == null) {
            return;
        }
        if (HoloEverywhere.DEBUG) {
            Log.v(TAG, "Calling onContextMenuClosed on " + mListener);
        }
        mListener.onContextMenuClosed((ContextMenu) menu);
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        if (mListener == null) {
            return false;
        }
        if (HoloEverywhere.DEBUG) {
            Log.v(TAG, "Calling onContextItemSelected on " + mListener);
        }
        if (menu instanceof ContextMenuBuilder
                && item instanceof MenuItemImpl) {
            ((MenuItemImpl) item).setMenuInfo(((ContextMenuBuilder) menu)
                    .getContextMenuInfo());
        }
        return mListener.onContextItemSelected(item);
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {

    }

    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        return false;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            return super.showContextMenuForChild(originalView);
        }
        if (contextMenu == null) {
            contextMenu = new ContextMenuBuilder(getContext(), mListener);
            contextMenu.setCallback(this);
        } else {
            contextMenu.clearAll();
        }
        final MenuDialogHelper helper = contextMenu.show(originalView,
                originalView.getWindowToken());
        if (helper != null) {
            helper.setPresenterCallback(this);
        }
        menuDialogHelper = helper;
        return menuDialogHelper != null;
    }

    public View unwrap() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }
}
