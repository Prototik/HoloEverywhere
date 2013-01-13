
package com.actionbarsherlock.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;

public class ContextMenuBackWrapper extends MenuBackWrapper implements android.view.ContextMenu {
    private final ContextMenu menu;

    public ContextMenuBackWrapper(ContextMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void clearHeader() {
        menu.clearHeader();
    }

    @Override
    public android.view.ContextMenu setHeaderIcon(Drawable arg0) {
        menu.setHeaderIcon(arg0);
        return this;
    }

    @Override
    public android.view.ContextMenu setHeaderIcon(int arg0) {
        menu.setHeaderIcon(arg0);
        return this;
    }

    @Override
    public android.view.ContextMenu setHeaderTitle(CharSequence arg0) {
        menu.setHeaderTitle(arg0);
        return this;
    }

    @Override
    public android.view.ContextMenu setHeaderTitle(int arg0) {
        menu.setHeaderTitle(arg0);
        return this;
    }

    @Override
    public android.view.ContextMenu setHeaderView(View arg0) {
        menu.setHeaderView(arg0);
        return this;
    }

    @Override
    public Menu unwrap() {
        return menu;
    }
}
