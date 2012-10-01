package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.Formatter;

public class NumberPickerPreference extends DialogPreference {
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

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.numberPickerPreferenceStyle);
	}


	public NumberPickerPreference(Context context) {
		this(context, null);
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

	/**
	 * Saves the value to the {@link SharedPreferences}.
	 * 
	 * @param value The value to save
	 */
	public void setValue(int value) {
		final boolean wasBlocking = shouldDisableDependents();

		mValue = value;

		persistInt(value);

		final boolean isBlocking = shouldDisableDependents(); 
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		NumberPicker numberPicker = mNumberPicker;
		numberPicker.setValue(getValue());

		ViewParent oldParent = numberPicker.getParent();
        if (oldParent != view) {
    		if (oldParent != null) {
    			((ViewGroup) oldParent).removeView(numberPicker);
    		}
    		((ViewGroup) view).addView(numberPicker, ViewGroup.LayoutParams.WRAP_CONTENT,
    				ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state since it's persistent
			return superState;
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

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(value);
		}

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
}
