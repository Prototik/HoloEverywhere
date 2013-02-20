
package com.actionbarsherlock.internal.view.menu;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.FrameLayout;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public final class ContextMenuDecorView extends FrameLayout implements
        MenuPresenter.Callback, MenuBuilder.Callback {
    public static ContextMenuDecorView inflateDecorView(LayoutInflater layoutInflater, int layout,
            ContextMenuListener listener) {
        ContextMenuDecorView view = new ContextMenuDecorView(layoutInflater.getContext(), listener);
        layoutInflater.inflate(layout, view, true);
        return view;
    }

    public static ContextMenuDecorView prepareDecorView(Context context, View v,
            ContextMenuListener listener, ViewGroup.LayoutParams params, int decorViewId) {
        if (v instanceof ContextMenuDecorView) {
            return (ContextMenuDecorView) v;
        }
        if (v != null) {
            ContextMenuDecorView decorView = new ContextMenuDecorView(context, v, params, listener);
            if (decorViewId > 0) {
                decorView.setId(decorViewId);
            }
            return decorView;
        } else {
            return null;
        }
    }

    private ContextMenuBuilder contextMenu;
    private MenuDialogHelper menuDialogHelper;
    private final ContextMenuListener mListener;

    private final String TAG = getClass().getSimpleName();

    private ContextMenuDecorView(Context context,
            ContextMenuListener listener) {
        super(context);
        mListener = listener;
    }

    private ContextMenuDecorView(Context context, View view, ViewGroup.LayoutParams params,
            ContextMenuListener listener) {
        this(context, listener);
        attachView(view, params);
    }

    public synchronized void attachView(View view, ViewGroup.LayoutParams params) {
        if (view == null) {
            throw new NullPointerException("View cannot be null");
        }
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
        removeAllViews();
        if (params == null) {
            params = view.getLayoutParams();
        }
        if (params == null) {
            params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        }
        addView(view, params);
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
            setLayoutParams(params);
        }
        ViewGroup.LayoutParams childParams = child.getLayoutParams();
        if (childParams == null) {
            child.setLayoutParams(params);
            return params;
        }
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
