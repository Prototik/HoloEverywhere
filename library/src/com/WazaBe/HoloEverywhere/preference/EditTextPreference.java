package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.EditText;

public class EditTextPreference extends DialogPreference {
	private static class SavedState extends BaseSavedState {
		String text;

		public SavedState(Parcel source) {
			super(source);
			text = source.readString();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(text);
		}
	}

	private EditText mEditText;

	private String mText;

	public EditTextPreference(Context context) {
		this(context, null);
	}

	public EditTextPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.editTextPreferenceStyle);
	}

	public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mEditText = new EditText(context, attrs);
		mEditText.setId(R.id.edit);
		mEditText.setEnabled(true);
	}

	public EditText getEditText() {
		return mEditText;
	}

	public String getText() {
		return mText;
	}

	@Override
	protected boolean needInputMethod() {
		return true;
	}

	protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
		ViewGroup container = (ViewGroup) dialogView
				.findViewById(R.id.edittext_container);
		if (container != null) {
			container.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		EditText editText = mEditText;
		editText.setText(getText());

		ViewParent oldParent = editText.getParent();
		if (oldParent != view) {
			if (oldParent != null) {
				((ViewGroup) oldParent).removeView(editText);
			}
			onAddEditTextToDialogView(view, editText);
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			String value = mEditText.getText().toString();
			if (callChangeListener(value)) {
				setText(value);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setText(myState.text);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}
		final SavedState myState = new SavedState(superState);
		myState.text = getText();
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setText(restoreValue ? getPersistedString(mText)
				: (String) defaultValue);
	}

	public void setText(String text) {
		final boolean wasBlocking = shouldDisableDependents();

		mText = text;

		persistString(text);

		final boolean isBlocking = shouldDisableDependents();
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	@Override
	public boolean shouldDisableDependents() {
		return TextUtils.isEmpty(mText) || super.shouldDisableDependents();
	}
}