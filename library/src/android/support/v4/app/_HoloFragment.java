
package android.support.v4.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.ContextMenuDecorView;
import android.support.v7.internal.view.menu.ContextMenuDecorView.ContextMenuListenersProvider;
import android.support.v7.internal.view.menu.ContextMenuListener;
import android.support.v7.view.ActionMode;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.IAddonAttacher;
import org.holoeverywhere.addon.IAddonFragment;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;

public abstract class _HoloFragment extends android.support.v4.app.Fragment implements ContextMenuListener,
        ContextMenuListenersProvider, IAddonAttacher<IAddonFragment> {
    boolean mDetachChildFragments = true;
    private Activity mActivity;

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
        return mActivity.getMenuInflater();
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {

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

    /**
     * If true this fragment will be detach all inflated child fragments after
     * destory view
     */
    public void setDetachChildFragments(boolean detachChildFragments) {
        mDetachChildFragments = detachChildFragments;
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
    public final View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(getLayoutInflater(), container, savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean openContextMenu(View v) {
        return v.showContextMenu();
    }

    @Override
    public void registerForContextMenu(View view) {
        mActivity.registerForContextMenu(view, this);
    }

    public ActionMode startSupportActionMode(ActionMode.Callback callback) {
        return mActivity.startSupportActionMode(callback);
    }

    @Override
    public void unregisterForContextMenu(View view) {
        mActivity.unregisterForContextMenu(view);
    }
}
