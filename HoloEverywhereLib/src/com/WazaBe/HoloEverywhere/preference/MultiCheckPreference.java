package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.AlertDialog.Builder;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.util.Arrays;

public class MultiCheckPreference extends DialogPreference {
	private static class SavedState extends BaseSavedState {
		boolean[] values;

		public SavedState(Parcel source) {
			super(source);
			values = source.createBooleanArray();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeBooleanArray(values);
		}
	}

	private CharSequence[] mEntries;
	private String[] mEntryValues;
	private boolean[] mOrigValues;
	private boolean[] mSetValues;

	private String mSummary;

	public MultiCheckPreference(Context context) {
		this(context, null);
	}

	public MultiCheckPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ListPreference, 0, 0);
		mEntries = a.getTextArray(R.styleable.ListPreference_entries);
		if (mEntries != null) {
			setEntries(mEntries);
		}
		setEntryValuesCS(a.getTextArray(R.styleable.ListPreference_entryValues));
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.Preference, 0, 0);
		mSummary = a.getString(R.styleable.Preference_summary);
		a.recycle();
	}

	public int findIndexOfValue(String value) {
		if (value != null && mEntryValues != null) {
			for (int i = mEntryValues.length - 1; i >= 0; i--) {
				if (mEntryValues[i].equals(value)) {
					return i;
				}
			}
		}
		return -1;
	}

	public CharSequence[] getEntries() {
		return mEntries;
	}

	public String[] getEntryValues() {
		return mEntryValues;
	}

	@Override
	public CharSequence getSummary() {
		if (mSummary == null) {
			return super.getSummary();
		} else {
			return mSummary;
		}
	}

	public boolean getValue(int index) {
		return mSetValues[index];
	}

	public boolean[] getValues() {
		return mSetValues;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			if (callChangeListener(getValues())) {
				return;
			}
		}
		System.arraycopy(mOrigValues, 0, mSetValues, 0, mSetValues.length);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);

		if (mEntries == null || mEntryValues == null) {
			throw new IllegalStateException(
					"MultiCheckPreference requires an entries array and an entryValues array.");
		}

		mOrigValues = Arrays.copyOf(mSetValues, mSetValues.length);
		builder.setMultiChoiceItems(mEntries, mSetValues,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						mSetValues[which] = isChecked;
					}
				});
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setValues(myState.values);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state since it's persistent
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.values = getValues();
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
	}

	public void setEntries(CharSequence[] entries) {
		mEntries = entries;
		mSetValues = new boolean[entries.length];
		mOrigValues = new boolean[entries.length];
	}

	public void setEntries(int entriesResId) {
		setEntries(getContext().getResources().getTextArray(entriesResId));
	}

	public void setEntryValues(int entryValuesResId) {
		setEntryValuesCS(getContext().getResources().getTextArray(
				entryValuesResId));
	}

	public void setEntryValues(String[] entryValues) {
		mEntryValues = entryValues;
		Arrays.fill(mSetValues, false);
		Arrays.fill(mOrigValues, false);
	}

	private void setEntryValuesCS(CharSequence[] values) {
		setValues(null);
		if (values != null) {
			mEntryValues = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				mEntryValues[i] = values[i].toString();
			}
		}
	}

	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(summary);
		if (summary == null && mSummary != null) {
			mSummary = null;
		} else if (summary != null && !summary.equals(mSummary)) {
			mSummary = summary.toString();
		}
	}

	public void setValue(int index, boolean state) {
		mSetValues[index] = state;
	}

	public void setValues(boolean[] values) {
		if (mSetValues != null) {
			Arrays.fill(mSetValues, false);
			Arrays.fill(mOrigValues, false);
			if (values != null) {
				System.arraycopy(values, 0, mSetValues, 0,
						values.length < mSetValues.length ? values.length
								: mSetValues.length);
			}
		}
	}

}