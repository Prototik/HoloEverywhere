
package org.holoeverywhere.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicReference;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.AddonSherlock;
import org.holoeverywhere.addon.AddonSherlock.AddonSherlockA;
import org.holoeverywhere.addon.IAddon;
import org.holoeverywhere.addon.IAddonActivity;
import org.holoeverywhere.addon.IAddonAttacher;
import org.holoeverywhere.addon.IAddonBasicAttacher;

import android.content.Intent;
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
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface Addons {
        public String[] value();
    }

    public static final String ADDON_ROBOGUICE = "Roboguice";
    public static final String ADDON_SHERLOCK = "Sherlock";
    public static final String ADDON_SLIDING_MENU = "SlidingMenu";
    private final IAddonAttacher<IAddonActivity> mAttacher =
            new IAddonBasicAttacher<IAddonActivity, Activity>(this);

    private MenuInflater mMenuInflater;

    @Override
    public void addContentView(View sView, final LayoutParams params) {
        final View view = prepareDecorView(sView, params);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.addContentView(view, params);
            }

            @Override
            public void justPost() {
                getWindow().addContentView(view, params);
            }
        });
    }

    @Override
    public <T extends IAddonActivity> T addon(Class<? extends IAddon> clazz) {
        return mAttacher.addon(clazz);
    }

    @Override
    public <T extends IAddonActivity> T addon(String classname) {
        return mAttacher.addon(classname);
    }

    public AddonSherlockA addonSherlock() {
        return addon(AddonSherlock.class);
    }

    @Override
    public void closeOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.closeOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.closeOptionsMenu();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.dispatchKeyEvent(event);
            }

            @Override
            public boolean post() {
                return Activity.super.dispatchKeyEvent(event);
            }
        });
    }

    @Override
    public View findViewById(final int id) {
        View view = super.findViewById(id);
        if (view != null) {
            return view;
        }
        final AtomicReference<View> ref = new AtomicReference<View>();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                View view = addon.findViewById(id);
                if (view != null) {
                    ref.set(view);
                    return true;
                } else {
                    return false;
                }
            }
        });
        return ref.get();
    }

    @Override
    public ActionBar getSupportActionBar() {
        return addonSherlock().getActionBar();
    }

    @Override
    public MenuInflater getSupportMenuInflater() {
        if (mMenuInflater != null) {
            return mMenuInflater;
        }
        mMenuInflater = new MenuInflater(getSupportActionBarContext(), this);
        return mMenuInflater;
    }

    public Bundle instanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(ThemeManager.KEY_INSTANCE_STATE)) {
            return extras.getBundle(ThemeManager.KEY_INSTANCE_STATE);
        }
        return null;
    }

    @Override
    public boolean isAddonAttached(Class<? extends IAddon> clazz) {
        return mAttacher.isAddonAttached(clazz);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onActivityResult(requestCode, resultCode, data);
            }
        });
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        final Configuration oldConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onConfigurationChanged(oldConfig, newConfig);
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onContentChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        final Bundle savedInstanceState = instanceState(sSavedInstanceState);
        forceInit(savedInstanceState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPreCreate(savedInstanceState);
            }
        });
        super.onCreate(savedInstanceState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onCreate(savedInstanceState);
            }
        });
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
    public boolean onCreatePanelMenu(final int featureId, final android.view.Menu menu) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onCreatePanelMenu(featureId, menu);
            }

            @Override
            public boolean post() {
                return Activity.super.onCreatePanelMenu(featureId, menu);
            }
        });
    }

    @Override
    protected void onDestroy() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onDestroy();
            }
        });
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onKeyUp(keyCode, event);
            }

            @Override
            public boolean post() {
                return Activity.super.onKeyUp(keyCode, event);
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(final int featureId,
            final android.view.MenuItem item) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onMenuItemSelected(featureId, item);
            }

            @Override
            public boolean post() {
                return Activity.super.onMenuItemSelected(featureId, item);
            }
        });
    }

    @Override
    public boolean onMenuOpened(final int featureId, final android.view.Menu menu) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onMenuOpened(featureId, menu);
            }

            @Override
            public boolean post() {
                return Activity.super.onMenuOpened(featureId, menu);
            }
        });
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onNewIntent(intent);
            }
        });
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
    public void onPanelClosed(final int featureId, final android.view.Menu menu) {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPanelClosed(featureId, menu);
            }
        });
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onPause() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPause();
            }
        });
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle sSavedInstanceState) {
        final Bundle savedInstanceState = instanceState(sSavedInstanceState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPostCreate(savedInstanceState);
            }
        });
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPostResume();
            }
        });
    }

    @Override
    protected void onPreInit(Holo config, Bundle savedInstanceState) {
        super.onPreInit(config, savedInstanceState);
        if (getClass().isAnnotationPresent(Addons.class)) {
            for (String addon : getClass().getAnnotation(Addons.class).value()) {
                if (ADDON_SHERLOCK.equals(addon)) {
                    config.requireSherlock = true;
                } else if (ADDON_SLIDING_MENU.equals(addon)) {
                    config.requireSlidingMenu = true;
                } else if (ADDON_ROBOGUICE.equals(addon)) {
                    config.requireRoboguice = true;
                } else {
                    addon(addon);
                }
            }
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
    public boolean onPreparePanel(final int featureId, final View view,
            final android.view.Menu menu) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onPreparePanel(featureId, view, menu);
            }

            @Override
            public boolean post() {
                return Activity.super.onPreparePanel(featureId, view, menu);
            }
        });
    }

    @Override
    protected void onRestart() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onRestart();
            }
        });
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onResume();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onSaveInstanceState(outState);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onStart();
            }
        });
    }

    @Override
    protected void onStop() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onStop();
            }
        });
        super.onStop();
    }

    @Override
    protected void onTitleChanged(final CharSequence title, final int color) {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onTitleChanged(title, color);
            }
        });
        super.onTitleChanged(title, color);
    }

    @Override
    public void openOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.openOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.openOptionsMenu();
            }
        });
    }

    @Override
    public boolean performAddonAction(AddonCallback<IAddonActivity> callback) {
        return mAttacher.performAddonAction(callback);
    }

    @Override
    public void requestWindowFeature(long featureIdLong) {
        if (!super.isWasInited()) {
            super.requestWindowFeature(featureIdLong);
            return;
        }
        final int featureId = (int) featureIdLong;
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.requestWindowFeature(featureId);
            }

            @Override
            public void justPost() {
                requestWindowFeature(featureId);
            }
        });
    }

    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle(getClassLoader());
        onSaveInstanceState(bundle);
        return bundle.size() > 0 ? bundle : null;
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
    public void setContentView(View sView, final LayoutParams params) {
        final View view = prepareDecorView(sView, params);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.setContentView(view, params);
            }

            @Override
            public void justPost() {
                getWindow().setContentView(view, params);
            }
        });
    }

    @Override
    public void setSupportProgress(int progress) {
        addonSherlock().setProgress(progress);
    }

    @Override
    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        addonSherlock().setProgressBarIndeterminate(indeterminate);
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        addonSherlock().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void setSupportProgressBarVisibility(boolean visible) {
        addonSherlock().setProgressBarVisibility(visible);
    }

    @Override
    public void setSupportSecondaryProgress(int secondaryProgress) {
        addonSherlock().setSecondaryProgress(secondaryProgress);
    }

    @Override
    public void setTheme(int resid) {
        mMenuInflater = null;
        super.setTheme(resid);
    }

    public void setUiOptions(int uiOptions) {
        if (isAddonAttached(AddonSherlock.class)) {
            addonSherlock().setUiOptions(uiOptions);
        } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().setUiOptions(uiOptions);
        }
    }

    public void setUiOptions(int uiOptions, int mask) {
        if (isAddonAttached(AddonSherlock.class)) {
            addonSherlock().setUiOptions(uiOptions, mask);
        } else if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            getWindow().setUiOptions(uiOptions, mask);
        }
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return addonSherlock().startActionMode(callback);
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.invalidateOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.supportInvalidateOptionsMenu();
            }
        });
    }
}
