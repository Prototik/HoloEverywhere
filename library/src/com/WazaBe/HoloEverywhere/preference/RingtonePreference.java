package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.internal.RingtonePicker;
import com.WazaBe.HoloEverywhere.internal.RingtonePicker.RingtonePickerListener;

public class RingtonePreference extends Preference implements
		RingtonePickerListener {
	private int mRingtoneType;
	private boolean mShowDefault, mShowSilent;

	public RingtonePreference(Context context) {
		this(context, null);
	}

	public RingtonePreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.ringtonePreferenceStyle);
	}

	public RingtonePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RingtonePreference, defStyle, 0);
		mRingtoneType = a.getInt(R.styleable.RingtonePreference_ringtoneType,
				RingtoneManager.TYPE_RINGTONE);
		mShowDefault = a.getBoolean(R.styleable.RingtonePreference_showDefault,
				true);
		mShowSilent = a.getBoolean(R.styleable.RingtonePreference_showSilent,
				true);
		a.recycle();
	}

	public int getRingtoneType() {
		return mRingtoneType;
	}

	public boolean getShowDefault() {
		return mShowDefault;
	}

	public boolean getShowSilent() {
		return mShowSilent;
	}

	@Override
	protected void onClick() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		onPrepareRingtonePickerIntent(intent);
		new RingtonePicker(getContext(), intent, this).show();
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
		ringtonePickerIntent.putExtra(
				RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
				onRestoreRingtone());
		ringtonePickerIntent.putExtra(
				RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, mShowDefault);
		if (mShowDefault) {
			ringtonePickerIntent.putExtra(
					RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
					RingtoneManager.getDefaultUri(getRingtoneType()));
		}
		ringtonePickerIntent.putExtra(
				RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, mShowSilent);
		ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				mRingtoneType);
		ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				getTitle());
	}

	protected Uri onRestoreRingtone() {
		final String uriString = getPersistedString(null);
		return !TextUtils.isEmpty(uriString) ? Uri.parse(uriString) : null;
	}

	@Override
	public void onRingtonePickerCanceled() {
		if (callChangeListener("")) {
			onSaveRingtone(null);
		}
	}

	@Override
	public void onRingtonePickerChanged(Uri uri) {
		if (callChangeListener(uri != null ? uri.toString() : "")) {
			onSaveRingtone(uri);
		}
	}

	protected void onSaveRingtone(Uri ringtoneUri) {
		persistString(ringtoneUri != null ? ringtoneUri.toString() : "");
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValueObj) {
		String defaultValue = (String) defaultValueObj;
		if (restorePersistedValue) {
			return;
		}
		if (!TextUtils.isEmpty(defaultValue)) {
			onSaveRingtone(Uri.parse(defaultValue));
		}
	}

	public void setRingtoneType(int type) {
		mRingtoneType = type;
	}

	public void setShowDefault(boolean showDefault) {
		mShowDefault = showDefault;
	}

	public void setShowSilent(boolean showSilent) {
		mShowSilent = showSilent;
	}

}