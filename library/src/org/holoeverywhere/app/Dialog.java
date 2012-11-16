
package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public class Dialog extends android.app.Dialog implements ContextMenuListener {
    private static final int checkTheme(Context context, int theme) {
        if (theme >= 0x01000000) {
            return theme;
        }
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.dialogTheme, value, true);
        if (value.resourceId > 0) {
            return value.resourceId;
        }
        return R.style.Holo_Theme_Dialog;
    }

    public Dialog(Context context) {
        this(context, 0);
    }

    public Dialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    public Dialog(Context context, int theme) {
        super(context, checkTheme(context, theme));
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        super.addContentView(prepareDecorView(view), params);
    }

    private void checkWindowSizes() {
        View view = findViewById(R.id.custom);
        if (view == null) {
            view = getWindow().getDecorView();
        }
        if (VERSION.SDK_INT < 11 && view != null) {
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            TypedArray a = getContext().obtainStyledAttributes(R.styleable.HoloActivity);
            final int windowMinWidthMajor = (int) a.getFraction(
                    R.styleable.HoloActivity_windowMinWidthMajor, dm.widthPixels, 1, 0);
            final int windowMinWidthMinor = (int) a.getFraction(
                    R.styleable.HoloActivity_windowMinWidthMinor, dm.widthPixels, 1, 0);
            a.recycle();
            final int orientation;
            if (getContext() instanceof Activity) {
                orientation = ((Activity) getContext()).getRequestedOrientation();
                switch (orientation) {
                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                        view.setMinimumWidth(windowMinWidthMajor);
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                        view.setMinimumWidth(windowMinWidthMinor);
                        break;
                }
            } else {
                orientation = getContext().getResources().getConfiguration().orientation;
                switch (orientation) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        view.setMinimumWidth(windowMinWidthMajor);
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:
                        view.setMinimumWidth(windowMinWidthMinor);
                        break;
                }
            }
        }
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
            View view, ContextMenuInfo menuInfo, ContextMenuListener listener) {
        listener.onCreateContextMenu(contextMenuBuilder, view, menuInfo);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        checkWindowSizes();
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
        return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item instanceof ContextMenuItemWrapper) {
            return super.onContextItemSelected(((ContextMenuItemWrapper) item)
                    .unwrap());
        }
        return false;
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
        if (menu instanceof ContextMenuWrapper) {
            super.onContextMenuClosed(((ContextMenuWrapper) menu).unwrap());
        }
    }

    @Override
    public final void onContextMenuClosed(Menu menu) {
        if (menu instanceof android.view.ContextMenu) {
            onContextMenuClosed(new ContextMenuWrapper(
                    (android.view.ContextMenu) menu));
        } else {
            super.onContextMenuClosed(menu);
        }
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View view, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), view, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        if (menu instanceof ContextMenuWrapper) {
            super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(),
                    view, menuInfo);
        }
    }

    public View prepareDecorView(View v) {
        return ContextMenuDecorView.prepareDecorView(getContext(), v, this, 0);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(prepareDecorView(view));
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(prepareDecorView(view), params);
    }
}
