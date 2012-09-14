package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.WazaBe.HoloEverywhere.R;

public class CheckBoxPreference extends TwoStatePreference {

	public CheckBoxPreference(Context context) {
		this(context, null);
	}

	public CheckBoxPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.checkBoxPreferenceStyle);
	}

	public CheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CheckBoxPreference, defStyle, 0);
		setSummaryOn(a.getString(R.styleable.CheckBoxPreference_summaryOn));
		setSummaryOff(a.getString(R.styleable.CheckBoxPreference_summaryOff));
		setDisableDependentsState(a.getBoolean(
				R.styleable.CheckBoxPreference_disableDependentsState, false));
		a.recycle();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		View checkboxView = view.findViewById(R.id.checkbox);
		if (checkboxView != null && checkboxView instanceof Checkable) {
			((Checkable) checkboxView).setChecked(mChecked);
			sendAccessibilityEvent(checkboxView);
		}
		syncSummaryView(view);
	}
}