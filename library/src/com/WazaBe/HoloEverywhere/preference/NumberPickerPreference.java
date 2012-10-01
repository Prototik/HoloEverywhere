package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.NumberPicker;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.OnScrollListener;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.OnValueChangeListener;

public class NumberPickerPreference extends DialogPreference {
	private static final class SavedState extends BaseSavedState {
		protected int min, max, value;

		public SavedState(Parcel source) {
			super(source);
			min = source.readInt();
			max = source.readInt();
			value = source.readInt();
		}

		public SavedState(Parcelable superState, int min, int max, int value) {
			super(superState);
			this.min = min;
			this.max = max;
			this.value = value;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(min);
			dest.writeInt(max);
			dest.writeInt(value);
		}
	}

	private NumberPicker lastNumberPicker;
	private int min, max, value;
	private long onLongPressUpdateInterval = 0;
	private OnScrollListener onScrollListener;
	private OnValueChangeListener onValueChangedListener;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.numberPickerPreferenceStyle);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.NumberPickerPreference, defStyle,
				R.style.Holo_PreferenceDialog_NumberPickerPreference);
		min = a.getInteger(R.styleable.NumberPickerPreference_min, 0);
		max = a.getInteger(R.styleable.NumberPickerPreference_max, 10);
		a.recycle();
	}

	public NumberPicker getLastNumberPicker() {
		return lastNumberPicker;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public long getOnLongPressUpdateInterval() {
		return onLongPressUpdateInterval;
	}

	public OnScrollListener getOnScrollListener() {
		return onScrollListener;
	}

	public OnValueChangeListener getOnValueChangedListener() {
		return onValueChangedListener;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		lastNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
		if (lastNumberPicker != null) {
			onBindPickerView(lastNumberPicker);
		}
	}

	protected void onBindPickerView(NumberPicker picker) {
		picker.setMinValue(min);
		picker.setMaxValue(max);
		picker.setValue(value);
		picker.setOnScrollListener(onScrollListener);
		picker.setOnValueChangedListener(onValueChangedListener);
		if (onLongPressUpdateInterval > 0) {
			picker.setOnLongPressUpdateInterval(onLongPressUpdateInterval);
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (lastNumberPicker == null) {
			return;
		}
		if (positiveResult) {
			int newValue = lastNumberPicker.getValue();
			if (callChangeListener(newValue)) {
				persistInt(newValue);
				value = newValue;
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		try {
			return Integer.parseInt(a.getString(index));
		} catch (NumberFormatException e) {
			return min;
		}
	}

	@Override
	protected void onPrepareForRemoval() {
		lastNumberPicker.setOnValueChangedListener(null);
		lastNumberPicker.setOnScrollListener(null);
		super.onPrepareForRemoval();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState s = (SavedState) state;
		super.onRestoreInstanceState(s.getSuperState());
		if (lastNumberPicker != null) {
			lastNumberPicker.setMinValue(s.min);
			lastNumberPicker.setMaxValue(s.max);
			lastNumberPicker.setValue(s.value);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		if (lastNumberPicker == null) {
			return new SavedState(super.onSaveInstanceState(), min, max, value);
		} else {
			return new SavedState(super.onSaveInstanceState(),
					lastNumberPicker.getMinValue(),
					lastNumberPicker.getMaxValue(), lastNumberPicker.getValue());
		}
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			value = getPersistedInt(min);
		} else {
			value = defaultValue instanceof Integer ? (Integer) defaultValue
					: Integer.parseInt(defaultValue.toString());
		}
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		super.setDefaultValue(defaultValue);
		value = defaultValue instanceof Integer ? (Integer) defaultValue
				: Integer.parseInt(defaultValue.toString());
	}

	public void setMax(int max) {
		this.max = max;
		notifyChanged();
	}

	public void setMin(int min) {
		this.min = min;
		notifyChanged();
	}

	public void setOnLongPressUpdateInterval(long onLongPressUpdateInterval) {
		this.onLongPressUpdateInterval = onLongPressUpdateInterval;
		notifyChanged();
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
		notifyChanged();
	}

	public void setOnValueChangedListener(
			OnValueChangeListener onValueChangeListener) {
		onValueChangedListener = onValueChangeListener;
		notifyChanged();
	}

	public void setRange(int min, int max) {
		this.min = min;
		this.max = max;
		notifyChanged();
	}
}
