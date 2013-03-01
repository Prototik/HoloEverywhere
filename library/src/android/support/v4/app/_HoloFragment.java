
package android.support.v4.app;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.IHoloFragment;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app._HoloActivity.Holo;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class _HoloFragment extends android.support.v4.app.Fragment implements
        IHoloFragment {
    private static final String CONFIG_KEY = "holo:config:fragment";
    private static final int INTERNAL_DECOR_VIEW_ID = 0x7f999999;
    private Activity activity;

    private Holo mConfig;

    private boolean mWasInited;

    private Bundle savedInstanceState;

    protected Holo createConfig(Bundle savedInstanceState) {
        if (mConfig == null) {
            mConfig = onCreateConfig(savedInstanceState);
        }
        if (mConfig == null) {
            mConfig = Holo.defaultConfig();
        }
        return mConfig;
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
            View view, ContextMenuInfo menuInfo, ContextMenuListener listener) {
        activity.createContextMenu(contextMenuBuilder, view, menuInfo, listener);
    }

    protected void forceInit(Bundle savedInstanceState) {
        if (mWasInited) {
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

    protected int getContainerId() {
        return mContainerId;
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return activity.getDefaultSharedPreferences();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return activity.getDefaultSharedPreferences(impl);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return activity.getLayoutInflater();
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return LayoutInflater.from(super.getLayoutInflater(savedInstanceState));
    }

    public MenuInflater getMenuInflater() {
        return activity.getSupportMenuInflater();
    }

    protected Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode) {
        return activity.getSharedPreferences(impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return activity.getSharedPreferences(name, mode);
    }

    @Override
    public ActionBar getSupportActionBar() {
        return getSupportActivity().getSupportActionBar();
    }

    @Override
    public Activity getSupportActivity() {
        return activity;
    }

    @Override
    public Application getSupportApplication() {
        return activity.getSupportApplication();
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        if (activity != null) {
            return activity.getSupportFragmentManager();
        } else {
            return getFragmentManager();
        }
    }

    public Object getSystemService(String name) {
        return activity.getSystemService(name);
    }

    protected void init(Holo config) {
        mConfig = config;
        if (mConfig.applyImmediately) {
            onInit(mConfig, null);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mConfig = activity.getConfig();
    }

    @Override
    public final void onAttach(android.app.Activity activity) {
        if (!(activity instanceof Activity)) {
            throw new RuntimeException(
                    "HoloEverywhere.Fragment must be attached to HoloEverywhere.Activity");
        }
        this.activity = (Activity) activity;
        onAttach((Activity) activity);
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
        return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return activity.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
        activity.onContextMenuClosed(menu);
    }

    protected Holo onCreateConfig(Bundle savedInstanceState) {
        return Holo.defaultConfig();
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View v, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        activity.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu,
            android.view.MenuInflater inflater) {
        onCreateOptionsMenu(new MenuWrapper(menu),
                activity.getSupportMenuInflater());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public final View onCreateView(android.view.LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return prepareDecorView(onCreateView(
                getLayoutInflater(savedInstanceState), container,
                savedInstanceState));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs,
            Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public final void onInflate(android.app.Activity activity,
            AttributeSet attrs, Bundle savedInstanceState) {
        onInflate((Activity) activity, attrs, savedInstanceState);
    }

    protected void onInit(Holo config, Bundle savedInstanceState) {
        if (mWasInited) {
            return;
        }
        mWasInited = true;
        if (config == null) {
            config = createConfig(savedInstanceState);
        }
        if (config == null) {
            config = Holo.defaultConfig();
        }
        onPreInit(config, savedInstanceState);
        if (this instanceof Fragment) {
            Fragment fragment = (Fragment) this;
            if (config.requireRoboguice) {
                fragment.addon(Activity.ADDON_ROBOGUICE);
            }
        }
        onPostInit(config, savedInstanceState);
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return onOptionsItemSelected(new MenuItemWrapper(item));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected void onPostInit(Holo config, Bundle savedInstanceState) {

    }

    protected void onPreInit(Holo config, Bundle savedInstanceState) {

    }

    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        onPrepareOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mConfig != null) {
            outState.putParcelable(CONFIG_KEY, mConfig);
        }
    }

    public void onViewCreated(View view) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
        View v = view.findViewById(INTERNAL_DECOR_VIEW_ID);
        if (v != null && v instanceof ContextMenuDecorView) {
            view = ((ContextMenuDecorView) v).unwrap();
        }
        this.savedInstanceState = savedInstanceState;
        onViewCreated(view);
    }

    public boolean openContextMenu(View v) {
        return v.showContextMenu();
    }

    @Override
    public View prepareDecorView(View v) {
        return ContextMenuDecorView.prepareDecorView(getSupportActivity(), v,
                this, null, INTERNAL_DECOR_VIEW_ID);
    }
}
