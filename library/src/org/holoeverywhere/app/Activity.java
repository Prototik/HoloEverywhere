
package org.holoeverywhere.app;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.addon.Sherlock;
import org.holoeverywhere.addon.Sherlock.SherlockA;
import org.holoeverywhere.addons.IAddon;

import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app._HoloActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class Activity extends _HoloActivity {
    public static final String ADDON_SHERLOCK = "Sherlock";
    public static final String ADDON_SLIDING_MENU = "SlidingMenu";
    private final List<IAddon<?, ?>> addons = new ArrayList<IAddon<?, ?>>();

    private MenuInflater mMenuInflater;

    @Override
    public void addContentView(View view, LayoutParams params) {
        view = prepareDecorView(view);
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).addContentView(view, params)) {
                return;
            }
        }
        super.addContentView(view, params);
    }

    public void attachAddon(IAddon<?, ?> addon) {
        if (!addons.contains(addon)) {
            addons.add(addon);
        }
    }

    @Override
    public void closeOptionsMenu() {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).closeOptionsMenu()) {
                return;
            }
        }
        super.closeOptionsMenu();
    }

    public void detachAddon(IAddon<?, ?> addon) {
        addons.remove(addon);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).dispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @SuppressWarnings("unchecked")
    public <T extends IAddon<?, ?>> T findAddon(Class<T> clazz) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.getClass().isAssignableFrom(clazz)) {
                return (T) addon;
            }
        }
        return null;
    }

    @Override
    public View findViewById(int id) {
        View view = super.findViewById(id);
        if (view != null) {
            return view;
        }
        for (IAddon<?, ?> addon : addons) {
            if ((view = addon.activity(this).findViewById(id)) != null) {
                return view;
            }
        }
        return null;
    }

    @Override
    public ActionBar getSupportActionBar() {
        return requireSherlock().getActionBar();
    }

    @Override
    public MenuInflater getSupportMenuInflater() {
        if (mMenuInflater != null) {
            return mMenuInflater;
        }
        mMenuInflater = new MenuInflater(getSupportActionBarContext(), this);
        return mMenuInflater;
    }

    public boolean isAddonAttached(Class<? extends IAddon<?, ?>> clazz) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onCreate(savedInstanceState);
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(android.view.Menu menu) {
        return onCreateOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).onCreatePanelMenu(featureId, menu)) {
                return true;
            }
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    protected void onDestroy() {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).onKeyUp(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onMenuItemSelected(int featureId,
            android.view.MenuItem item) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).onMenuItemSelected(featureId, item)) {
                return true;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onMenuOpened(int featureId, android.view.Menu menu) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).onMenuOpened(featureId, menu)) {
                return true;
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return onOptionsItemSelected(new MenuItemWrapper(item));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onPanelClosed(int featureId, android.view.Menu menu) {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onPanelClosed(featureId, menu);
        }
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onPause() {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onPause();
        }
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onPostCreate(savedInstanceState);
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onPostResume();
        }
    }

    @Override
    public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
        return onPrepareOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view,
            android.view.Menu menu) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).onPreparePanel(featureId, view, menu)) {
                return true;
            }
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onStop() {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onStop();
        }
        super.onStop();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        for (IAddon<?, ?> addon : addons) {
            addon.activity(this).onTitleChanged(title, color);
        }
        super.onTitleChanged(title, color);
    }

    @Override
    public void openOptionsMenu() {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).openOptionsMenu()) {
                return;
            }
        }
        super.openOptionsMenu();
    }

    @Override
    public void requestWindowFeature(long featureIdLong) {
        if (!super.isWasInited()) {
            super.requestWindowFeature(featureIdLong);
            return;
        }
        int featureId = (int) featureIdLong;
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).requestWindowFeature(featureId)) {
                return;
            }
        }
        requestWindowFeature(featureId);
    }

    public <T extends IAddon<?, ?>> T requireAddon(Class<T> clazz) {
        T t = findAddon(clazz);
        if (t == null) {
            try {
                t = clazz.newInstance();
                t.addon(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public void requireAddon(String name) {
        if (name == null) {
            return;
        }
        String className = getConfig().holoEverywherePackage.getValue() + ".addon." + name;
        try {
            requireAddon((Class<? extends IAddon<?, ?>>) Class.forName(className, true,
                    getClassLoader()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to init addon", e);
        }
    }

    public SherlockA requireSherlock() {
        return requireAddon(Sherlock.class).activity(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        setContentView(getLayoutInflater().makeDecorView(layoutResId, this));
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        view = prepareDecorView(view);
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).setContentView(view, params)) {
                return;
            }
        }
        super.setContentView(view, params);
    }

    @Override
    public void setSupportProgress(int progress) {
        requireSherlock().setProgress(progress);
    }

    @Override
    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        requireSherlock().setProgressBarIndeterminate(indeterminate);
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        requireSherlock().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void setSupportProgressBarVisibility(boolean visible) {
        requireSherlock().setProgressBarVisibility(visible);
    }

    @Override
    public void setSupportSecondaryProgress(int secondaryProgress) {
        requireSherlock().setSecondaryProgress(secondaryProgress);
    }

    @Override
    public void setTheme(int resid) {
        mMenuInflater = null;
        super.setTheme(resid);
    }

    public void setUiOptions(int uiOptions) {
        if (isAddonAttached(Sherlock.class)) {
            requireSherlock().setUiOptions(uiOptions);
        } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().setUiOptions(uiOptions);
        }
    }

    public void setUiOptions(int uiOptions, int mask) {
        if (isAddonAttached(Sherlock.class)) {
            requireSherlock().setUiOptions(uiOptions, mask);
        } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().setUiOptions(uiOptions, mask);
        }
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return requireSherlock().startActionMode(callback);
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        for (IAddon<?, ?> addon : addons) {
            if (addon.activity(this).invalidateOptionsMenu()) {
                return;
            }
        }
        super.supportInvalidateOptionsMenu();
    }
}
