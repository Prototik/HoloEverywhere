
package com.actionbarsherlock.internal.view.menu;

import java.lang.reflect.Method;

import org.holoeverywhere.app.Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.util.EventLog;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.view.ContextMenu;

public class ContextMenuBuilder extends MenuBuilder implements ContextMenu {
    public static interface ContextMenuInfoGetter {
        public ContextMenuInfo getContextMenuInfo();
    }

    private ContextMenuInfo contextMenuInfo;
    private final ContextMenuListener listener;

    private final String TAG = getClass().getSimpleName();

    public ContextMenuBuilder(Context context, ContextMenuListener listener) {
        super(context);
        if (listener == null) {
            throw new IllegalArgumentException(
                    "ContextMenuListener can't be null");
        }
        this.listener = listener;
    }

    public ContextMenuInfo getContextMenuInfo() {
        return contextMenuInfo;
    }

    private ContextMenuInfo getContextMenuInfo(View view) {
        if (view instanceof ContextMenuInfoGetter) {
            return ((ContextMenuInfoGetter) view).getContextMenuInfo();
        }
        ContextMenuInfo menuInfo = null;
        try {
            Class<?> clazz = view.getClass();
            while (clazz != View.class) {
                clazz = clazz.getSuperclass();
            }
            Method method = clazz.getDeclaredMethod("getContextMenuInfo");
            method.setAccessible(true);
            menuInfo = (ContextMenuInfo) method.invoke(view);
        } catch (Exception e) {
            if (Application.isDebugMode()) {
                Log.e(TAG, "getContextMenuInfo error", e);
            }
        }
        return menuInfo;
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
        contextMenuInfo = getContextMenuInfo(originalView);
        listener.createContextMenu(this, originalView, contextMenuInfo,
                listener);
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
