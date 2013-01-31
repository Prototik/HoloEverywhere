
package org.holoeverywhere.preference;

import java.util.Calendar;

import org.holoeverywhere.app.DatePickerDialog;
import org.holoeverywhere.widget.DatePicker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class DatePreference extends DialogPreference {
    public static interface OnDateSetListener {
        public boolean onDateSet(DatePreference preference, long date, int year, int month, int day);
    }

    private final DatePickerDialog.OnDateSetListener mCallback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
            calendar.set(Calendar.YEAR, mYear = year);
            calendar.set(Calendar.MONTH, mMonth = month);
            calendar.set(Calendar.DAY_OF_MONTH, mDay = day);
            DatePreference.this.onDateSet(datePicker, calendar.getTimeInMillis(), year, month, day);
        }
    };

    private long mCurrentTime = Long.MAX_VALUE;

    private OnDateSetListener mOnDateSetListener;

    private int mYear, mMonth, mDay;

    public DatePreference(Context context) {
        this(context, null);
    }

    public DatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.datePreferenceStyle);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DatePreference,
                defStyle, R.style.Holo_PreferenceDate);
        a.recycle();
    }

    public long getCurrentTime() {
        if (mCurrentTime == Long.MAX_VALUE) {
            return System.currentTimeMillis();
        }
        return mCurrentTime;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public OnDateSetListener getOnDateSetListener() {
        return mOnDateSetListener;
    }

    public int getYear() {
        return mYear;
    }

    @Override
    protected Dialog onCreateDialog(Context context) {
        return new DatePickerDialog(context, mCallback, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker datePicker, long date, int year, int month, int day) {
        if (mOnDateSetListener == null
                || mOnDateSetListener.onDateSet(this, date, year, month, day)) {
            persistLong(date);
        }
    }

    @Override
    protected String onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        if (value == null || value.length() == 0) {
            value = String.valueOf(getCurrentTime());
        }
        return value;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            defaultValue = getPersistedLong(getCurrentTime());
        }
        long time;
        try {
            time = Long.parseLong(String.valueOf(defaultValue));
        } catch (Exception e) {
            time = getCurrentTime();
        }
        setTime(time);
    }

    public void setCurrentTime(long time) {
        mCurrentTime = time;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public void setOnDateSetListener(OnDateSetListener onTimeSetListener) {
        mOnDateSetListener = onTimeSetListener;
    }

    private void setTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDay(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setYear(int year) {
        mYear = year;
    }

    protected void updateDialogState() {
        DatePickerDialog dialog = (DatePickerDialog) getDialog();
        if (dialog != null) {
            dialog.updateDate(mYear, mMonth, mDay);
        }
    }
}
