package org.holoeverywhere.preference;

import org.holoeverywhere.widget.Switch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SwitchablePreferenceScreen extends PreferenceScreen {

	public SwitchablePreferenceScreen(Context context, PreferenceManager pm) {
		this(context, null, pm);
	}

	public SwitchablePreferenceScreen(Context context, AttributeSet attrs,
			PreferenceManager pm) {
		this(context, attrs, R.attr.switchPreferenceStyle, pm);
	}

	public SwitchablePreferenceScreen(Context context, AttributeSet attrs,
			int defStyle, PreferenceManager pm) {
		super(context, attrs);
		this.onAttachedToHierarchy(pm);
		
		// TwoStatePreference
		context = getContext();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TwoStatePreference, defStyle,
				R.style.Holo_PreferenceTwoState);
		mSummaryOn = a.getText(R.styleable.TwoStatePreference_summaryOn);
		mSummaryOff = a.getText(R.styleable.TwoStatePreference_summaryOff);
		mDisableDependentsState = a.getBoolean(
				R.styleable.TwoStatePreference_disableDependentsState, false);
		a.recycle();

		// SwitchPreference
		a = context.obtainStyledAttributes(attrs, R.styleable.SwitchPreference,
				defStyle, R.style.Holo_PreferenceSwitch);
		mSwitchOn = a.getString(R.styleable.SwitchPreference_switchTextOn);
		mSwitchOff = a.getString(R.styleable.SwitchPreference_switchTextOff);
		a.recycle();
		
		this.setWidgetLayoutResource(R.layout.preference_widget_switch_screen);
	}

	// from TwoStatePreference
	static class SavedState extends BaseSavedState {
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		boolean checked;

		public SavedState(Parcel source) {
			super(source);
			checked = source.readInt() == 1;
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(checked ? 1 : 0);
		}
	}

	boolean mChecked;
	private boolean mDisableDependentsState;
	private boolean mSendClickAccessibilityEvent;
	private CharSequence mSummaryOff, mSummaryOn;

	public boolean getDisableDependentsState() {
		return mDisableDependentsState;
	}

	public CharSequence getSummaryOff() {
		return mSummaryOff;
	}

	public CharSequence getSummaryOn() {
		return mSummaryOn;
	}

	public boolean isChecked() {
		return mChecked;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getBoolean(index, false);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setChecked(myState.checked);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.checked = isChecked();
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setChecked(restoreValue ? getPersistedBoolean(mChecked)
				: (Boolean) defaultValue);
	}

	@SuppressLint("NewApi")
	void sendAccessibilityEvent(View view) {
		try {
			AccessibilityManager accessibilityManager = (AccessibilityManager) getContext()
					.getSystemService(Context.ACCESSIBILITY_SERVICE);
			if (mSendClickAccessibilityEvent
					&& accessibilityManager.isEnabled()) {
				AccessibilityEvent event = AccessibilityEvent.obtain();
				event.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
				if (VERSION.SDK_INT >= 14) {
					view.onInitializeAccessibilityEvent(event);
				}
				view.dispatchPopulateAccessibilityEvent(event);
				accessibilityManager.sendAccessibilityEvent(event);
			}
		} catch (Exception e) {
		}
		mSendClickAccessibilityEvent = false;
	}

	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			persistBoolean(checked);
			notifyDependencyChange(shouldDisableDependents());
			notifyChanged();
		}
	}

	public void setDisableDependentsState(boolean disableDependentsState) {
		mDisableDependentsState = disableDependentsState;
	}

	public void setSummaryOff(CharSequence summary) {
		mSummaryOff = summary;
		if (!isChecked()) {
			notifyChanged();
		}
	}

	public void setSummaryOff(int summaryResId) {
		setSummaryOff(getContext().getString(summaryResId));
	}

	public void setSummaryOn(CharSequence summary) {
		mSummaryOn = summary;
		if (isChecked()) {
			notifyChanged();
		}
	}

	public void setSummaryOn(int summaryResId) {
		setSummaryOn(getContext().getString(summaryResId));
	}

	@Override
	public boolean shouldDisableDependents() {
		return mDisableDependentsState ? mChecked : !mChecked
				|| super.shouldDisableDependents();
	}

	void syncSummaryView(View view) {
		TextView summaryView = (TextView) view.findViewById(R.id.summary);
		if (summaryView != null) {
			boolean useDefaultSummary = true;
			if (mChecked && mSummaryOn != null) {
				summaryView.setText(mSummaryOn);
				useDefaultSummary = false;
			} else if (!mChecked && mSummaryOff != null) {
				summaryView.setText(mSummaryOff);
				useDefaultSummary = false;
			}
			if (useDefaultSummary) {
				final CharSequence summary = getSummary();
				if (summary != null) {
					summaryView.setText(summary);
					useDefaultSummary = false;
				}
			}
			int newVisibility = useDefaultSummary ? View.GONE : View.VISIBLE;
			if (newVisibility != summaryView.getVisibility()) {
				summaryView.setVisibility(newVisibility);
			}
		}
	}

	// from SwitchPreference
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
	private CharSequence mSwitchOff, mSwitchOn;

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
		setSwitchTextOff(getContext().getText(resId));
	}

	public void setSwitchTextOn(CharSequence onText) {
		mSwitchOn = onText;
		notifyChanged();
	}

	public void setSwitchTextOn(int resId) {
		setSwitchTextOn(getContext().getText(resId));
	}

}
