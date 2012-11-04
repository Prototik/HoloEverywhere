
package org.holoeverywhere.app;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.view.menu.ContextMenuListener;

public interface IHoloFragment extends ContextMenuListener, OnCreateOptionsMenuListener,
        OnPrepareOptionsMenuListener,
        OnOptionsItemSelectedListener {
    public SharedPreferences getDefaultSharedPreferences();

    public IHoloActivity getHoloActivity();

    public LayoutInflater getLayoutInflater();

    public LayoutInflater getLayoutInflater(Bundle savedInstanceState);

    public SharedPreferences getSharedPreferences(String name, int mode);

    public <T extends Activity & IHoloActivity> T getSupportActivity();

    public FragmentManager getSupportFragmentManager();

    public boolean isABSSupport();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    public void onInflate(Activity activity, AttributeSet attrs,
            Bundle savedInstanceState);

    public View prepareDecorView(View view);
}
