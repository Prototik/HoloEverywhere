
package org.holoeverywhere.preference;

import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.NumberPicker.OnScrollListener;
import org.holoeverywhere.widget.NumberPicker.OnValueChangeListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class NumberPickerPreference extends DialogPreference {
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

        protected int mValue, mMinValue, mMaxValue;
        protected boolean mWrapSelectorWheel;

        public SavedState(Parcel source) {
            super(source);
            mValue = source.readInt();
            mMinValue = source.readInt();
            mMaxValue = source.readInt();
            mWrapSelectorWheel = source.readInt() > 0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mValue);
            dest.writeInt(mMinValue);
            dest.writeInt(mMaxValue);
            dest.writeInt(mWrapSelectorWheel ? 1 : 0);
        }
    }

    private final NumberPicker mNumberPicker;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private int mValue = Integer.MIN_VALUE, mMinValue = Integer.MIN_VALUE,
            mMaxValue = Integer.MIN_VALUE;
    private boolean mWrapSelectorWheel = false;

    public NumberPickerPreference(Context context) {
        this(context, null);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberPickerPreferenceStyle);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference, defStyle,
                R.style.Holo_PreferenceDialog_NumberPickerPreference);
        int minValue = a.getInt(R.styleable.NumberPickerPreference_min, 1);
        int maxValue = a.getInt(R.styleable.NumberPickerPreference_max, 10);
        boolean wrapSelectorWheel = a.getBoolean(
                R.styleable.NumberPickerPreference_wrapSelectorWheel, false);
        a.recycle();
        mNumberPicker = onCreateNumberPicker();
        setMinValue(minValue);
        setMaxValue(maxValue);
        setWrapSelectorWheel(wrapSelectorWheel);
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public int getMinValue() {
        return mMinValue;
    }

    public NumberPicker getNumberPicker() {
        return mNumberPicker;
    }

    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public OnValueChangeListener getOnValueChangeListener() {
        return mOnValueChangeListener;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        synchronized (mNumberPicker) {
            if (mOnValueChangeListener != null) {
                mNumberPicker.setOnValueChangedListener(mOnValueChangeListener);
            }
            if (mOnScrollListener != null) {
                mNumberPicker.setOnScrollListener(mOnScrollListener);
            }
            ViewParent oldParent = mNumberPicker.getParent();
            if (oldParent != view) {
                if (oldParent != null) {
                    ((ViewGroup) oldParent).removeView(mNumberPicker);
                }
                ((ViewGroup) view).addView(mNumberPicker);
            }
        }
    }

    protected NumberPicker onCreateNumberPicker() {
        return new NumberPicker(getDialogContext(true));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        final int value;
        synchronized (mNumberPicker) {
            mNumberPicker.setOnValueChangedListener(null);
            mNumberPicker.setOnScrollListener(null);
            value = mNumberPicker.getValue();
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
        setMinValue(ss.mMinValue);
        setMaxValue(ss.mMaxValue);
        setWrapSelectorWheel(ss.mWrapSelectorWheel);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.mValue = mValue;
        myState.mMinValue = mMinValue;
        myState.mMaxValue = mMaxValue;
        myState.mWrapSelectorWheel = mWrapSelectorWheel;
        return myState;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int def = defaultValue instanceof Integer ? (Integer) defaultValue
                : defaultValue == null ? 0 : Integer.valueOf(defaultValue
                        .toString());
        setValue(restoreValue ? getPersistedInt(def) : def);
    }

    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mMaxValue = maxValue;
        mNumberPicker.setMaxValue(maxValue);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }

    public void setMinValue(int minValue) {
        if (mMinValue == minValue) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mMinValue = minValue;
        mNumberPicker.setMinValue(minValue);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public void setOnValueChangeListener(
            OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    public void setValue(int value) {
        if (mValue == value) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mValue = value;
        mNumberPicker.setValue(value);
        persistInt(value);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        if (mWrapSelectorWheel == wrapSelectorWheel) {
            return;
        }
        final boolean wasBlocking = shouldDisableDependents();
        mWrapSelectorWheel = wrapSelectorWheel;
        mNumberPicker.setWrapSelectorWheel(wrapSelectorWheel);
        if (shouldDisableDependents() != wasBlocking) {
            notifyDependencyChange(!wasBlocking);
        }
    }
}
