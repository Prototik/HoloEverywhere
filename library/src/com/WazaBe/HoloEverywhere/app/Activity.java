package com.WazaBe.HoloEverywhere.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Watson;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.ThemeManager;
import com.WazaBe.HoloEverywhere.app.Application.Config;
import com.WazaBe.HoloEverywhere.preference.PreferenceManager;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public abstract class Activity extends Watson implements Base {
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

	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.apply(view), params);
	}

	public SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this);
	}

	@Override
	public Config getSettings() {
		return Application.getConfig();
	}

	@Override
	public SharedPreferences getSupportSharedPreferences(String name, int mode) {
		return PreferenceManager.wrap(this, name, mode);
	}

	@Override
	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(super.getSystemService(name));
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
		Holo holo = getClass().isAnnotationPresent(Holo.class) ? getClass()
				.getAnnotation(Holo.class) : DEFAULT_HOLO;
		if (holo.addFactoryToInflater()) {
			getLayoutInflater().addFactory(this, 0);
		}
		boolean forceThemeApply = isForceThemeApply();
		if (holo.forceThemeApply()) {
			setForceThemeApply(forceThemeApply = true);
		}
		if (forceThemeApply || getSettings().isUseThemeManager()) {
			ThemeManager.applyTheme(this, forceThemeApply);
		}
		super.onCreate(savedInstanceState);
		final int layout = holo.layout();
		if (layout > 0) {
			setContentView(layout);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LayoutInflater.onDestroy(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(FontLoader.inflate(this, layoutResID));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(FontLoader.apply(view));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(FontLoader.apply(view), params);
	}

	public void setForceThemeApply(boolean forceThemeApply) {
		this.forceThemeApply = forceThemeApply;
	}

	@Override
	public void startActivities(Intent[] intents) {
		startActivities(intents, null);
	}

	@Override
	public void startActivities(Intent[] intents, Bundle options) {
		for (Intent intent : intents) {
			startActivity(intent, options);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		startActivity(intent, null);
	}

	@Override
	public void startActivity(Intent intent, Bundle options) {
		startActivityForResult(intent, -1, options);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode, null);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		if (getSettings().isAlwaysUseParentTheme()) {
			ThemeManager.startActivity(this, intent, requestCode, options);
		} else {
			superStartActivity(intent, requestCode, options);
		}
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
}
