
package android.support.v4.app;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.IAddonAttacher;
import org.holoeverywhere.addon.IAddonFragment;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView.ContextMenuListenersProvider;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class _HoloFragment extends android.support.v4.app.Fragment implements
        OnPrepareOptionsMenuListener,
        OnCreateOptionsMenuListener, OnOptionsItemSelectedListener, ContextMenuListener,
        ContextMenuListenersProvider, IAddonAttacher<IAddonFragment> {
    private Activity mActivity;
    boolean mDetachChildFragments = true;

    private void fixClassloader(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        savedInstanceState.setClassLoader(HoloEverywhere.class.getClassLoader());
    }

    public final int getContainerId() {
        return mContainerId;
    }

    @Override
    public ContextMenuListener getContextMenuListener(View view) {
        return mActivity.getContextMenuListener(view);
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return mActivity.getDefaultSharedPreferences();
    }

    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return mActivity.getDefaultSharedPreferences(impl);
    }

    public abstract LayoutInflater getLayoutInflater();

    @Override
    @Deprecated
    /**
     * It's method internal. Don't use or override it
     */
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return getLayoutInflater();
    }

    public MenuInflater getMenuInflater() {
        return mActivity.getSupportMenuInflater();
    }

    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode) {
        return mActivity.getSharedPreferences(impl, name, mode);
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mActivity.getSharedPreferences(name, mode);
    }

    public ActionBar getSupportActionBar() {
        return mActivity.getSupportActionBar();
    }

    public Context getSupportActionBarContext() {
        return mActivity.getSupportActionBarContext();
    }

    public Activity getSupportActivity() {
        return mActivity;
    }

    public Application getSupportApplication() {
        return mActivity.getSupportApplication();
    }

    public Object getSystemService(String name) {
        return mActivity.getSystemService(name);
    }

    public boolean isDetachChildFragments() {
        return mDetachChildFragments;
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
        onCreateOptionsMenu(new MenuWrapper(menu), getMenuInflater());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public final View onCreateView(android.view.LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ContextMenuDecorView decorView = new ContextMenuDecorView(mActivity);
        decorView.setProvider(this);
        final View view = onCreateView(getLayoutInflater(), decorView, savedInstanceState);
        if (view == null) {
            return null;
        }
        decorView.addView(view);
        return decorView;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mChildFragmentManager != null && mChildFragmentManager.mActive != null
                && mDetachChildFragments) {
            for (Fragment fragment : mChildFragmentManager.mActive) {
                if (fragment == null || !fragment.mFromLayout) {
                    continue;
                }
                mChildFragmentManager.detachFragment(fragment, 0, 0);
            }
        }
    }

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

    /**
     * Use {@link #onViewCreated(View, Bundle)} instead
     */
    @Deprecated
    public void onViewCreated(View view) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreated(view);
    }

    public boolean openContextMenu(View v) {
        return v.showContextMenu();
    }

    @Override
    void performActivityCreated(Bundle savedInstanceState) {
        fixClassloader(savedInstanceState);
        super.performActivityCreated(savedInstanceState);
    }

    @Override
    public void registerForContextMenu(View view) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            super.registerForContextMenu(view);
        } else {
            mActivity.registerForContextMenu(view, this);
        }
    }

    /**
     * If true this fragment will be detach all inflated child fragments after
     * destory view
     */
    public void setDetachChildFragments(boolean detachChildFragments) {
        mDetachChildFragments = detachChildFragments;
    }

    @Override
    public void setInitialSavedState(SavedState state) {
        if (state != null) {
            fixClassloader(state.mState);
        }
        super.setInitialSavedState(state);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            fixClassloader(mSavedFragmentState);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public abstract ActionMode startActionMode(ActionMode.Callback callback);

    @Override
    public void unregisterForContextMenu(View view) {
        if (HoloEverywhere.WRAP_TO_NATIVE_CONTEXT_MENU) {
            super.unregisterForContextMenu(view);
        } else {
            mActivity.unregisterForContextMenu(view);
        }
    }
}
