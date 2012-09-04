package com.WazaBe.HoloEverywhere.preference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.WazaBe.HoloEverywhere.preference.Preference.OnPreferenceChangeInternalListener;

class PreferenceGroupAdapter extends BaseAdapter implements
		OnPreferenceChangeInternalListener {
	private static class PreferenceLayout implements
			Comparable<PreferenceLayout> {
		private String name;
		private int resId;
		private int widgetResId;

		@Override
		public int compareTo(PreferenceLayout other) {
			int compareNames = name.compareTo(other.name);
			if (compareNames == 0) {
				if (resId == other.resId) {
					if (widgetResId == other.widgetResId) {
						return 0;
					} else {
						return widgetResId - other.widgetResId;
					}
				} else {
					return resId - other.resId;
				}
			} else {
				return compareNames;
			}
		}
	}

	private Handler mHandler = new Handler();
	private boolean mHasReturnedViewTypeCount = false;
	private volatile boolean mIsSyncing = false;
	private PreferenceGroup mPreferenceGroup;
	private ArrayList<PreferenceLayout> mPreferenceLayouts;
	private List<Preference> mPreferenceList;
	private Runnable mSyncRunnable = new Runnable() {
		@Override
		public void run() {
			syncMyPreferences();
		}
	};

	private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();

	public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
		mPreferenceGroup = preferenceGroup;
		mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
		mPreferenceList = new ArrayList<Preference>();
		mPreferenceLayouts = new ArrayList<PreferenceLayout>();
		syncMyPreferences();
	}

	private void addPreferenceClassName(Preference preference) {
		final PreferenceLayout pl = createPreferenceLayout(preference, null);
		int insertPos = Collections.binarySearch(mPreferenceLayouts, pl);
		if (insertPos < 0) {
			insertPos = insertPos * -1 - 1;
			mPreferenceLayouts.add(insertPos, pl);
		}
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	private PreferenceLayout createPreferenceLayout(Preference preference,
			PreferenceLayout in) {
		PreferenceLayout pl = in != null ? in : new PreferenceLayout();
		pl.name = preference.getClass().getName();
		pl.resId = preference.getLayoutResource();
		pl.widgetResId = preference.getWidgetLayoutResource();
		return pl;
	}

	private void flattenPreferenceGroup(List<Preference> preferences,
			PreferenceGroup group) {
		group.sortPreferences();

		final int groupSize = group.getPreferenceCount();
		for (int i = 0; i < groupSize; i++) {
			final Preference preference = group.getPreference(i);

			preferences.add(preference);

			if (!mHasReturnedViewTypeCount && !preference.hasSpecifiedLayout()) {
				addPreferenceClassName(preference);
			}

			if (preference instanceof PreferenceGroup) {
				final PreferenceGroup preferenceAsGroup = (PreferenceGroup) preference;
				if (preferenceAsGroup.isOnSameScreenAsChildren()) {
					flattenPreferenceGroup(preferences, preferenceAsGroup);
				}
			}

			preference.setOnPreferenceChangeInternalListener(this);
		}
	}

	@Override
	public int getCount() {
		return mPreferenceList.size();
	}

	@Override
	public Preference getItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		}
		return mPreferenceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= getCount()) {
			return AdapterView.INVALID_ROW_ID;
		}
		return getItem(position).getId();
	}

	@Override
	public int getItemViewType(int position) {
		if (!mHasReturnedViewTypeCount) {
			mHasReturnedViewTypeCount = true;
		}
		final Preference preference = getItem(position);
		if (preference.hasSpecifiedLayout()) {
			return IGNORE_ITEM_VIEW_TYPE;
		}
		mTempPreferenceLayout = createPreferenceLayout(preference,
				mTempPreferenceLayout);
		int viewType = Collections.binarySearch(mPreferenceLayouts,
				mTempPreferenceLayout);
		if (viewType < 0) {
			return IGNORE_ITEM_VIEW_TYPE;
		} else {
			return viewType;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Preference preference = getItem(position);
		mTempPreferenceLayout = createPreferenceLayout(preference,
				mTempPreferenceLayout);
		if (Collections.binarySearch(mPreferenceLayouts, mTempPreferenceLayout) < 0) {
			convertView = null;
		}
		return preference.getView(convertView, parent);
	}

	@Override
	public int getViewTypeCount() {
		if (!mHasReturnedViewTypeCount) {
			mHasReturnedViewTypeCount = true;
		}
		return Math.max(1, mPreferenceLayouts.size());
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position < 0 || position >= getCount()) {
			return true;
		}
		return getItem(position).isSelectable();
	}

	@Override
	public void onPreferenceChange(Preference preference) {
		notifyDataSetChanged();
	}

	@Override
	public void onPreferenceHierarchyChange(Preference preference) {
		mHandler.removeCallbacks(mSyncRunnable);
		mHandler.post(mSyncRunnable);
	}

	private void syncMyPreferences() {
		synchronized (this) {
			if (mIsSyncing) {
				return;
			}

			mIsSyncing = true;
		}

		List<Preference> newPreferenceList = new ArrayList<Preference>(
				mPreferenceList.size());
		flattenPreferenceGroup(newPreferenceList, mPreferenceGroup);
		mPreferenceList = newPreferenceList;

		notifyDataSetChanged();

		synchronized (this) {
			mIsSyncing = false;
			notifyAll();
		}
	}
}