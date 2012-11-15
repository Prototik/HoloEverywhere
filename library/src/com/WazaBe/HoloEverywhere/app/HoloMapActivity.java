package com.WazaBe.HoloEverywhere.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.SystemServiceManager;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.SystemServiceManager.SuperSystemService;
import com.WazaBe.HoloEverywhere.app.Application.Config;
import com.WazaBe.HoloEverywhere.app.Application.Config.PreferenceImpl;
import com.WazaBe.HoloEverywhere.preference.PreferenceManager;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapActivity;

public abstract class HoloMapActivity extends MapActivity implements Base, ContextMenuListener, SuperSystemService {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Holo {
	public boolean addFactoryToInflater() default true;

	public boolean forceThemeApply() default false;

	public int layout() default -1;
    }

    private static final Holo DEFAULT_HOLO = new Holo() {
	@Override
	public boolean addFactoryToInflater() {
	    return true;
	}

	@Override
	public Class<Holo> annotationType() {
	    return Holo.class;
	}

	@Override
	public boolean forceThemeApply() {
	    return false;
	}

	@Override
	public int layout() {
	    return 0;
	}
    };

    private boolean forceThemeApply = false;
    private int lastThemeResourceId = 0;

    @Override
    public FragmentManager getSupportFragmentManager() {
	return null;
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
	return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	if (item instanceof ContextMenuItemWrapper) {
	    return super.onContextItemSelected(((ContextMenuItemWrapper) item).unwrap());
	}
	return false;
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
	if (menu instanceof ContextMenuWrapper) {
	    super.onContextMenuClosed(((ContextMenuWrapper) menu).unwrap());
	}
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	onCreateContextMenu(new ContextMenuWrapper(menu), v, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
	if (menu instanceof ContextMenuWrapper) {
	    super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(), view, menuInfo);
	}
    }

    @Override
    public Object superGetSystemService(String name) {
	return super.getSystemService(name);
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
	super.addContentView(prepareDecorView(view), params);
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder, View view, ContextMenuInfo menuInfo,
	    ContextMenuListener listener) {
	listener.onCreateContextMenu(contextMenuBuilder, view, menuInfo);
    }

    @Override
    public Config getConfig() {
	return Application.getConfig();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
	return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public int getLastThemeResourceId() {
	return lastThemeResourceId;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
	return LayoutInflater.from(this);
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl, String name, int mode) {
	return PreferenceManager.wrap(this, impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
	return PreferenceManager.wrap(this, name, mode);
    }

    @Override
    public Object getSystemService(String name) {
	return SystemServiceManager.getSystemService(this, name);
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
    protected void onCreate(Bundle savedInstanceState) {
	Holo holo = getClass().isAnnotationPresent(Holo.class) ? getClass().getAnnotation(Holo.class)
		: HoloMapActivity.DEFAULT_HOLO;
	if (holo.addFactoryToInflater()) {
	    getLayoutInflater().addFactory(this, 0);
	}
	boolean forceThemeApply = isForceThemeApply();
	if (holo.forceThemeApply()) {
	    setForceThemeApply(forceThemeApply = true);
	}
	if (forceThemeApply || getConfig().isUseThemeManager()) {
	    ThemeManager.applyTheme(this, forceThemeApply);
	}
	super.onCreate(savedInstanceState);
	final int layout = holo.layout();
	if (layout > 0) {
	    setContentView(layout);
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	LayoutInflater.onDestroy(this);
    }

    @Override
    public View prepareDecorView(View v) {
	v = FontLoader.apply(v);
	if (!getConfig().isDisableContextMenu() && v != null) {
	    v = new ContextMenuDecorView(this, v, this);
	}
	return v;
    }

    @Override
    public void setContentView(int layoutResID) {
	super.setContentView(prepareDecorView(getLayoutInflater().inflate(layoutResID)));
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

//     @Override
//     public void startActivities(Intent[] intents) {
//     super.startActivities(intents, null);
//     }
    //
    // @Override
    // public void startActivities(Intent[] intents, Bundle options) {
    // for (Intent intent : intents) {
    // startActivity(intent, options);
    // }
    // }
    //
    // @Override
    // public void startActivity(Intent intent) {
    // startActivity(intent, null);
    // }
    //
    // @Override
    // public void startActivity(Intent intent, Bundle options) {
    // startActivityForResult(intent, -1, options);
    // }
    //
    // @Override
    // public void startActivityForResult(Intent intent, int requestCode) {
    // startActivityForResult(intent, requestCode, null);
    // }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
	if (getConfig().isAlwaysUseParentTheme()) {
	    ThemeManager.startActivity(this, intent, requestCode, options);
	} else {
	    superStartActivity(intent, requestCode, options);
	}
    }

    @Override
    public android.content.SharedPreferences superGetSharedPreferences(String name, int mode) {
	return super.getSharedPreferences(name, mode);
    }

    @Override
    @SuppressLint("NewApi")
    public void superStartActivity(Intent intent, int requestCode, Bundle options) {
	if (VERSION.SDK_INT >= 16) {
	    super.startActivityForResult(intent, requestCode, options);
	} else {
	    super.startActivityForResult(intent, requestCode);
	}
    }
}
