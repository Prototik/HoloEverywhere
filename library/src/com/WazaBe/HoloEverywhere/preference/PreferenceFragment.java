package com.WazaBe.HoloEverywhere.preference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.Fragment;
import com.WazaBe.HoloEverywhere.widget.ListView;

public abstract class PreferenceFragment extends Fragment implements
		PreferenceManager.OnPreferenceTreeClickListener {
	public interface OnPreferenceStartFragmentCallback {
		boolean onPreferenceStartFragment(PreferenceFragment caller,
				Preference pref);
	}

	private static final int FIRST_REQUEST_CODE = 100;
	private static final int MSG_BIND_PREFERENCES = 1;
	private static final String PREFERENCES_TAG = "android:preferences";
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_BIND_PREFERENCES:
				bindPreferences();
				break;
			}
		}
	};

	private boolean mHavePrefs, mInitDone;
	private ListView mList;
	private OnKeyListener mListOnKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Object selectedItem = mList.getSelectedItem();
			if (selectedItem instanceof Preference) {
				View selectedView = mList.getSelectedView();
				return ((Preference) selectedItem).onKey(selectedView, keyCode,
						event);
			}
			return false;
		}

	};

	private PreferenceManager mPreferenceManager;

	final private Runnable mRequestFocus = new Runnable() {
		@Override
		public void run() {
			mList.focusableViewAvailable(mList);
		}
	};

	public void addPreferencesFromIntent(Intent intent) {
		requirePreferenceManager();

		setPreferenceScreen(mPreferenceManager.inflateFromIntent(intent,
				getPreferenceScreen()));
	}

	public void addPreferencesFromResource(int preferencesResId) {
		requirePreferenceManager();

		setPreferenceScreen(mPreferenceManager.inflateFromResource(
				getActivity(), preferencesResId, getPreferenceScreen()));
	}

	private void bindPreferences() {
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		if (preferenceScreen != null) {
			preferenceScreen.bind(getListView());
		}
	}

	private void ensureList() {
		if (mList != null) {
			return;
		}
		View root = getView();
		if (root == null) {
			throw new IllegalStateException("Content view not yet created");
		}
		View rawListView = root.findViewById(android.R.id.list);
		if (!(rawListView instanceof ListView)) {
			throw new RuntimeException(
					"Content has view with id attribute 'android.R.id.list' "
							+ "that is not a ListView class");
		}
		mList = (ListView) rawListView;
		if (mList == null) {
			throw new RuntimeException(
					"Your content must have a ListView whose id attribute is "
							+ "'android.R.id.list'");
		}
		mList.setOnKeyListener(mListOnKeyListener);
		mHandler.post(mRequestFocus);
	}

	public Preference findPreference(CharSequence key) {
		if (mPreferenceManager == null) {
			return null;
		}
		return mPreferenceManager.findPreference(key);
	}

	public ListView getListView() {
		ensureList();
		return mList;
	}

	public PreferenceManager getPreferenceManager() {
		return mPreferenceManager;
	}

	public PreferenceScreen getPreferenceScreen() {
		return mPreferenceManager.getPreferenceScreen();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mHavePrefs) {
			bindPreferences();
		}
		mInitDone = true;
		if (savedInstanceState != null) {
			Bundle container = savedInstanceState.getBundle(PREFERENCES_TAG);
			if (container != null) {
				final PreferenceScreen preferenceScreen = getPreferenceScreen();
				if (preferenceScreen != null) {
					preferenceScreen.restoreHierarchyState(container);
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPreferenceManager
				.dispatchActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPreferenceManager = new PreferenceManager(getActivity(),
				FIRST_REQUEST_CODE);
		mPreferenceManager.setFragment(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.preference_list_fragment, container,
				false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPreferenceManager.dispatchActivityDestroy();
	}

	@Override
	public void onDestroyView() {
		mList = null;
		mHandler.removeCallbacks(mRequestFocus);
		mHandler.removeMessages(MSG_BIND_PREFERENCES);
		super.onDestroyView();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference.getFragment() != null
				&& getActivity() instanceof OnPreferenceStartFragmentCallback) {
			return ((OnPreferenceStartFragmentCallback) getActivity())
					.onPreferenceStartFragment(this, preference);
		}
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		if (preferenceScreen != null) {
			Bundle container = new Bundle();
			preferenceScreen.saveHierarchyState(container);
			outState.putBundle(PREFERENCES_TAG, container);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mPreferenceManager.setOnPreferenceTreeClickListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		mPreferenceManager.dispatchActivityStop();
		mPreferenceManager.setOnPreferenceTreeClickListener(null);
	}

	private void postBindPreferences() {
		if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) {
			return;
		}
		mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
	}

	private void requirePreferenceManager() {
		if (mPreferenceManager == null) {
			throw new RuntimeException(
					"This should be called after super.onCreate.");
		}
	}

	public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
		if (mPreferenceManager.setPreferences(preferenceScreen)
				&& preferenceScreen != null) {
			mHavePrefs = true;
			if (mInitDone) {
				postBindPreferences();
			}
		}
	}
}