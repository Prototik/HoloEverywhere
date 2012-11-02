
package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;

import android.content.Context;
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
    public Dialog(Context context) {
        super(context);
    }

    public Dialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public Dialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        super.addContentView(prepareDecorView(view), params);
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder, View view,
            ContextMenuInfo menuInfo, ContextMenuListener listener) {
        listener.onCreateContextMenu(contextMenuBuilder, view, menuInfo);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
        return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item instanceof ContextMenuItemWrapper) {
            return super.onContextItemSelected(((ContextMenuItemWrapper) item).unwrap());
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
            onContextMenuClosed(new ContextMenuWrapper((android.view.ContextMenu) menu));
        } else {
            super.onContextMenuClosed(menu);
        }
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), view, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if (menu instanceof ContextMenuWrapper) {
            super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(), view, menuInfo);
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
