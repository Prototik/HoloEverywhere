
package org.holoeverywhere;

import org.holoeverywhere.SystemServiceManager.SuperSystemService;
import org.holoeverywhere.ThemeManager.SuperStartActivity;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public interface IHoloActivity extends IHolo, SuperStartActivity,
        OnCreatePanelMenuListener, OnPreparePanelListener,
        OnMenuItemSelectedListener, OnActionModeStartedListener,
        OnActionModeFinishedListener, SuperSystemService, ContextMenuListener {
    public ActionBarSherlock getSherlock();

    public ActionBar getSupportActionBar();

    public FragmentManager getSupportFragmentManager();

    public MenuInflater getSupportMenuInflater();

    public boolean isForceThemeApply();

    public boolean onCreateOptionsMenu(Menu menu);

    public boolean onOptionsItemSelected(MenuItem item);

    public boolean onPrepareOptionsMenu(Menu menu);

    public View prepareDecorView(View view);

    public void requestWindowFeature(long featureId);

    public void setSupportProgress(int progress);

    public void setSupportProgressBarIndeterminate(boolean indeterminate);

    public void setSupportProgressBarIndeterminateVisibility(boolean visible);

    public void setSupportProgressBarVisibility(boolean visible);

    public void setSupportSecondaryProgress(int secondaryProgress);

    public ActionMode startActionMode(ActionMode.Callback callback);

    public android.content.SharedPreferences superGetSharedPreferences(
            String name, int mode);

    public void supportInvalidateOptionsMenu();
}
