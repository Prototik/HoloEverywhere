package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;

public class PreferenceCategory extends PreferenceGroup {
	private static final String TAG = "PreferenceCategory";

	public PreferenceCategory(Context context) {
		this(context, null);
	}

	public PreferenceCategory(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.preferenceCategoryStyle);
	}

	public PreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	protected boolean onPrepareAddPreference(Preference preference) {
		if (preference instanceof PreferenceCategory) {
			throw new IllegalArgumentException("Cannot add a " + TAG
					+ " directly to a " + TAG);
		}
		return super.onPrepareAddPreference(preference);
	}
}