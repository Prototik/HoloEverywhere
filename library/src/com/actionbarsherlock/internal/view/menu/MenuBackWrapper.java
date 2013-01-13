
package com.actionbarsherlock.internal.view.menu;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class MenuBackWrapper implements android.view.Menu {
    private static final MenuItem[] array(android.view.MenuItem[] source) {
        MenuItem[] result = new MenuItem[source.length];
        for (int i = 0; i < source.length; i++) {
            android.view.MenuItem s = source[i];
            if (s instanceof MenuItemBackWrapper) {
                result[i] = ((MenuItemBackWrapper) s).unwrap();
            } else {
                result[i] = new MenuItemWrapper(s);
            }
        }
        return result;
    }

    protected static android.view.MenuItem item(MenuItem item) {
        if (item instanceof ContextMenuItemWrapper) {
            return ((ContextMenuItemWrapper) item).unwrap();
        } else {
            return new MenuItemBackWrapper(item);
        }
    }

    protected static android.view.SubMenu submenu(SubMenu menu) {
        if (menu instanceof SubMenuWrapper) {
            return (android.view.SubMenu) ((SubMenuWrapper) menu).unwrap();
        } else {
            return new SubMenuBackWrapper(menu);
        }
    }

    private final Menu menu;

    public MenuBackWrapper(Menu menu) {
        this.menu = menu;
    }

    @Override
    public android.view.MenuItem add(CharSequence arg0) {
        return new MenuItemBackWrapper(menu.add(arg0));
    }

    @Override
    public android.view.MenuItem add(int arg0) {
        return new MenuItemBackWrapper(menu.add(arg0));
    }

    @Override
    public android.view.MenuItem add(int arg0, int arg1, int arg2, CharSequence arg3) {
        return new MenuItemBackWrapper(menu.add(arg0, arg1, arg2, arg3));
    }

    @Override
    public android.view.MenuItem add(int arg0, int arg1, int arg2, int arg3) {
        return new MenuItemBackWrapper(menu.add(arg0, arg1, arg2, arg3));
    }

    @Override
    public int addIntentOptions(int arg0, int arg1, int arg2, ComponentName arg3, Intent[] arg4,
            Intent arg5, int arg6, android.view.MenuItem[] arg7) {
        return menu.addIntentOptions(arg0, arg1, arg2, arg3, arg4, arg5, arg6, array(arg7));
    }

    @Override
    public android.view.SubMenu addSubMenu(CharSequence arg0) {
        return submenu(menu.addSubMenu(arg0));
    }

    @Override
    public android.view.SubMenu addSubMenu(int arg0) {
        return submenu(menu.addSubMenu(arg0));
    }

    @Override
    public android.view.SubMenu addSubMenu(int arg0, int arg1, int arg2, CharSequence arg3) {
        return submenu(menu.addSubMenu(arg0, arg1, arg2, arg3));
    }

    @Override
    public android.view.SubMenu addSubMenu(int arg0, int arg1, int arg2, int arg3) {
        return submenu(menu.addSubMenu(arg0, arg1, arg2, arg3));
    }

    @Override
    public void clear() {
        menu.clear();
    }

    @Override
    public void close() {
        menu.close();
    }

    @Override
    public android.view.MenuItem findItem(int arg0) {
        return item(menu.findItem(arg0));
    }

    @Override
    public android.view.MenuItem getItem(int arg0) {
        return item(menu.getItem(arg0));
    }

    @Override
    public boolean hasVisibleItems() {
        return menu.hasVisibleItems();
    }

    @Override
    public boolean isShortcutKey(int arg0, KeyEvent arg1) {
        return menu.isShortcutKey(arg0, arg1);
    }

    @Override
    public boolean performIdentifierAction(int arg0, int arg1) {
        return menu.performIdentifierAction(arg0, arg1);
    }

    @Override
    public boolean performShortcut(int arg0, KeyEvent arg1, int arg2) {
        return menu.performShortcut(arg0, arg1, arg2);
    }

    @Override
    public void removeGroup(int arg0) {
        menu.removeGroup(arg0);
    }

    @Override
    public void removeItem(int arg0) {
        menu.removeItem(arg0);
    }

    @Override
    public void setGroupCheckable(int arg0, boolean arg1, boolean arg2) {
        menu.setGroupCheckable(arg0, arg1, arg2);
    }

    @Override
    public void setGroupEnabled(int arg0, boolean arg1) {
        menu.setGroupEnabled(arg0, arg1);
    }

    @Override
    public void setGroupVisible(int arg0, boolean arg1) {
        menu.setGroupVisible(arg0, arg1);
    }

    @Override
    public void setQwertyMode(boolean arg0) {
        menu.setQwertyMode(arg0);
    }

    @Override
    public int size() {
        return menu.size();
    }

    public Menu unwrap() {
        return menu;
    }
}
