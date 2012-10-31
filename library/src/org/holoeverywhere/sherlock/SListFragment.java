
package org.holoeverywhere.sherlock;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ListFragment;

import android.os.Build.VERSION;

import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SListFragment extends ListFragment implements SBaseFragment {
    private SBase mBase;

    @Override
    public SBase getSBase() {
        return mBase;
    }

    @Deprecated
    public SActivity getSherlockActivity() {
        return (SActivity) mBase;
    }

    @Override
    public boolean isABSSupport() {
        return VERSION.SDK_INT >= 7;
    }

    @Override
    public void onAttach(Activity activity) {
        if (isABSSupport()) {
            if (!(activity instanceof SBase)) {
                throw new IllegalStateException(getClass().getSimpleName()
                        + " must be attached to a S***Activity.");
            }
            mBase = (SBase) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu,
            android.view.MenuInflater inflater) {
        if (isABSSupport()) {
            onCreateOptionsMenu(new MenuWrapper(menu),
                    mBase.getSupportMenuInflater());
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onDetach() {
        mBase = null;
        super.onDetach();
    }

    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (isABSSupport()) {
            return onOptionsItemSelected(new MenuItemWrapper(item));
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        if (isABSSupport()) {
            onPrepareOptionsMenu(new MenuWrapper(menu));
        } else {
            super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }
}
