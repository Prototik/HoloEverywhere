package com.WazaBe.HoloEverywhere.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.AlertDialog;

public abstract class DialogPreference extends Preference implements
		DialogInterface.OnClickListener, DialogInterface.OnDismissListener,
		PreferenceManager.OnActivityDestroyListener {
	private static class SavedState extends BaseSavedState {
		Bundle dialogBundle;
		boolean isDialogShowing;

		public SavedState(Parcel source) {
			super(source);
			isDialogShowing = source.readInt() == 1;
			dialogBundle = source.readBundle();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(isDialogShowing ? 1 : 0);
			dest.writeBundle(dialogBundle);
		}
	}

	private AlertDialog.Builder mBuilder;
	private Dialog mDialog;
	private Drawable mDialogIcon;
	private int mDialogLayoutResId;
	private CharSequence mDialogMessage;
	private CharSequence mDialogTitle;
	private CharSequence mNegativeButtonText;
	private CharSequence mPositiveButtonText;

	private int mWhichButtonClicked;

	public DialogPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.dialogPreferenceStyle);
	}

	public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DialogPreference, defStyle, 0);
		mDialogTitle = a.getString(R.styleable.DialogPreference_dialogTitle);
		if (mDialogTitle == null) {
			mDialogTitle = getTitle();
		}
		mDialogMessage = a
				.getString(R.styleable.DialogPreference_dialogMessage);
		mDialogIcon = a.getDrawable(R.styleable.DialogPreference_dialogIcon);
		mPositiveButtonText = a
				.getString(R.styleable.DialogPreference_positiveButtonText);
		mNegativeButtonText = a
				.getString(R.styleable.DialogPreference_negativeButtonText);
		mDialogLayoutResId = a.getResourceId(
				R.styleable.DialogPreference_dialogLayout, mDialogLayoutResId);
		a.recycle();

	}

	public Dialog getDialog() {
		return mDialog;
	}

	public Drawable getDialogIcon() {
		return mDialogIcon;
	}

	public int getDialogLayoutResource() {
		return mDialogLayoutResId;
	}

	public CharSequence getDialogMessage() {
		return mDialogMessage;
	}

	public CharSequence getDialogTitle() {
		return mDialogTitle;
	}

	public CharSequence getNegativeButtonText() {
		return mNegativeButtonText;
	}

	public CharSequence getPositiveButtonText() {
		return mPositiveButtonText;
	}

	protected boolean needInputMethod() {
		return false;
	}

	@Override
	public void onActivityDestroy() {

		if (mDialog == null || !mDialog.isShowing()) {
			return;
		}

		mDialog.dismiss();
	}

	protected void onBindDialogView(View view) {
		View dialogMessageView = view.findViewById(R.id.message);
		if (dialogMessageView != null) {
			final CharSequence message = getDialogMessage();
			int newVisibility = View.GONE;
			if (!TextUtils.isEmpty(message)) {
				if (dialogMessageView instanceof TextView) {
					((TextView) dialogMessageView).setText(message);
				}
				newVisibility = View.VISIBLE;
			}
			if (dialogMessageView.getVisibility() != newVisibility) {
				dialogMessageView.setVisibility(newVisibility);
			}
		}
	}

	@Override
	protected void onClick() {
		if (mDialog != null && mDialog.isShowing()) {
			return;
		}
		showDialog(null);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		mWhichButtonClicked = which;
	}

	protected View onCreateDialogView() {
		if (mDialogLayoutResId == 0) {
			return null;
		}
		return LayoutInflater.inflate(getContext(), mDialogLayoutResId);
	}

	protected void onDialogClosed(boolean positiveResult) {
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		getPreferenceManager().unregisterOnActivityDestroyListener(this);

		mDialog = null;
		onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
	}

	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (myState.isDialogShowing) {
			showDialog(myState.dialogBundle);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (mDialog == null || !mDialog.isShowing()) {
			return superState;
		}
		final SavedState myState = new SavedState(superState);
		myState.isDialogShowing = true;
		myState.dialogBundle = mDialog.onSaveInstanceState();
		return myState;
	}

	private void requestInputMethod(Dialog dialog) {
		Window window = dialog.getWindow();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void setDialogIcon(Drawable dialogIcon) {
		mDialogIcon = dialogIcon;
	}

	public void setDialogIcon(int dialogIconRes) {
		mDialogIcon = getContext().getResources().getDrawable(dialogIconRes);
	}

	public void setDialogLayoutResource(int dialogLayoutResId) {
		mDialogLayoutResId = dialogLayoutResId;
	}

	public void setDialogMessage(CharSequence dialogMessage) {
		mDialogMessage = dialogMessage;
	}

	public void setDialogMessage(int dialogMessageResId) {
		setDialogMessage(getContext().getString(dialogMessageResId));
	}

	public void setDialogTitle(CharSequence dialogTitle) {
		mDialogTitle = dialogTitle;
	}

	public void setDialogTitle(int dialogTitleResId) {
		setDialogTitle(getContext().getString(dialogTitleResId));
	}

	public void setNegativeButtonText(CharSequence negativeButtonText) {
		mNegativeButtonText = negativeButtonText;
	}

	public void setNegativeButtonText(int negativeButtonTextResId) {
		setNegativeButtonText(getContext().getString(negativeButtonTextResId));
	}

	public void setPositiveButtonText(CharSequence positiveButtonText) {
		mPositiveButtonText = positiveButtonText;
	}

	public void setPositiveButtonText(int positiveButtonTextResId) {
		setPositiveButtonText(getContext().getString(positiveButtonTextResId));
	}

	protected void showDialog(Bundle state) {
		Context context = getContext();
		mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
		mBuilder = new AlertDialog.Builder(context).setTitle(mDialogTitle)
				.setIcon(mDialogIcon)
				.setPositiveButton(mPositiveButtonText, this)
				.setNegativeButton(mNegativeButtonText, this);
		View contentView = onCreateDialogView();
		if (contentView != null) {
			onBindDialogView(contentView);
			mBuilder.setView(contentView);
		} else {
			mBuilder.setMessage(mDialogMessage);
		}
		onPrepareDialogBuilder(mBuilder);
		getPreferenceManager().registerOnActivityDestroyListener(this);
		final Dialog dialog = mDialog = mBuilder.create();
		if (state != null) {
			dialog.onRestoreInstanceState(state);
		}
		if (needInputMethod()) {
			requestInputMethod(dialog);
		}
		dialog.setOnDismissListener(this);
		dialog.show();
	}
}