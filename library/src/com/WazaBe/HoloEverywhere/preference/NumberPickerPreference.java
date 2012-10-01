package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.Formatter;

public class NumberPickerPreference extends DialogPreference {
<<<<<<< HEAD
	public static interface OnBindPickerListener {
		public void onBindPicker(NumberPicker picker,
				NumberPickerPreference preference);
	}

	private static final class SavedState extends BaseSavedState {
		protected int min, max, value;
		protected boolean wrapSelectorWhell;

		public SavedState(Parcel source) {
			super(source);
			min = source.readInt();
			max = source.readInt();
			value = source.readInt();
			wrapSelectorWhell = source.readInt() == 1;
		}

		public SavedState(Parcelable superState, int min, int max, int value,
				boolean wrapSelectorWhell) {
			super(superState);
			this.min = min;
			this.max = max;
			this.value = value;
			this.wrapSelectorWhell = wrapSelectorWhell;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(min);
			dest.writeInt(max);
			dest.writeInt(value);
			dest.writeInt(wrapSelectorWhell ? 1 : 0);
		}
	}

	private NumberPicker lastNumberPicker;
	private int min, max, value;
	private OnBindPickerListener onBindPickerListener;
	private long onLongPressUpdateInterval = 0;
	private OnScrollListener onScrollListener;
	private OnValueChangeListener onValueChangedListener;
	private boolean wrapSelectorWhell;
=======
	private int mValue = 0;
	private NumberPicker mNumberPicker;

	/**
	 * TODO see NumberPickerPreference(Context context, AttributeSet attrs)
	 * this constructor is broken
	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	*/

	/**
	 * TODO there is no button bar using this constructor. This is a workaround for now.
	 * When fixed we should call this method inside the constructor like EditTextPreference:
	 * this(context, attrs, R.attr.numberPickerPreferenceStyle);
	 * and move all the logic into NumberPickerPreference(Context context, AttributeSet attrs, int defStyle)
	 */ 
	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs);
		mNumberPicker = new NumberPicker(context, attrs);
		mNumberPicker.setId(R.id.numberPicker);
		mNumberPicker.setFocusable(true);
		mNumberPicker.setFocusableInTouchMode(true);
		mNumberPicker.setEnabled(true);

		// some sane defaults if not provided in xml
		onSetInitialValue(true, mValue);
		int mMinValue = 0, mMaxValue = 100;
		boolean mWrapSelectorWheel = true;
		TypedArray numberPickerAttrs = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
		mMinValue = numberPickerAttrs.getInt(R.styleable.NumberPickerPreference_minValue, mMinValue);
		mMaxValue = numberPickerAttrs.getInt(R.styleable.NumberPickerPreference_maxValue, mMaxValue);
		mWrapSelectorWheel = numberPickerAttrs.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheel, mWrapSelectorWheel);

		mNumberPicker.setMinValue(mMinValue);
		mNumberPicker.setMaxValue(mMaxValue);
		mNumberPicker.setWrapSelectorWheel(mWrapSelectorWheel);

		/**
		 * Force dialog resource until constructor issue is fixed.
		 */
		setDialogLayoutResource(R.layout.preference_dialog_numberpicker);
	}
>>>>>>> saik0/npp_fixup

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.numberPickerPreferenceStyle);
	}

<<<<<<< HEAD
	public NumberPickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.NumberPickerPreference, defStyle,
				R.style.Holo_PreferenceDialog_NumberPickerPreference);
		min = a.getInteger(R.styleable.NumberPickerPreference_min, 0);
		max = a.getInteger(R.styleable.NumberPickerPreference_max, 10);
		wrapSelectorWhell = a.getBoolean(
				R.styleable.NumberPickerPreference_wrapSelectorWhell, true);
		a.recycle();
	}

	protected NumberPicker getLastNumberPicker() {
		return lastNumberPicker;
=======

	public NumberPickerPreference(Context context) {
		this(context, null);
>>>>>>> saik0/npp_fixup
	}

	/**
	 * Returns the {@link NumberPicker} widget that will be shown in the dialog.
	 * 
	 * @return The {@link NumberPicker} widget that will be shown in the dialog.
	 */
	public NumberPicker getNumberPicker() {
		return mNumberPicker;
	}

	/**
	 * Gets the value from the {@link Formatter}.
	 * 
	 * @return The current preference value.
	 */
	public int getValue() {
		return mValue;
	}

<<<<<<< HEAD
	public OnBindPickerListener getOnBindPickerListener() {
		return onBindPickerListener;
	}

	public long getOnLongPressUpdateInterval() {
		return onLongPressUpdateInterval;
	}
=======
	/**
	 * Saves the value to the {@link SharedPreferences}.
	 * 
	 * @param value The value to save
	 */
	public void setValue(int value) {
		final boolean wasBlocking = shouldDisableDependents();
>>>>>>> saik0/npp_fixup

		mValue = value;

		persistInt(value);

		final boolean isBlocking = shouldDisableDependents(); 
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	public int getValue() {
		return value;
	}

	public boolean isWrapSelectorWhell() {
		return wrapSelectorWhell;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		NumberPicker numberPicker = mNumberPicker;
		numberPicker.setValue(getValue());

<<<<<<< HEAD
	protected void onBindPickerView(NumberPicker picker) {
		picker.setMinValue(min);
		picker.setMaxValue(max);
		picker.setValue(value);
		picker.setOnScrollListener(onScrollListener);
		picker.setOnValueChangedListener(onValueChangedListener);
		if (onLongPressUpdateInterval > 0) {
			picker.setOnLongPressUpdateInterval(onLongPressUpdateInterval);
		}
		if (onBindPickerListener != null) {
			onBindPickerListener.onBindPicker(picker, this);
		}
=======
		ViewParent oldParent = numberPicker.getParent();
        if (oldParent != view) {
    		if (oldParent != null) {
    			((ViewGroup) oldParent).removeView(numberPicker);
    		}
    		((ViewGroup) view).addView(numberPicker, ViewGroup.LayoutParams.WRAP_CONTENT,
    				ViewGroup.LayoutParams.WRAP_CONTENT);
        }
>>>>>>> saik0/npp_fixup
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int value = mNumberPicker.getValue();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInteger(index, 0);
	}

	@Override
<<<<<<< HEAD
	protected void onPrepareForRemoval() {
		if (lastNumberPicker != null) {
			lastNumberPicker.setOnValueChangedListener(null);
			lastNumberPicker.setOnScrollListener(null);
		}
		super.onPrepareForRemoval();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (isPersistent()) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState s = (SavedState) state;
		super.onRestoreInstanceState(s.getSuperState());
		if (lastNumberPicker != null) {
			lastNumberPicker.setMinValue(s.min);
			lastNumberPicker.setMaxValue(s.max);
			lastNumberPicker.setValue(s.value);
			lastNumberPicker.setWrapSelectorWheel(s.wrapSelectorWhell);
		}
=======
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
>>>>>>> saik0/npp_fixup
	}

	@Override
	protected Parcelable onSaveInstanceState() {
<<<<<<< HEAD
		Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}
		if (lastNumberPicker == null) {
			return new SavedState(superState, min, max, value,
					wrapSelectorWhell);
		} else {
			return new SavedState(superState, lastNumberPicker.getMinValue(),
					lastNumberPicker.getMaxValue(),
					lastNumberPicker.getValue(),
					lastNumberPicker.getWrapSelectorWheel());
=======
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state since it's persistent
			return superState;
>>>>>>> saik0/npp_fixup
		}

		final SavedState myState = new SavedState(superState);
		myState.value = getValue();
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setValue(myState.value);
	}

	private static class SavedState extends BaseSavedState {
		int value;

		public SavedState(Parcel source) {
			super(source);
			value = source.readInt();
		}

<<<<<<< HEAD
	public void setOnBindPickerListener(
			OnBindPickerListener onBindPickerListener) {
		this.onBindPickerListener = onBindPickerListener;
	}

	public void setOnLongPressUpdateInterval(long onLongPressUpdateInterval) {
		this.onLongPressUpdateInterval = onLongPressUpdateInterval;
		notifyChanged();
	}
=======
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(value);
		}
>>>>>>> saik0/npp_fixup

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public void setValue(int value) {
		if (this.value == value) {
			return;
		}
		this.value = value;
		persistInt(value);
		notifyChanged();
	}

	public void setWrapSelectorWhell(boolean wrapSelectorWhell) {
		this.wrapSelectorWhell = wrapSelectorWhell;
		notifyChanged();
	}
}
