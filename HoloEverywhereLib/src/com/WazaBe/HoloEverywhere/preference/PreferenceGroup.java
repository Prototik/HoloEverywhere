package com.WazaBe.HoloEverywhere.preference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;

public abstract class PreferenceGroup extends Preference implements
		GenericInflater.Parent<Preference> {
	private boolean mAttachedToActivity = false;
	private int mCurrentPreferenceOrder = 0;
	private boolean mOrderingAsAdded = true;
	private List<Preference> mPreferenceList;

	public PreferenceGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PreferenceGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPreferenceList = new ArrayList<Preference>();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PreferenceGroup, defStyle, 0);
		mOrderingAsAdded = a.getBoolean(
				R.styleable.PreferenceGroup_orderingFromXml, mOrderingAsAdded);
		a.recycle();
	}

	@Override
	public void addItemFromInflater(Preference preference) {
		addPreference(preference);
	}

	public boolean addPreference(Preference preference) {
		if (mPreferenceList.contains(preference)) {
			// Exists
			return true;
		}

		if (preference.getOrder() == Preference.DEFAULT_ORDER) {
			if (mOrderingAsAdded) {
				preference.setOrder(mCurrentPreferenceOrder++);
			}

			if (preference instanceof PreferenceGroup) {
				((PreferenceGroup) preference)
						.setOrderingAsAdded(mOrderingAsAdded);
			}
		}

		int insertionIndex = Collections.binarySearch(mPreferenceList,
				preference);
		if (insertionIndex < 0) {
			insertionIndex = insertionIndex * -1 - 1;
		}

		if (!onPrepareAddPreference(preference)) {
			return false;
		}

		synchronized (this) {
			mPreferenceList.add(insertionIndex, preference);
		}

		preference.onAttachedToHierarchy(getPreferenceManager());

		if (mAttachedToActivity) {
			preference.onAttachedToActivity();
		}

		notifyHierarchyChanged();

		return true;
	}

	@Override
	protected void dispatchRestoreInstanceState(Bundle container) {
		super.dispatchRestoreInstanceState(container);
		final int preferenceCount = getPreferenceCount();
		for (int i = 0; i < preferenceCount; i++) {
			getPreference(i).dispatchRestoreInstanceState(container);
		}
	}

	@Override
	protected void dispatchSaveInstanceState(Bundle container) {
		super.dispatchSaveInstanceState(container);
		final int preferenceCount = getPreferenceCount();
		for (int i = 0; i < preferenceCount; i++) {
			getPreference(i).dispatchSaveInstanceState(container);
		}
	}

	public Preference findPreference(CharSequence key) {
		if (TextUtils.equals(getKey(), key)) {
			return this;
		}
		final int preferenceCount = getPreferenceCount();
		for (int i = 0; i < preferenceCount; i++) {
			final Preference preference = getPreference(i);
			final String curKey = preference.getKey();

			if (curKey != null && curKey.equals(key)) {
				return preference;
			}

			if (preference instanceof PreferenceGroup) {
				final Preference returnedPreference = ((PreferenceGroup) preference)
						.findPreference(key);
				if (returnedPreference != null) {
					return returnedPreference;
				}
			}
		}

		return null;
	}

	public Preference getPreference(int index) {
		return mPreferenceList.get(index);
	}

	public int getPreferenceCount() {
		return mPreferenceList.size();
	}

	protected boolean isOnSameScreenAsChildren() {
		return true;
	}

	public boolean isOrderingAsAdded() {
		return mOrderingAsAdded;
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();
		mAttachedToActivity = true;
		final int preferenceCount = getPreferenceCount();
		for (int i = 0; i < preferenceCount; i++) {
			getPreference(i).onAttachedToActivity();
		}
	}

	protected boolean onPrepareAddPreference(Preference preference) {
		if (!super.isEnabled()) {
			preference.setEnabled(false);
		}

		return true;
	}

	@Override
	protected void onPrepareForRemoval() {
		super.onPrepareForRemoval();
		mAttachedToActivity = false;
	}

	public void removeAll() {
		synchronized (this) {
			List<Preference> preferenceList = mPreferenceList;
			for (int i = preferenceList.size() - 1; i >= 0; i--) {
				removePreferenceInt(preferenceList.get(0));
			}
		}
		notifyHierarchyChanged();
	}

	public boolean removePreference(Preference preference) {
		final boolean returnValue = removePreferenceInt(preference);
		notifyHierarchyChanged();
		return returnValue;
	}

	private boolean removePreferenceInt(Preference preference) {
		synchronized (this) {
			preference.onPrepareForRemoval();
			return mPreferenceList.remove(preference);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		final int preferenceCount = getPreferenceCount();
		for (int i = 0; i < preferenceCount; i++) {
			getPreference(i).setEnabled(enabled);
		}
	}

	public void setOrderingAsAdded(boolean orderingAsAdded) {
		mOrderingAsAdded = orderingAsAdded;
	}

	void sortPreferences() {
		synchronized (this) {
			Collections.sort(mPreferenceList);
		}
	}
}