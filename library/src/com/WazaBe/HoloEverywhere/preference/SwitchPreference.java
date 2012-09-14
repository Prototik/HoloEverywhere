package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.Switch;

public class SwitchPreference extends TwoStatePreference {
	private class Listener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (!callChangeListener(isChecked)) {
				buttonView.setChecked(!isChecked);
				return;
			}
			setChecked(isChecked);
		}
	}

	private final Listener mListener = new Listener();
	private CharSequence mSwitchOff;

	private CharSequence mSwitchOn;

	public SwitchPreference(Context context) {
		this(context, null);
	}

	public SwitchPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.switchPreferenceStyle);
	}

	public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SwitchPreference, defStyle, 0);
		setSummaryOn(a.getString(R.styleable.SwitchPreference_summaryOn));
		setSummaryOff(a.getString(R.styleable.SwitchPreference_summaryOff));
		setSwitchTextOn(a.getString(R.styleable.SwitchPreference_switchTextOn));
		setSwitchTextOff(a
				.getString(R.styleable.SwitchPreference_switchTextOff));
		setDisableDependentsState(a.getBoolean(
				R.styleable.SwitchPreference_disableDependentsState, false));
		a.recycle();
	}

	public CharSequence getSwitchTextOff() {
		return mSwitchOff;
	}

	public CharSequence getSwitchTextOn() {
		return mSwitchOn;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		View checkableView = view.findViewById(R.id.switchWidget);
		if (checkableView != null && checkableView instanceof Checkable) {
			((Checkable) checkableView).setChecked(mChecked);
			sendAccessibilityEvent(checkableView);
			if (checkableView instanceof Switch) {
				final Switch switchView = (Switch) checkableView;
				switchView.setTextOn(mSwitchOn);
				switchView.setTextOff(mSwitchOff);
				switchView.setOnCheckedChangeListener(mListener);
			}
		}
		syncSummaryView(view);
	}

	public void setSwitchTextOff(CharSequence offText) {
		mSwitchOff = offText;
		notifyChanged();
	}

	public void setSwitchTextOff(int resId) {
		setSwitchTextOff(getContext().getString(resId));
	}

	public void setSwitchTextOn(CharSequence onText) {
		mSwitchOn = onText;
		notifyChanged();
	}

	public void setSwitchTextOn(int resId) {
		setSwitchTextOn(getContext().getString(resId));
	}
}
