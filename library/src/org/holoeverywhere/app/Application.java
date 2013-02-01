
package org.holoeverywhere.app;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.IHolo;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.LayoutInflater.LayoutInflaterCreator;
import org.holoeverywhere.SystemServiceManager;
import org.holoeverywhere.SystemServiceManager.SuperSystemService;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.ThemeManager.SuperStartActivity;
import org.holoeverywhere.preference.PreferenceManagerHelper;
import org.holoeverywhere.preference.SharedPreferences;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

public class Application extends android.app.Application implements
        IHolo, SuperStartActivity, SuperSystemService {
    private static Application lastInstance;

    static {
        SystemServiceManager.register(LayoutInflaterCreator.class);
    }

    public static Application getLastInstance() {
        return Application.lastInstance;
    }

    public static void init() {
    }

    public Application() {
        Application.lastInstance = this;
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManagerHelper.getDefaultSharedPreferences(this);
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return PreferenceManagerHelper.getDefaultSharedPreferences(this, impl);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(this);
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl, String name, int mode) {
        return PreferenceManagerHelper.wrap(this, impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return PreferenceManagerHelper.wrap(this, name, mode);
    }

    @Override
    public Application getSupportApplication() {
        return this;
    }

    @Override
    public void onTerminate() {
        LayoutInflater.clearInstances();
        super.onTerminate();
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivities(Intent[] intents, Bundle options) {
        for (Intent intent : intents) {
            startActivity(intent, options);
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        if (HoloEverywhere.ALWAYS_USE_PARENT_THEME) {
            ThemeManager.startActivity(this, intent, options);
        } else {
            superStartActivity(intent, -1, options);
        }
    }

    public android.content.SharedPreferences superGetSharedPreferences(
            String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public Object superGetSystemService(String name) {
        return super.getSystemService(name);
    }

    @Override
    @SuppressLint("NewApi")
    public void superStartActivity(Intent intent, int requestCode,
            Bundle options) {
        if (VERSION.SDK_INT >= 16) {
            super.startActivity(intent, options);
        } else {
            super.startActivity(intent);
        }
    }
}
