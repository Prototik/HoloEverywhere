
package org.holoeverywhere.preference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.SeekBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class SeekBarDialogPreference extends DialogPreference {
    private static class SavedState extends BaseSavedState {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        protected int mValue, mMaxValue;

        public SavedState(Parcel source) {
            super(source);
            mValue = source.readInt();
            mMaxValue = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mValue);
            dest.writeInt(mMaxValue);
        }
    }

    private final SeekBar mSeekBar;
    private int mValue = Integer.MIN_VALUE, mMaxValue = Integer.MIN_VALUE;

    public SeekBarDialogPreference(Context context) {
        this(context, null);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarDialogPreferenceStyle);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBarDialogPreference, defStyle,
                R.style.Holo_PreferenceDialog_SeekBarDialogPreference);
        int maxValue = a.getInt(R.styleable.SeekBarDialogPreference_max, 100);
        a.recycle();
        mSeekBar = onCreateSeekBar();
        setMaxValue(maxValue);
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        synchronized (mSeekBar) {
            ViewParent oldParent = mSeekBar.getParent();
            if (oldParent != view) {
                if (oldParent != null) {
                    ((ViewGroup) oldParent).removeView(mSeekBar);
                }
                ((ViewGroup) view).addView(mSeekBar);
            }
        }
    }

    protected SeekBar onCreateSeekBar() {
        return (SeekBar) LayoutInflater.inflate(getContext(),
                R.layout.preference_dialog_seekbar_widget);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        final int value;
        synchronized (mSeekBar) {
            value = mSeekBar.getProgress();
        }
        if (positiveResult && callChangeListener(value)) {
            setValue(value);
        }
    }

    @Override
    protected Integer onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setValue(ss.mValue);
        setMaxValue(ss.mMaxValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.mValue = mValue;
        myState.mMaxValue = mMaxValue;
        return myState;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int def = defaultValue instanceof Integer ? (Integer) defaultValue
                : defaultValue == null ? 0 : Integer.parseInt(defaultValue
                        .toString());
        setValue(restoreValue ? getPersistedInt(def) : def);
    }

    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mMaxValue = maxValue;
        mSeekBar.setMax(maxValue);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }

    public void setValue(int value) {
        if (mValue == value) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mValue = value;
        mSeekBar.setProgress(value);
        persistInt(value);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }
}
