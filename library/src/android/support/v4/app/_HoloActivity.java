
package android.support.v4.app;

import org.holoeverywhere.IHoloActivity;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;
import org.holoeverywhere.SystemServiceManager;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Application.Config;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class _HoloActivity extends Watson implements IHoloActivity {
    public static class Holo {
        public static Holo defaultConfig() {
            return new Holo();
        }

        public static Holo wrap(Holo holo) {
            return defaultConfig().onWrap(holo);
        }

        public boolean addFactoryToInflater = true;

        public boolean forceThemeApply = false;

        public boolean ignoreThemeCheck = false;

        public int layout = -1;

        protected Holo onWrap(Holo holo) {
            addFactoryToInflater = holo.addFactoryToInflater;
            forceThemeApply = holo.forceThemeApply;
            ignoreThemeCheck = holo.ignoreThemeCheck;
            layout = holo.layout;
            return this;
        }
    }

    private static final class HoloThemeException extends RuntimeException {
        private static final long serialVersionUID = -2346897999325868420L;

        public HoloThemeException(_HoloActivity activity) {
            super("You must apply Holo.Theme, Holo.Theme.Light or "
                    + "Holo.Theme.Light.DarkActionBar theme on the activity ("
                    + activity.getClass().getSimpleName()
                    + ") for using HoloEverywhere");
        }
    }

    private Holo config;
    private boolean forceThemeApply = false;
    private int lastThemeResourceId = 0;
    private final String TAG = getClass().getSimpleName();
    private boolean wasInited = false;

    @Override
    public void addContentView(View view, LayoutParams params) {
        super.addContentView(prepareDecorView(view), params);
    }

    private void checkWindowSizes() {
        View view = getWindow().getDecorView();
        if (VERSION.SDK_INT < 11 && view != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            TypedArray a = obtainStyledAttributes(R.styleable.HoloActivity);
            final int windowMinWidthMajor = (int) a.getFraction(
                    R.styleable.HoloActivity_windowMinWidthMajor, dm.widthPixels, 1, 0);
            final int windowMinWidthMinor = (int) a.getFraction(
                    R.styleable.HoloActivity_windowMinWidthMinor, dm.widthPixels, 1, 0);
            a.recycle();
            switch (getRequestedOrientation()) {
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    view.setMinimumWidth(windowMinWidthMajor);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    view.setMinimumWidth(windowMinWidthMinor);
                    break;
            }
        }
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
            View view, ContextMenuInfo menuInfo, ContextMenuListener listener) {
        listener.onCreateContextMenu(contextMenuBuilder, view, menuInfo);
    }

    @Override
    public Config getConfig() {
        return Application.config();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return PreferenceManager.getDefaultSharedPreferences(this, impl);
    }

    public Holo getHolo() {
        return config;
    }

    public int getLastThemeResourceId() {
        return lastThemeResourceId;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(this);
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode) {
        return PreferenceManager.wrap(this, impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return PreferenceManager.wrap(this, name, mode);
    }

    @Override
    public Application getSupportApplication() {
        return Application.getLastInstance();
    }

    @Override
    public MenuInflater getSupportMenuInflater() {
        return null;
    }

    @Override
    public Object getSystemService(String name) {
        return SystemServiceManager.getSystemService(this, name);
    }

    protected void init(Holo config) {
        this.config = config;
    }

    @Override
    public void invalidateOptionsMenu() {
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean isABSSupport() {
        return false;
    }

    @Override
    public boolean isForceThemeApply() {
        return forceThemeApply;
    }

    @Override
    @SuppressLint("NewApi")
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkWindowSizes();
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
    public final void onContextMenuClosed(android.view.Menu menu) {
        if (menu instanceof android.view.ContextMenu) {
            onContextMenuClosed(new ContextMenuWrapper(
                    (android.view.ContextMenu) menu));
        } else {
            Log.w(TAG, "onContextMenuClosed: menu is not ContextMenu instance");
            super.onContextMenuClosed(menu);
        }
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
        if (menu instanceof ContextMenuWrapper) {
            super.onContextMenuClosed(((ContextMenuWrapper) menu).unwrap());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onPreInit();
        onInit(config);
        super.onCreate(savedInstanceState);
        onPostInit(config);
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View v, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        if (menu instanceof ContextMenuWrapper) {
            super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(),
                    view, menuInfo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LayoutInflater.onDestroy(this);
    }

    protected void onInit(Holo config) {
        if (config.addFactoryToInflater) {
            getLayoutInflater().addFactory(this, 0);
        }
        boolean forceThemeApply = isForceThemeApply();
        if (config.forceThemeApply) {
            setForceThemeApply(forceThemeApply = true);
        }
        if (lastThemeResourceId == 0) {
            forceThemeApply = true;
        }
        ThemeManager.applyTheme(this, forceThemeApply);
        if (!config.ignoreThemeCheck) {
            TypedArray a = obtainStyledAttributes(R.styleable.HoloActivity);
            final boolean holoTheme = a.getBoolean(
                    R.styleable.HoloActivity_holoTheme, false);
            a.recycle();
            if (!holoTheme) {
                throw new HoloThemeException(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected void onPostInit(Holo config) {
        final int layout = config.layout;
        if (layout > 0) {
            setContentView(layout);
        }
    }

    protected void onPreInit() {
        if (wasInited) {
            return;
        }
        wasInited = true;
        if (config == null) {
            config = Holo.defaultConfig();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public View prepareDecorView(View v) {
        return ContextMenuDecorView.prepareDecorView(this, v, this, 0);
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

    public void setForceThemeApply(boolean forceThemeApply) {
        this.forceThemeApply = forceThemeApply;
    }

    @Override
    public void setTheme(int resid) {
        lastThemeResourceId = resid;
        super.setTheme(resid);
    }

    @Override
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        for (Intent intent : intents) {
            startActivity(intent, options);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        startActivityForResult(intent, -1, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode,
            Bundle options) {
        if (getConfig().isAlwaysUseParentTheme()) {
            ThemeManager.startActivity(this, intent, requestCode, options);
        } else {
            superStartActivity(intent, requestCode, options);
        }
    }

    @Override
    public android.content.SharedPreferences superGetSharedPreferences(
            String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public Object superGetSystemService(String name) {
        return super.getSystemService(name);
    }

    @Override
    @SuppressLint("NewApi")
    public void superStartActivity(Intent intent, int requestCode,
            Bundle options) {
        if (VERSION.SDK_INT >= 16) {
            super.startActivityForResult(intent, requestCode, options);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        if (VERSION.SDK_INT >= 11) {
            super.invalidateOptionsMenu();
        }
    }
}
