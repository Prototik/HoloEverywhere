package com.WazaBe.HoloEverywhere.preference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.WazaBe.HoloEverywhere.util.BaseSharedPreferences;

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
	public static final String METADATA_KEY_PREFERENCES = "com.WazaBe.HoloEverywhere.preference";
	private static final String TAG = "PreferenceManager";

	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return new BaseSharedPreferences(context.getSharedPreferences(
				getDefaultSharedPreferencesName(context),
				getDefaultSharedPreferencesMode()));
	}

	private static int getDefaultSharedPreferencesMode() {
		return Context.MODE_PRIVATE;
	}

	private static String getDefaultSharedPreferencesName(Context context) {
		return context.getPackageName() + "_preferences";
	}

	public static void setDefaultValues(Context context, int resId,
			boolean readAgain) {

		// Use the default shared preferences name and mode
		setDefaultValues(context, getDefaultSharedPreferencesName(context),
				getDefaultSharedPreferencesMode(), resId, readAgain);
	}

	@SuppressLint("NewApi")
	public static void setDefaultValues(Context context,
			String sharedPreferencesName, int sharedPreferencesMode, int resId,
			boolean readAgain) {
		final SharedPreferences defaultValueSp = new BaseSharedPreferences(
				context.getSharedPreferences(KEY_HAS_SET_DEFAULT_VALUES,
						Context.MODE_PRIVATE));

		if (readAgain
				|| !defaultValueSp
						.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
			final PreferenceManager pm = new PreferenceManager(context);
			pm.setSharedPreferencesName(sharedPreferencesName);
			pm.setSharedPreferencesMode(sharedPreferencesMode);
			pm.inflateFromResource(context, resId, null);

			SharedPreferences.Editor editor = defaultValueSp.edit().putBoolean(
					KEY_HAS_SET_DEFAULT_VALUES, true);
			try {
				if (VERSION.SDK_INT < 9) {
					throw new AbstractMethodError();
				}
				editor.apply();
			} catch (AbstractMethodError unused) {
				editor.commit();
			}
		}
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

	PreferenceScreen getPreferenceScreen() {
		return mPreferenceScreen;
	}

	public SharedPreferences getSharedPreferences() {
		if (mSharedPreferences == null) {
			mSharedPreferences = new BaseSharedPreferences(
					mContext.getSharedPreferences(mSharedPreferencesName,
							mSharedPreferencesMode));
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
					|| !metaData.containsKey(METADATA_KEY_PREFERENCES)) {
				continue;
			}
			final String uniqueResId = activityInfo.packageName + ":"
					+ activityInfo.metaData.getInt(METADATA_KEY_PREFERENCES);

			if (!inflatedRes.contains(uniqueResId)) {
				inflatedRes.add(uniqueResId);

				final Context context;
				try {
					context = mContext.createPackageContext(
							activityInfo.packageName, 0);
				} catch (NameNotFoundException e) {
					Log.w(TAG,
							"Could not create context for "
									+ activityInfo.packageName + ": "
									+ Log.getStackTraceString(e));
					continue;
				}

				final PreferenceInflater inflater = new PreferenceInflater(
						context, this);
				final XmlResourceParser parser = activityInfo.loadXmlMetaData(
						context.getPackageManager(), METADATA_KEY_PREFERENCES);
				rootPreferences = (PreferenceScreen) inflater.inflate(parser,
						rootPreferences, true);
				parser.close();
			}
		}

		rootPreferences.onAttachedToHierarchy(this);

		return rootPreferences;
	}

	public PreferenceScreen inflateFromResource(Context context, int resId,
			PreferenceScreen rootPreferences) {
		// Block commits
		setNoCommit(true);

		final PreferenceInflater inflater = new PreferenceInflater(context,
				this);
		rootPreferences = (PreferenceScreen) inflater.inflate(resId,
				rootPreferences, true);
		rootPreferences.onAttachedToHierarchy(this);

		// Unblock commits
		setNoCommit(false);

		return rootPreferences;
	}

	private void init(Context context) {
		mContext = context;

		setSharedPreferencesName(getDefaultSharedPreferencesName(context));
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