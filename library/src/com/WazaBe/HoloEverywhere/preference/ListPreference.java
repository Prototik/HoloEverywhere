package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.AlertDialog.Builder;

public class ListPreference extends DialogPreference {
	private static class SavedState extends BaseSavedState {
		String value;

		public SavedState(Parcel source) {
			super(source);
			value = source.readString();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(value);
		}
	}

	private int mClickedDialogEntryIndex;
	private CharSequence[] mEntries;
	private CharSequence[] mEntryValues;
	private String mSummary;

	private String mValue;

	public ListPreference(Context context) {
		this(context, null);
	}

	public ListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ListPreference, 0, 0);
		mEntries = a.getTextArray(R.styleable.ListPreference_entries);
		mEntryValues = a.getTextArray(R.styleable.ListPreference_entryValues);
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

	public CharSequence getEntry() {
		int index = getValueIndex();
		return index >= 0 && mEntries != null ? mEntries[index] : null;
	}

	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}

	@Override
	public CharSequence getSummary() {
		final CharSequence entry = getEntry();
		if (mSummary == null || entry == null) {
			return super.getSummary();
		} else {
			return String.format(mSummary, entry);
		}
	}

	public String getValue() {
		return mValue;
	}

	private int getValueIndex() {
		return findIndexOfValue(mValue);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult && mClickedDialogEntryIndex >= 0
				&& mEntryValues != null) {
			String value = mEntryValues[mClickedDialogEntryIndex].toString();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
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
					"ListPreference requires an entries array and an entryValues array.");
		}
		mClickedDialogEntryIndex = getValueIndex();
		builder.setSingleChoiceItems(mEntries, mClickedDialogEntryIndex,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mClickedDialogEntryIndex = which;
						ListPreference.this.onClick(dialog,
								DialogInterface.BUTTON_POSITIVE);
						dialog.dismiss();
					}
				});
		builder.setPositiveButton(null, null);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setValue(myState.value);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}
		final SavedState myState = new SavedState(superState);
		myState.value = getValue();
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValue(restoreValue ? getPersistedString(mValue)
				: (String) defaultValue);
	}

	public void setEntries(CharSequence[] entries) {
		mEntries = entries;
	}

	public void setEntries(int entriesResId) {
		setEntries(getContext().getResources().getTextArray(entriesResId));
	}

	public void setEntryValues(CharSequence[] entryValues) {
		mEntryValues = entryValues;
	}

	public void setEntryValues(int entryValuesResId) {
		setEntryValues(getContext().getResources().getTextArray(
				entryValuesResId));
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

	public void setValue(String value) {
		mValue = value;
		persistString(value);
	}

	public void setValueIndex(int index) {
		if (mEntryValues != null) {
			setValue(mEntryValues[index].toString());
		}
	}

}