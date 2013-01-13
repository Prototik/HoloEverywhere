
package com.actionbarsherlock.internal.view.menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.SubMenu;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;

public class MenuItemBackWrapper implements android.view.MenuItem {
    private MenuItem menuItem;

    public MenuItemBackWrapper(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public boolean collapseActionView() {
        return menuItem.collapseActionView();
    }

    @Override
    public boolean expandActionView() {
        return menuItem.expandActionView();
    }

    @Override
    public android.view.ActionProvider getActionProvider() {
        // TODO
        return null;
    }

    @Override
    public View getActionView() {
        return menuItem.getActionView();
    }

    @Override
    public char getAlphabeticShortcut() {
        return menuItem.getAlphabeticShortcut();
    }

    @Override
    public int getGroupId() {
        return menuItem.getGroupId();
    }

    @Override
    public Drawable getIcon() {
        return menuItem.getIcon();
    }

    @Override
    public Intent getIntent() {
        return menuItem.getIntent();
    }

    @Override
    public int getItemId() {
        return menuItem.getItemId();
    }

    @Override
    public ContextMenuInfo getMenuInfo() {
        return menuItem.getMenuInfo();
    }

    @Override
    public char getNumericShortcut() {
        return menuItem.getNumericShortcut();
    }

    @Override
    public int getOrder() {
        return menuItem.getOrder();
    }

    @Override
    public SubMenu getSubMenu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CharSequence getTitle() {
        return menuItem.getTitle();
    }

    @Override
    public CharSequence getTitleCondensed() {
        return menuItem.getTitleCondensed();
    }

    @Override
    public boolean hasSubMenu() {
        return menuItem.hasSubMenu();
    }

    @Override
    public boolean isActionViewExpanded() {
        return menuItem.isActionViewExpanded();
    }

    @Override
    public boolean isCheckable() {
        return menuItem.isCheckable();
    }

    @Override
    public boolean isChecked() {
        return menuItem.isChecked();
    }

    @Override
    public boolean isEnabled() {
        return menuItem.isEnabled();
    }

    @Override
    public boolean isVisible() {
        return menuItem.isVisible();
    }

    @Override
    public android.view.MenuItem setActionProvider(android.view.ActionProvider provider) {
        // TODO
        return this;
    }

    @Override
    public android.view.MenuItem setActionView(int arg0) {
        menuItem.setActionView(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setActionView(View arg0) {
        menuItem.setActionView(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setAlphabeticShortcut(char arg0) {
        menuItem.setAlphabeticShortcut(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setCheckable(boolean arg0) {
        menuItem.setCheckable(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setChecked(boolean arg0) {
        menuItem.setChecked(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setEnabled(boolean arg0) {
        menuItem.setEnabled(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setIcon(Drawable arg0) {
        menuItem.setIcon(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setIcon(int arg0) {
        menuItem.setIcon(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setIntent(Intent arg0) {
        menuItem.setIntent(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setNumericShortcut(char arg0) {
        menuItem.setNumericShortcut(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setOnActionExpandListener(final OnActionExpandListener arg0) {
        menuItem.setOnActionExpandListener(new com.actionbarsherlock.view.MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return arg0.onMenuItemActionCollapse(MenuItemBackWrapper.this);
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return arg0.onMenuItemActionExpand(MenuItemBackWrapper.this);
            }
        });
        return this;
    }

    @Override
    public android.view.MenuItem setOnMenuItemClickListener(
            final OnMenuItemClickListener arg0) {
        menuItem.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return arg0.onMenuItemClick(MenuItemBackWrapper.this);
            }
        });
        return this;
    }

    @Override
    public android.view.MenuItem setShortcut(char arg0, char arg1) {
        menuItem.setShortcut(arg0, arg1);
        return this;
    }

    @Override
    public void setShowAsAction(int arg0) {
        menuItem.setShowAsAction(arg0);
    }

    @Override
    public android.view.MenuItem setShowAsActionFlags(int arg0) {
        menuItem.setShowAsActionFlags(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setTitle(CharSequence arg0) {
        menuItem.setTitle(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setTitle(int arg0) {
        menuItem.setTitle(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setTitleCondensed(CharSequence arg0) {
        menuItem.setTitleCondensed(arg0);
        return this;
    }

    @Override
    public android.view.MenuItem setVisible(boolean arg0) {
        menuItem.setVisible(arg0);
        return this;
    }

    public MenuItem unwrap() {
        return menuItem;
    }
}
