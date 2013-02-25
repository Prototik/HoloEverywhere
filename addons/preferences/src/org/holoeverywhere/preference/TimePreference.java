
package org.holoeverywhere.preference;

import java.util.Calendar;

import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.widget.TimePicker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;

public class TimePreference extends DialogPreference {
    public static interface OnTimeSetListener {
        public boolean onTimeSet(TimePreference preference, long date, int hour, int minute);
    }

    private boolean m24HourView;

    private final TimePickerDialog.OnTimeSetListener mCallback = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
            calendar.set(Calendar.HOUR_OF_DAY, mHour = hour);
            calendar.set(Calendar.MINUTE, mMinute = minute);
            TimePreference.this.onTimeSet(timePicker, calendar.getTimeInMillis(), hour, minute);
            updateDialogState();
        }
    };

    private long mDefaultTime;
    private boolean mDefaultTimeSetted = false;
    private int mHour, mMinute;
    private OnTimeSetListener mOnTimeSetListener;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timePreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimePreference, defStyle,
                R.style.Holo_PreferenceTime);
        switch (a.getInt(R.styleable.TimePreference_is24HourView, 0)) {
            case 0:
            default:
                // Auto
                m24HourView = DateFormat.is24HourFormat(context);
                break;
            case 1:
                // Yes
                m24HourView = true;
                break;
            case -1:
                // No
                m24HourView = false;
                break;
        }
        a.recycle();
    }

    public long getDefaultTime() {
        if (!mDefaultTimeSetted) {
            return System.currentTimeMillis();
        }
        return mDefaultTime;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public OnTimeSetListener getOnTimeSetListener() {
        return mOnTimeSetListener;
    }

    protected boolean is24HourView() {
        return m24HourView;
    }

    @Override
    protected Dialog onCreateDialog(Context context) {
        return new TimePickerDialog(context, mCallback, mHour, mMinute, m24HourView);
    }

    @Override
    protected String onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        if (value == null || value.length() == 0) {
            value = String.valueOf(getDefaultTime());
        }
        return value;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            defaultValue = getPersistedLong(getDefaultTime());
        }
        long time;
        try {
            if (defaultValue instanceof Long) {
                time = ((Long) defaultValue).longValue();
            } else {
                time = Long.parseLong(String.valueOf(defaultValue));
            }
        } catch (Exception e) {
            time = getDefaultTime();
        }
        setTime(time);
    }

    public void onTimeSet(TimePicker timePicker, long time, int hour, int minute) {
        if (mOnTimeSetListener == null || mOnTimeSetListener.onTimeSet(this, time, hour, minute)) {
            persistLong(time);
        }
    }

    public void resetDefaultTime() {
        mDefaultTimeSetted = false;
    }

    public void setDefaultTime(long defaultTime) {
        mDefaultTime = defaultTime;
        mDefaultTimeSetted = true;
    }

    public void setHour(int hour) {
        mHour = hour;
        updateDialogState();
    }

    public void setIs24HourView(boolean is24HourView) {
        m24HourView = is24HourView;
    }

    public void setMinute(int minute) {
        mMinute = minute;
        updateDialogState();
    }

    public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener) {
        mOnTimeSetListener = onTimeSetListener;
    }

    private void setTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        setHour(calendar.get(Calendar.HOUR_OF_DAY));
        setMinute(calendar.get(Calendar.MINUTE));
    }

    protected void updateDialogState() {
        TimePickerDialog dialog = (TimePickerDialog) getDialog();
        if (dialog != null) {
            dialog.updateTime(mHour, mMinute);
        }
    }
}
