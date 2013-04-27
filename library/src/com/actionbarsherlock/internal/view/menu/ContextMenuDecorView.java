
package com.actionbarsherlock.internal.view.menu;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.widget.FrameLayout;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public class ContextMenuDecorView extends FrameLayout implements
        MenuPresenter.Callback, MenuBuilder.Callback {
    public interface ContextMenuListenersProvider {
        public ContextMenuListener getContextMenuListener(View view);
    }

    private ContextMenuBuilder mContextMenu;
    private ContextMenuListener mListener;
    private MenuDialogHelper mMenuDialogHelper;
    private ContextMenuListenersProvider mProvider;

    private final String TAG = getClass().getSimpleName();

    public ContextMenuDecorView(Context context) {
        super(context);
    }

    public ContextMenuDecorView(Context context, View view, ViewGroup.LayoutParams params) {
        this(context);
        if (view != null) {
            attachView(view, params);
        }
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
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
        }
        setLayoutParams(params);
        addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
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
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setProvider(ContextMenuListenersProvider provider) {
        mProvider = provider;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            return super.showContextMenuForChild(originalView);
        }
        mListener = mProvider.getContextMenuListener(originalView);
        if (mListener == null) {
            return false;
        }
        if (mContextMenu == null) {
            mContextMenu = new ContextMenuBuilder(getContext(), mListener);
            mContextMenu.setCallback(this);
        } else {
            mContextMenu.clearAll();
            mContextMenu.setContextMenuListener(mListener);
        }
        mMenuDialogHelper = mContextMenu.show(originalView, originalView.getWindowToken());
        if (mMenuDialogHelper != null) {
            mMenuDialogHelper.setPresenterCallback(this);
            return true;
        } else {
            return false;
        }
    }

    public View unwrap() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }
}
