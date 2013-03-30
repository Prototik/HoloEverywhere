
package org.holoeverywhere.preference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;

public class PreferenceManager {
    public interface OnActivityDestroyListener {
        void onActivityDestroy();
    }

    public interface OnActivityResultListener {
        boolean onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface OnActivityStopListener {
        void onActivityStop();
    }

    interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                Preference preference);
    }

    public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
    public static final String METADATA_KEY_PREFERENCES = PreferenceInit.PACKAGE;
    private static final String TAG = "PreferenceManager";

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return getDefaultSharedPreferences(context, HoloEverywhere.PREFERENCE_IMPL);
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context,
            PreferenceImpl impl) {
        return wrap(context, impl, getDefaultSharedPreferencesName(context),
                getDefaultSharedPreferencesMode());
    }

    private static int getDefaultSharedPreferencesMode() {
        return Context.MODE_PRIVATE;
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    public static String makeNameById(int id) {
        if (id > 0) {
            if (HoloEverywhere.NAMED_PREFERENCES) {
                return Application.getLastInstance().getResources().getResourceEntryName(id);
            } else {
                return "preference_0x" + Integer.toHexString(id);
            }
        } else {
            return null;
        }
    }

    public static void setDefaultValues(Context context, int resId,
            boolean readAgain) {
        PreferenceManager.setDefaultValues(context,
                PreferenceManager.getDefaultSharedPreferencesName(context),
                PreferenceManager.getDefaultSharedPreferencesMode(), resId,
                readAgain);
    }

    @SuppressLint("NewApi")
    public static void setDefaultValues(Context context,
            String sharedPreferencesName, int sharedPreferencesMode, int resId,
            boolean readAgain) {
        final SharedPreferences defaultValueSp = PreferenceManager.wrap(
                context, PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES,
                Context.MODE_PRIVATE);
        if (readAgain
                || !defaultValueSp.getBoolean(
                        PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            final PreferenceManager pm = new PreferenceManager(context);
            pm.setSharedPreferencesName(sharedPreferencesName);
            pm.setSharedPreferencesMode(sharedPreferencesMode);
            pm.inflateFromResource(context, resId, null);
            SharedPreferences.Editor editor = defaultValueSp.edit().putBoolean(
                    PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, true);
            editor.apply();
        }
    }

    public static SharedPreferences wrap(Context context, PreferenceImpl impl,
            String name, int mode) {
        switch (impl) {
            case XML:
                return new _SharedPreferencesImpl_XML(context, name, mode);
            case JSON:
            default:
                return new _SharedPreferencesImpl_JSON(context, name, mode);
        }
    }

    public static SharedPreferences wrap(Context context, String name, int mode) {
        return PreferenceManager.wrap(context, HoloEverywhere.PREFERENCE_IMPL, name, mode);
    }

    private Activity mActivity;
    private List<OnActivityDestroyListener> mActivityDestroyListeners;
    private List<OnActivityResultListener> mActivityResultListeners;
    private List<OnActivityStopListener> mActivityStopListeners;
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private PreferenceFragment mFragment;
    private long mNextId = 0;
    private int mNextRequestCode;
    private boolean mNoCommit;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceInflater mPreferenceInflater;
    private PreferenceScreen mPreferenceScreen;
    private List<DialogInterface> mPreferencesScreens;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;

    private String mSharedPreferencesName;

    PreferenceManager(Activity activity, int firstRequestCode) {
        mActivity = activity;
        mNextRequestCode = firstRequestCode;

        init(activity);
    }

    private PreferenceManager(Context context) {
        init(context);
    }

    void addPreferencesScreen(DialogInterface screen) {
        synchronized (this) {

            if (mPreferencesScreens == null) {
                mPreferencesScreens = new ArrayList<DialogInterface>();
            }

            mPreferencesScreens.add(screen);
        }
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        final PreferenceScreen preferenceScreen = new PreferenceScreen(context,
                null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    private void dismissAllScreens() {
        ArrayList<DialogInterface> screensToDismiss;

        synchronized (this) {

            if (mPreferencesScreens == null) {
                return;
            }

            screensToDismiss = new ArrayList<DialogInterface>(
                    mPreferencesScreens);
            mPreferencesScreens.clear();
        }

        for (int i = screensToDismiss.size() - 1; i >= 0; i--) {
            screensToDismiss.get(i).dismiss();
        }
    }

    void dispatchActivityDestroy() {
        List<OnActivityDestroyListener> list = null;

        synchronized (this) {
            if (mActivityDestroyListeners != null) {
                list = new ArrayList<OnActivityDestroyListener>(
                        mActivityDestroyListeners);
            }
        }

        if (list != null) {
            final int N = list.size();
            for (int i = 0; i < N; i++) {
                list.get(i).onActivityDestroy();
            }
        }
        dismissAllScreens();
    }

    void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        List<OnActivityResultListener> list;

        synchronized (this) {
            if (mActivityResultListeners == null) {
                return;
            }
            list = new ArrayList<OnActivityResultListener>(
                    mActivityResultListeners);
        }

        final int N = list.size();
        for (int i = 0; i < N; i++) {
            if (list.get(i).onActivityResult(requestCode, resultCode, data)) {
                break;
            }
        }
    }

    void dispatchActivityStop() {
        List<OnActivityStopListener> list;

        synchronized (this) {
            if (mActivityStopListeners == null) {
                return;
            }
            list = new ArrayList<OnActivityStopListener>(mActivityStopListeners);
        }

        final int N = list.size();
        for (int i = 0; i < N; i++) {
            list.get(i).onActivityStop();
        }
    }

    void dispatchNewIntent(Intent intent) {
        dismissAllScreens();
    }

    public Preference findPreference(CharSequence key) {
        if (mPreferenceScreen == null) {
            return null;
        }

        return mPreferenceScreen.findPreference(key);
    }

    public Preference findPreference(int id) {
        if (mPreferenceScreen == null) {
            return null;
        }

        return mPreferenceScreen.findPreference(id);
    }

    Activity getActivity() {
        return mActivity;
    }

    Context getContext() {
        return mContext;
    }

    SharedPreferences.Editor getEditor() {

        if (mNoCommit) {
            if (mEditor == null) {
                mEditor = getSharedPreferences().edit();
            }

            return mEditor;
        } else {
            return getSharedPreferences().edit();
        }
    }

    PreferenceFragment getFragment() {
        return mFragment;
    }

    long getNextId() {
        synchronized (this) {
            return mNextId++;
        }
    }

    int getNextRequestCode() {
        synchronized (this) {
            return mNextRequestCode++;
        }
    }

    OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return mOnPreferenceTreeClickListener;
    }

    public PreferenceInflater getPreferenceInflater() {
        return getPreferenceInflater(mActivity == null ? mContext : mActivity);
    }

    private PreferenceInflater getPreferenceInflater(Context context) {
        if (mPreferenceInflater != null) {
            return mPreferenceInflater;
        }
        mPreferenceInflater = new PreferenceInflater(context, this);
        onInitInflater(mPreferenceInflater);
        return mPreferenceInflater;
    }

    PreferenceScreen getPreferenceScreen() {
        return mPreferenceScreen;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.wrap(mContext,
                    mSharedPreferencesName, mSharedPreferencesMode);
        }
        return mSharedPreferences;
    }

    public int getSharedPreferencesMode() {
        return mSharedPreferencesMode;
    }

    public String getSharedPreferencesName() {
        return mSharedPreferencesName;
    }

    PreferenceScreen inflateFromIntent(Intent queryIntent,
            PreferenceScreen rootPreferences) {
        final List<ResolveInfo> activities = queryIntentActivities(queryIntent);
        final HashSet<String> inflatedRes = new HashSet<String>();
        for (int i = activities.size() - 1; i >= 0; i--) {
            final ActivityInfo activityInfo = activities.get(i).activityInfo;
            final Bundle metaData = activityInfo.metaData;
            if (metaData == null
                    || !metaData
                            .containsKey(PreferenceManager.METADATA_KEY_PREFERENCES)) {
                continue;
            }
            final String uniqueResId = activityInfo.packageName
                    + ":"
                    + activityInfo.metaData
                            .getInt(PreferenceManager.METADATA_KEY_PREFERENCES);
            if (!inflatedRes.contains(uniqueResId)) {
                inflatedRes.add(uniqueResId);

                final Context context;
                try {
                    context = mContext.createPackageContext(
                            activityInfo.packageName, 0);
                } catch (NameNotFoundException e) {
                    Log.w(PreferenceManager.TAG,
                            "Could not create context for "
                                    + activityInfo.packageName + ": "
                                    + Log.getStackTraceString(e));
                    continue;
                }
                final XmlResourceParser parser = activityInfo.loadXmlMetaData(
                        context.getPackageManager(), PreferenceManager.METADATA_KEY_PREFERENCES);
                rootPreferences = (PreferenceScreen) getPreferenceInflater(context).inflate(
                        parser, rootPreferences);
                parser.close();
            }
        }
        rootPreferences.onAttachedToHierarchy(this);
        return rootPreferences;
    }

    public PreferenceScreen inflateFromResource(Context context, int resId,
            PreferenceScreen rootPreferences) {
        setNoCommit(true);
        rootPreferences = (PreferenceScreen) getPreferenceInflater(context).inflate(
                resId, rootPreferences);
        rootPreferences.onAttachedToHierarchy(this);
        setNoCommit(false);
        return rootPreferences;
    }

    private void init(Context context) {
        mContext = PreferenceInit.context(context);

        setSharedPreferencesName(PreferenceManager
                .getDefaultSharedPreferencesName(context));
    }

    protected void onInitInflater(PreferenceInflater inflater) {

    }

    private List<ResolveInfo> queryIntentActivities(Intent queryIntent) {
        return mContext.getPackageManager().queryIntentActivities(queryIntent,
                PackageManager.GET_META_DATA);
    }

    void registerOnActivityDestroyListener(OnActivityDestroyListener listener) {
        synchronized (this) {
            if (mActivityDestroyListeners == null) {
                mActivityDestroyListeners = new ArrayList<OnActivityDestroyListener>();
            }

            if (!mActivityDestroyListeners.contains(listener)) {
                mActivityDestroyListeners.add(listener);
            }
        }
    }

    void registerOnActivityResultListener(OnActivityResultListener listener) {
        synchronized (this) {
            if (mActivityResultListeners == null) {
                mActivityResultListeners = new ArrayList<OnActivityResultListener>();
            }

            if (!mActivityResultListeners.contains(listener)) {
                mActivityResultListeners.add(listener);
            }
        }
    }

    void registerOnActivityStopListener(OnActivityStopListener listener) {
        synchronized (this) {
            if (mActivityStopListeners == null) {
                mActivityStopListeners = new ArrayList<OnActivityStopListener>();
            }

            if (!mActivityStopListeners.contains(listener)) {
                mActivityStopListeners.add(listener);
            }
        }
    }

    void removePreferencesScreen(DialogInterface screen) {
        synchronized (this) {

            if (mPreferencesScreens == null) {
                return;
            }

            mPreferencesScreens.remove(screen);
        }
    }

    void setFragment(PreferenceFragment fragment) {
        mFragment = fragment;
    }

    @SuppressLint("NewApi")
    private void setNoCommit(boolean noCommit) {
        if (!noCommit && mEditor != null) {
            try {
                if (VERSION.SDK_INT < 9) {
                    throw new AbstractMethodError();
                }
                mEditor.apply();
            } catch (AbstractMethodError unused) {
                mEditor.commit();
            }
        }
        mNoCommit = noCommit;
    }

    void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener listener) {
        mOnPreferenceTreeClickListener = listener;
    }

    boolean setPreferences(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != mPreferenceScreen) {
            mPreferenceScreen = preferenceScreen;
            return true;
        }

        return false;
    }

    public void setSharedPreferencesMode(int sharedPreferencesMode) {
        mSharedPreferencesMode = sharedPreferencesMode;
        mSharedPreferences = null;
    }

    public void setSharedPreferencesName(String sharedPreferencesName) {
        mSharedPreferencesName = sharedPreferencesName;
        mSharedPreferences = null;
    }

    boolean shouldCommit() {
        return !mNoCommit;
    }

    void unregisterOnActivityDestroyListener(OnActivityDestroyListener listener) {
        synchronized (this) {
            if (mActivityDestroyListeners != null) {
                mActivityDestroyListeners.remove(listener);
            }
        }
    }

    void unregisterOnActivityResultListener(OnActivityResultListener listener) {
        synchronized (this) {
            if (mActivityResultListeners != null) {
                mActivityResultListeners.remove(listener);
            }
        }
    }

    void unregisterOnActivityStopListener(OnActivityStopListener listener) {
        synchronized (this) {
            if (mActivityStopListeners != null) {
                mActivityStopListeners.remove(listener);
            }
        }
    }

}
