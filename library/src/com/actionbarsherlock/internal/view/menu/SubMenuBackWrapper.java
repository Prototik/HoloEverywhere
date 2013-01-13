
package com.actionbarsherlock.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;

import com.actionbarsherlock.view.SubMenu;

public class SubMenuBackWrapper extends MenuBackWrapper implements android.view.SubMenu {
    private SubMenu menu;

    public SubMenuBackWrapper(SubMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void clearHeader() {
        menu.clearHeader();
    }

    @Override
    public MenuItem getItem() {
        return item(menu.getItem());
    }

    @Override
    public android.view.SubMenu setHeaderIcon(Drawable arg0) {
        menu.setHeaderIcon(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setHeaderIcon(int arg0) {
        menu.setHeaderIcon(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setHeaderTitle(CharSequence arg0) {
        menu.setHeaderTitle(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setHeaderTitle(int arg0) {
        menu.setHeaderTitle(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setHeaderView(View arg0) {
        menu.setHeaderView(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setIcon(Drawable arg0) {
        menu.setIcon(arg0);
        return this;
    }

    @Override
    public android.view.SubMenu setIcon(int arg0) {
        menu.setIcon(arg0);
        return this;
    }

    @Override
    public SubMenu unwrap() {
        return menu;
    }
}
