
package org.holoeverywhere.addon;

import org.holoeverywhere.addon.AddonSherlock.AddonSherlockA;
import org.holoeverywhere.addon.AddonSherlock.AddonSherlockF;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.MenuInflater;

public class AddonSherlock extends IAddon<AddonSherlockA, AddonSherlockF> {
    public static class AddonSherlockA extends IAddonActivity {
        private boolean mIgnoreNativeCreate = false;

        private boolean mIgnoreNativePrepare = false;

        private boolean mIgnoreNativeSelected = false;
        private ActionBarSherlock mSherlock;

        public AddonSherlockA(Activity activity) {
            super(activity);
        }

        @Override
        public boolean addContentView(View view, LayoutParams params) {
            getSherlock().addContentView(view, params);
            return true;
        }

        @Override
        public boolean closeOptionsMenu() {
            return getSherlock().dispatchCloseOptionsMenu();
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return getSherlock().dispatchKeyEvent(event);
        }

        public ActionBar getActionBar() {
            return getSherlock().getActionBar();
        }

        public MenuInflater getMenuInflater() {
            return getSherlock().getMenuInflater();
        }

        protected ActionBarSherlock getSherlock() {
            if (mSherlock == null) {
                mSherlock = ActionBarSherlock.wrap(getActivity(),
                        ActionBarSherlock.FLAG_DELEGATE);
            }
            return mSherlock;
        }

        @Override
        public boolean invalidateOptionsMenu() {
            getSherlock().dispatchInvalidateOptionsMenu();
            return true;
        }

        @Override
        public void onConfigurationChanged(Configuration oldConfig, Configuration newConfig) {
            getSherlock().dispatchConfigurationChanged(newConfig);
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL
                    && !mIgnoreNativeCreate) {
                mIgnoreNativeCreate = true;
                boolean result = getSherlock().dispatchCreateOptionsMenu(menu);
                mIgnoreNativeCreate = false;
                return result;
            }
            return false;
        }

        @Override
        public void onDestroy() {
            getSherlock().dispatchDestroy();
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL
                    && !mIgnoreNativeSelected) {
                mIgnoreNativeSelected = true;
                boolean result = getSherlock().dispatchOptionsItemSelected(item);
                mIgnoreNativeSelected = false;
                return result;
            }
            return false;
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu) {
            return getSherlock().dispatchMenuOpened(featureId, menu);
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu) {
            getSherlock().dispatchPanelClosed(featureId, menu);
        }

        @Override
        public void onPause() {
            getSherlock().dispatchPause();
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            getSherlock().dispatchPostCreate(savedInstanceState);
        }

        @Override
        public void onPostResume() {
            getSherlock().dispatchPostResume();
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL
                    && !mIgnoreNativePrepare) {
                mIgnoreNativePrepare = true;
                boolean result = getSherlock().dispatchPrepareOptionsMenu(menu);
                mIgnoreNativePrepare = false;
                return result;
            }
            return false;
        }

        @Override
        public void onStop() {
            getSherlock().dispatchStop();
        }

        @Override
        public void onTitleChanged(CharSequence title, int color) {
            getSherlock().dispatchTitleChanged(title, color);
        }

        @Override
        public boolean openOptionsMenu() {
            return getSherlock().dispatchOpenOptionsMenu();
        }

        @Override
        public boolean requestWindowFeature(int featureId) {
            return getSherlock().requestFeature(featureId);
        }

        @Override
        public boolean setContentView(View view, LayoutParams params) {
            getSherlock().setContentView(view, params);
            return true;
        }

        public void setProgress(int progress) {
            getSherlock().setProgress(progress);
        }

        public void setProgressBarIndeterminate(boolean indeterminate) {
            getSherlock().setProgressBarIndeterminate(indeterminate);
        }

        public void setProgressBarIndeterminateVisibility(boolean visible) {
            getSherlock().setProgressBarIndeterminateVisibility(visible);
        }

        public void setProgressBarVisibility(boolean visible) {
            getSherlock().setProgressBarVisibility(visible);
        }

        public void setSecondaryProgress(int secondaryProgress) {
            getSherlock().setSecondaryProgress(secondaryProgress);
        }

        public void setUiOptions(int uiOptions) {
            getSherlock().setUiOptions(uiOptions);
        }

        public void setUiOptions(int uiOptions, int mask) {
            getSherlock().setUiOptions(uiOptions, mask);
        }

        public ActionMode startActionMode(Callback callback) {
            return getSherlock().startActionMode(callback);
        }
    }

    public static class AddonSherlockF extends IAddonFragment {
        public AddonSherlockF(Fragment fragment) {
            super(fragment);
        }
    }

    @Override
    public AddonSherlockA createAddon(Activity activity) {
        return new AddonSherlockA(activity);
    }

    @Override
    public AddonSherlockF createAddon(Fragment fragment) {
        return new AddonSherlockF(fragment);
    }
}
