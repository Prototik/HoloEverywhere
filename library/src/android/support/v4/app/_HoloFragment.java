
package android.support.v4.app;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.IHoloFragment;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
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
    private static final int INTERNAL_DECOR_VIEW_ID = 0x7f999999;
    private Activity mActivity;
    private Bundle mSavedInstanceState;

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
            View view, ContextMenuInfo menuInfo, ContextMenuListener listener) {
        mActivity.createContextMenu(contextMenuBuilder, view, menuInfo, listener);
    }

    protected int getContainerId() {
        return mContainerId;
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return mActivity.getDefaultSharedPreferences();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return mActivity.getDefaultSharedPreferences(impl);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return mActivity.getLayoutInflater();
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return mActivity.getLayoutInflater();
    }

    public MenuInflater getMenuInflater() {
        return mActivity.getSupportMenuInflater();
    }

    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode) {
        return mActivity.getSharedPreferences(impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mActivity.getSharedPreferences(name, mode);
    }

    @Override
    public ActionBar getSupportActionBar() {
        return getSupportActivity().getSupportActionBar();
    }

    @Override
    public Activity getSupportActivity() {
        return mActivity;
    }

    @Override
    public Application getSupportApplication() {
        return mActivity.getSupportApplication();
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        if (mActivity != null) {
            return mActivity.getSupportFragmentManager();
        } else {
            return getFragmentManager();
        }
    }

    public Object getSystemService(String name) {
        return mActivity.getSystemService(name);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public final void onAttach(android.app.Activity activity) {
        if (!(activity instanceof Activity)) {
            throw new RuntimeException(
                    "HoloEverywhere.Fragment must be attached to HoloEverywhere.Activity");
        }
        mActivity = (Activity) activity;
        onAttach((Activity) activity);
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
        return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mActivity.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
        mActivity.onContextMenuClosed(menu);
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View v, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        mActivity.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu,
            android.view.MenuInflater inflater) {
        onCreateOptionsMenu(new MenuWrapper(menu),
                mActivity.getSupportMenuInflater());
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

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        return onOptionsItemSelected(new MenuItemWrapper(item));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        onPrepareOptionsMenu(new MenuWrapper(menu));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    public void onViewCreated(View view) {
        super.onViewCreated(view, mSavedInstanceState);
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
        View v = view.findViewById(INTERNAL_DECOR_VIEW_ID);
        if (v != null && v instanceof ContextMenuDecorView) {
            view = ((ContextMenuDecorView) v).unwrap();
        }
        mSavedInstanceState = savedInstanceState;
        onViewCreated(view);
    }

    public boolean openContextMenu(View v) {
        return v.showContextMenu();
    }

    @Override
    public View prepareDecorView(View v) {
        if (v == null) {
            return v;
        }
        ContextMenuDecorView view = new ContextMenuDecorView(mActivity, v, null, this);
        view.setId(INTERNAL_DECOR_VIEW_ID);
        return view;
    }
}
