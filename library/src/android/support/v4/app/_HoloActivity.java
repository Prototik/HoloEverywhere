
package android.support.v4.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;
import org.holoeverywhere.SystemServiceManager;
import org.holoeverywhere.SystemServiceManager.SuperSystemService;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.ThemeManager.SuperStartActivity;
import org.holoeverywhere.addon.IAddonActivity;
import org.holoeverywhere.addon.IAddonAttacher;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.ContextThemeWrapperPlus;
import org.holoeverywhere.internal.WindowDecorView;
import org.holoeverywhere.preference.PreferenceManagerHelper;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.util.SparseIntArray;
import org.holoeverywhere.util.WeaklyMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ContextMenuCallbackGetter;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView.ContextMenuListenersProvider;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class _HoloActivity extends Watson implements SuperStartActivity,
        OnCreatePanelMenuListener, OnPreparePanelListener,
        OnMenuItemSelectedListener, OnActionModeStartedListener,
        OnActionModeFinishedListener, SuperSystemService, ContextMenuListener,
        ContextMenuListenersProvider, IAddonAttacher<IAddonActivity> {
    private final class ActivityDecorView extends WindowDecorView {
        public ActivityDecorView() {
            super(_HoloActivity.this);
        }

        @Override
        protected boolean fitSystemWindows(Rect insets) {
            final SparseIntArray windowFeatures = createConfig(null).windowFeatures;
            if (windowFeatures != null
                    && windowFeatures.get(Window.FEATURE_ACTION_BAR_OVERLAY, 0) != 0) {
                return false;
            }
            return super.fitSystemWindows(insets);
        }
    }

    public static final class Holo implements Parcelable {
        public static final Parcelable.Creator<Holo> CREATOR = new Creator<Holo>() {
            @Override
            public Holo createFromParcel(Parcel source) {
                return new Holo(source);
            }

            @Override
            public Holo[] newArray(int size) {
                return new Holo[size];
            }
        };

        public static Holo defaultConfig() {
            return new Holo();
        }

        public boolean ignoreApplicationInstanceCheck = false;
        public boolean ignoreThemeCheck = false;
        public boolean requireRoboguice = false;
        public boolean requireSherlock = true;
        public boolean requireSlider = false;
        public boolean requireTabber = false;
        private SparseIntArray windowFeatures;

        public Holo() {

        }

        private Holo(Parcel source) {
            ignoreThemeCheck = source.readInt() == 1;
            ignoreApplicationInstanceCheck = source.readInt() == 1;
            requireSherlock = source.readInt() == 1;
            requireSlider = source.readInt() == 1;
            requireRoboguice = source.readInt() == 1;
            requireTabber = source.readInt() == 1;
            windowFeatures = source.readParcelable(SparseIntArray.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public void requestWindowFeature(int feature) {
            if (windowFeatures == null) {
                windowFeatures = new SparseIntArray();
            }
            windowFeatures.put(feature, feature);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ignoreThemeCheck ? 1 : 0);
            dest.writeInt(ignoreApplicationInstanceCheck ? 1 : 0);
            dest.writeInt(requireSherlock ? 1 : 0);
            dest.writeInt(requireSlider ? 1 : 0);
            dest.writeInt(requireRoboguice ? 1 : 0);
            dest.writeInt(requireTabber ? 1 : 0);
            dest.writeParcelable(windowFeatures, flags);
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

    public static interface OnWindowFocusChangeListener {
        public void onWindowFocusChanged(boolean hasFocus);
    }

    private static final String CONFIG_KEY = "holo:config:activity";
    private Context mActionBarContext;
    private Holo mConfig;
    private Map<View, ContextMenuListener> mContextMenuListeners;
    private WindowDecorView mDecorView;
    private boolean mInited = false;
    private int mLastThemeResourceId = 0;
    private MenuInflater mMenuInflater;
    private final List<WeakReference<OnWindowFocusChangeListener>> mOnWindowFocusChangeListeners = new ArrayList<WeakReference<OnWindowFocusChangeListener>>();
    private Handler mUserHandler;

    @Override
    public void addContentView(View view, LayoutParams params) {
        if (requestDecorView(view, params, -1)) {
            mDecorView.addView(view, params);
            onContentChanged();
        }
    }

    public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener listener) {
        synchronized (mOnWindowFocusChangeListeners) {
            Iterator<WeakReference<OnWindowFocusChangeListener>> i = mOnWindowFocusChangeListeners
                    .iterator();
            while (i.hasNext()) {
                WeakReference<OnWindowFocusChangeListener> reference = i.next();
                if (reference == null) {
                    i.remove();
                    continue;
                }
                OnWindowFocusChangeListener iListener = reference.get();
                if (iListener == null) {
                    i.remove();
                    continue;
                }
                if (iListener == listener) {
                    return;
                }
            }
            mOnWindowFocusChangeListeners
                    .add(new WeakReference<OnWindowFocusChangeListener>(listener));
        }
    }

    protected Holo createConfig(Bundle savedInstanceState) {
        if (mConfig == null) {
            mConfig = onCreateConfig(savedInstanceState);
        }
        if (mConfig == null) {
            mConfig = Holo.defaultConfig();
        }
        return mConfig;
    }

    protected void forceInit(Bundle savedInstanceState) {
        if (mInited) {
            return;
        }
        if (mConfig == null && savedInstanceState != null
                && savedInstanceState.containsKey(CONFIG_KEY)) {
            mConfig = savedInstanceState.getParcelable(CONFIG_KEY);
        }
        onInit(mConfig, savedInstanceState);
    }

    public Holo getConfig() {
        return mConfig;
    }

    @Override
    public ContextMenuListener getContextMenuListener(View view) {
        if (mContextMenuListeners == null) {
            return null;
        }
        return mContextMenuListeners.get(view);
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManagerHelper.getDefaultSharedPreferences(this);
    }

    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return PreferenceManagerHelper.getDefaultSharedPreferences(this, impl);
    }

    public int getLastThemeResourceId() {
        return mLastThemeResourceId;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(this);
    }

    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode) {
        return PreferenceManagerHelper.wrap(this, impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return PreferenceManagerHelper.wrap(this, name, mode);
    }

    public abstract ActionBar getSupportActionBar();

    /**
     * @return Themed context for using in action bar
     */
    public Context getSupportActionBarContext() {
        if (mActionBarContext == null) {
            int theme = ThemeManager.getThemeType(this);
            if (theme != ThemeManager.LIGHT) {
                theme = ThemeManager.DARK;
            }
            theme = ThemeManager.getThemeResource(theme, false);
            if (mLastThemeResourceId == theme) {
                mActionBarContext = this;
            } else {
                mActionBarContext = new ContextThemeWrapperPlus(this, theme);
            }
        }
        return mActionBarContext;
    }

    public Application getSupportApplication() {
        return Application.getLastInstance();
    }

    @Override
    public MenuInflater getSupportMenuInflater() {
        if (mMenuInflater != null) {
            return mMenuInflater;
        }
        mMenuInflater = new MenuInflater(getSupportActionBarContext(), this);
        return mMenuInflater;
    }

    @Override
    public Object getSystemService(String name) {
        return SystemServiceManager.getSystemService(this, name);
    }

    @Override
    public Theme getTheme() {
        if (mLastThemeResourceId == 0) {
            setTheme(ThemeManager.getDefaultTheme());
        }
        return super.getTheme();
    }

    public LayoutInflater getThemedLayoutInflater() {
        return getLayoutInflater();
    }

    public Handler getUserHandler() {
        if (mUserHandler == null) {
            mUserHandler = new Handler(getMainLooper());
        }
        return mUserHandler;
    }

    protected final WindowDecorView getWindowDecorView() {
        return mDecorView;
    }

    @Override
    public void invalidateOptionsMenu() {
        supportInvalidateOptionsMenu();
    }

    public boolean isDecorViewInited() {
        return mDecorView != null;
    }

    public boolean isInited() {
        return mInited;
    }

    @Override
    @SuppressLint("NewApi")
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            finish();
        }
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
        forceInit(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    protected Holo onCreateConfig(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CONFIG_KEY)) {
            final Holo config = savedInstanceState.getParcelable(CONFIG_KEY);
            if (config != null) {
                return config;
            }
        }
        return Holo.defaultConfig();
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View v, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        final android.view.ContextMenu nativeMenu;
        if (menu instanceof ContextMenuWrapper) {
            nativeMenu = ((ContextMenuWrapper) menu).unwrap();
            super.onCreateContextMenu(nativeMenu, view, menuInfo);
            if (view instanceof ContextMenuCallbackGetter) {
                final OnCreateContextMenuListener l = ((ContextMenuCallbackGetter) view)
                        .getOnCreateContextMenuListener();
                if (l != null) {
                    l.onCreateContextMenu(nativeMenu, view, menuInfo);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LayoutInflater.removeInstance(this);
    }

    public boolean onHomePressed() {
        return false;
    }

    /**
     * Do not override this method. Use {@link #onPreInit(Holo, Bundle)} and
     * {@link #onPostInit(Holo, Bundle)}
     */
    protected void onInit(Holo config, Bundle savedInstanceState) {
        if (mInited) {
            throw new IllegalStateException("This instance was already inited");
        }
        mInited = true;
        if (config == null) {
            config = createConfig(savedInstanceState);
        }
        if (config == null) {
            config = Holo.defaultConfig();
        }
        onPreInit(config, savedInstanceState);
        if (!config.ignoreApplicationInstanceCheck && !(getApplication() instanceof Application)) {
            String text = "Application instance isn't HoloEverywhere.\n";
            if (getApplication().getClass() == android.app.Application.class) {
                text += "Put attr 'android:name=\"org.holoeverywhere.app.Application\"'" +
                        " in <application> tag of AndroidManifest.xml";
            } else {
                text += "Please sure that you extend " + getApplication().getClass() +
                        " from a org.holoeverywhere.app.Application";
            }
            throw new IllegalStateException(text);
        }
        getLayoutInflater().setFragmentActivity(this);
        if (this instanceof Activity) {
            Activity activity = (Activity) this;
            if (config.requireRoboguice) {
                activity.addon(Activity.ADDON_ROBOGUICE);
            }
            if (config.requireSlider) {
                activity.addon(Activity.ADDON_SLIDER);
            }
            if (config.requireSherlock) {
                activity.addonSherlock();
            }
            final SparseIntArray windowFeatures = config.windowFeatures;
            if (windowFeatures != null) {
                for (int i = 0; i < windowFeatures.size(); i++) {
                    if (windowFeatures.valueAt(i) > 0) {
                        requestWindowFeature((long) windowFeatures.keyAt(i));
                    }
                }
            }
            ThemeManager.applyTheme(activity, mLastThemeResourceId == 0);
            if (!config.ignoreThemeCheck && ThemeManager.getThemeType(this) == ThemeManager.INVALID) {
                throw new HoloThemeException(activity);
            }
            TypedArray a = obtainStyledAttributes(new int[] {
                    android.R.attr.windowActionBarOverlay, R.attr.windowActionBarOverlay
            });
            if (a.getBoolean(0, false) || a.getBoolean(1, false)) {
                requestWindowFeature((long) Window.FEATURE_ACTION_BAR_OVERLAY);
            }
            a.recycle();
        }
        onPostInit(config, savedInstanceState);
        lockAttaching();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && onHomePressed()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        requestDecorView(null, null, -1);
        super.onPostCreate(savedInstanceState);
    }

    protected void onPostInit(Holo config, Bundle savedInstanceState) {

    }

    protected void onPreInit(Holo config, Bundle savedInstanceState) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mConfig != null) {
            outState.putParcelable(CONFIG_KEY, mConfig);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        synchronized (mOnWindowFocusChangeListeners) {
            Iterator<WeakReference<OnWindowFocusChangeListener>> i = mOnWindowFocusChangeListeners
                    .iterator();
            while (i.hasNext()) {
                WeakReference<OnWindowFocusChangeListener> reference = i.next();
                if (reference == null) {
                    i.remove();
                    continue;
                }
                OnWindowFocusChangeListener iListener = reference.get();
                if (iListener == null) {
                    i.remove();
                    continue;
                }
                iListener.onWindowFocusChanged(hasFocus);
            }
        }
    }

    @Override
    public void registerForContextMenu(View view) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            super.registerForContextMenu(view);
        } else {
            registerForContextMenu(view, this);
        }
    }

    public void registerForContextMenu(View view, ContextMenuListener listener) {
        if (mContextMenuListeners == null) {
            mContextMenuListeners = new WeaklyMap<View, ContextMenuListener>();
        }
        mContextMenuListeners.put(view, listener);
        view.setLongClickable(true);
    }

    private boolean requestDecorView(View view, LayoutParams params, int layoutRes) {
        if (mDecorView != null) {
            return true;
        }
        mDecorView = new ActivityDecorView();
        mDecorView.setId(android.R.id.content);
        mDecorView.setProvider(this);
        if (view != null) {
            mDecorView.addView(view, params);
        } else if (layoutRes > 0) {
            getThemedLayoutInflater().inflate(layoutRes, mDecorView, true);
        }
        final LayoutParams p = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.installDecorView(mDecorView, p);
            }

            @Override
            public void justPost() {
                getWindow().setContentView(mDecorView, p);
            }
        });
        return false;
    }

    public void requestWindowFeature(long featureId) {
        createConfig(null).requestWindowFeature((int) featureId);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (requestDecorView(null, null, layoutResID)) {
            mDecorView.removeAllViewsInLayout();
            getThemedLayoutInflater().inflate(layoutResID, mDecorView, true);
            onContentChanged();
        }
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (requestDecorView(view, params, -1)) {
            mDecorView.removeAllViewsInLayout();
            mDecorView.addView(view, params);
            onContentChanged();
        }
    }

    public abstract void setSupportProgress(int progress);

    public abstract void setSupportProgressBarIndeterminate(boolean indeterminate);

    public abstract void setSupportProgressBarIndeterminateVisibility(boolean visible);

    public abstract void setSupportProgressBarVisibility(boolean visible);

    public abstract void setSupportSecondaryProgress(int secondaryProgress);

    @Override
    public void setTheme(int resid) {
        setTheme(resid, true);
    }

    public synchronized void setTheme(int resid, boolean modifyGlobal) {
        if (resid > ThemeManager._START_RESOURCES_ID) {
            if (mLastThemeResourceId != resid) {
                mActionBarContext = null;
                mMenuInflater = null;
                super.setTheme(mLastThemeResourceId = resid);
            }
        } else {
            if ((resid & ThemeManager.COLOR_SCHEME_MASK) == 0) {
                int theme = ThemeManager.getTheme(getIntent(), false);
                if (theme == 0) {
                    final android.app.Activity activity = getParent();
                    if (activity != null) {
                        theme = ThemeManager.getTheme(activity.getIntent(), false);
                    }
                }
                theme &= ThemeManager.COLOR_SCHEME_MASK;
                if (theme != 0) {
                    resid |= theme;
                }
            }
            setTheme(ThemeManager.getThemeResource(resid, modifyGlobal));
        }
    }

    public abstract ActionMode startActionMode(ActionMode.Callback callback);

    @SuppressLint("NewApi")
    @Override
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        for (Intent intent : intents) {
            startActivity(intent, options);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivity(Intent intent, Bundle options) {
        startActivityForResult(intent, -1, options);
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode,
            Bundle options) {
        if (HoloEverywhere.ALWAYS_USE_PARENT_THEME) {
            ThemeManager.startActivity(this, intent, requestCode, options);
        } else {
            superStartActivity(intent, requestCode, options);
        }
    }

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

    @SuppressLint("NewApi")
    @Override
    public void supportInvalidateOptionsMenu() {
        if (VERSION.SDK_INT >= 11) {
            super.invalidateOptionsMenu();
        }
    }

    @Override
    public void unregisterForContextMenu(View view) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            super.unregisterForContextMenu(view);
        } else {
            if (mContextMenuListeners != null) {
                mContextMenuListeners.remove(view);
            }
        }
    }
}
