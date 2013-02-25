
package org.holoeverywhere.preference;

import org.holoeverywhere.preference._RingtonePickerDialog.RingtonePickerListener;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

public class RingtonePreference extends DialogPreference implements
        RingtonePickerListener {
    private Uri mLastUri;
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
        context = getContext();
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
    protected Dialog onCreateDialog(Context context) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        onPrepareRingtonePickerIntent(intent);
        return new _RingtonePickerDialog(getContext(), intent, this).makeDialog();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        String uri = mLastUri == null ? "" : mLastUri.toString();
        if (positiveResult && callChangeListener(uri)) {
            persistString(uri);
        }
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
        onDialogClosed(false);
    }

    @Override
    public void onRingtonePickerChanged(Uri uri) {
        mLastUri = uri;
        onDialogClosed(true);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
            Object defaultValueObj) {
        String defaultValue = (String) defaultValueObj;
        if (restorePersistedValue) {
            defaultValue = getPersistedString(defaultValue);
        }
        if (!TextUtils.isEmpty(defaultValue)) {
            mLastUri = Uri.parse(defaultValue);
            onDialogClosed(true);
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
