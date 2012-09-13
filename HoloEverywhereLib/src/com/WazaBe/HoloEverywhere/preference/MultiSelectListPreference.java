package com.WazaBe.HoloEverywhere.preference;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.AlertDialog.Builder;

public class MultiSelectListPreference extends DialogPreference {
	private static class SavedState extends BaseSavedState {
		Set<String> values;

		public SavedState(Parcel source) {
			super(source);
			String[] v = source.createStringArray();
			values = new HashSet<String>(Arrays.asList(v));
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeStringArray(values.toArray(new String[values.size()]));
		}
	}

	private CharSequence[] mEntries;
	private CharSequence[] mEntryValues;
	private Set<String> mNewValues = new HashSet<String>();
	private boolean mPreferenceChanged;

	private Set<String> mValues = new HashSet<String>();

	public MultiSelectListPreference(Context context) {
		this(context, null);
	}

	public MultiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ListPreference, 0, 0);
		mEntries = a.getTextArray(R.styleable.ListPreference_entries);
		mEntryValues = a.getTextArray(R.styleable.ListPreference_entryValues);
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

	public CharSequence[] getEntryValues() {
		return mEntryValues;
	}

	private boolean[] getSelectedItems() {
		final CharSequence[] entries = mEntryValues;
		final int entryCount = entries.length;
		final Set<String> values = mValues;
		boolean[] result = new boolean[entryCount];

		for (int i = 0; i < entryCount; i++) {
			result[i] = values.contains(entries[i].toString());
		}

		return result;
	}

	public Set<String> getValues() {
		return mValues;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult && mPreferenceChanged) {
			final Set<String> values = mNewValues;
			if (callChangeListener(values)) {
				setValues(values);
			}
		}
		mPreferenceChanged = false;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		final CharSequence[] defaultValues = a.getTextArray(index);
		final Set<String> result = new HashSet<String>();
		if (defaultValues != null) {
			for (CharSequence s : defaultValues) {
				result.add(s.toString());
			}
		}
		return result;
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);

		if (mEntries == null || mEntryValues == null) {
			throw new IllegalStateException(
					"MultiSelectListPreference requires an entries array and "
							+ "an entryValues array.");
		}

		boolean[] checkedItems = getSelectedItems();
		builder.setMultiChoiceItems(mEntries, checkedItems,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							mPreferenceChanged |= mNewValues
									.add(mEntryValues[which].toString());
						} else {
							mPreferenceChanged |= mNewValues
									.remove(mEntryValues[which].toString());
						}
					}
				});
		mNewValues.clear();
		mNewValues.addAll(mValues);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// No need to save instance state
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.values = new HashSet<String>(getValues());
		return myState;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setValues(restoreValue ? getPersistedStringSet(mValues)
				: (Set<String>) defaultValue);
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

	public void setValues(Set<String> values) {
		mValues.clear();
		mValues.addAll(values);

		persistStringSet(values);
	}
}