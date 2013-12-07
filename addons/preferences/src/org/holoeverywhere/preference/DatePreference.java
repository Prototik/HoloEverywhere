
package org.holoeverywhere.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;

import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;

import java.util.Calendar;

public class DatePreference extends DialogPreference {
    private final DatePickerDialog.OnDateSetListener mCallback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
            calendar.set(Calendar.YEAR, mYear = year);
            calendar.set(Calendar.MONTH, mMonth = month);
            calendar.set(Calendar.DAY_OF_MONTH, mDay = day);
            DatePreference.this.onDateSet(datePicker, calendar.getTimeInMillis(), year, month, day);
            updateDialogState();
        }
    };
    private long mDefaultDate;
    private boolean mDefaultDateSetted = false;
    private OnDateSetListener mOnDateSetListener;
    private int mYear, mMonth, mDay;
    private DatePickerDialog mDatePickerDialog;

    public DatePreference(Context context) {
        this(context, null);
    }

    public DatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.datePreferenceStyle);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setForceNotSaveState(true);
        context = getContext();
        //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePreference,
        //        defStyle, R.style.Holo_PreferenceDate);
        //a.recycle();
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
        updateDialogState();
    }

    public long getDefaultDate() {
        if (!mDefaultDateSetted) {
            return System.currentTimeMillis();
        }
        return mDefaultDate;
    }

    public void setDefaultDate(long defaultDate) {
        mDefaultDate = defaultDate;
        mDefaultDateSetted = true;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
        updateDialogState();
    }

    public OnDateSetListener getOnDateSetListener() {
        return mOnDateSetListener;
    }

    public void setOnDateSetListener(OnDateSetListener onTimeSetListener) {
        mOnDateSetListener = onTimeSetListener;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
        updateDialogState();
    }

    @Override
    protected Dialog onCreateDialog(Context context) {
        return getDatePickerDialog(true).getDialog();
    }

    private DatePickerDialog getDatePickerDialog(boolean create) {
        if (mDatePickerDialog == null && create) {
            mDatePickerDialog = DatePickerDialog.newInstance(mCallback, mYear, mMonth, mDay);
            mDatePickerDialog.setForceNotShow(true);
            final FragmentManager fm = Activity.extract(getContext(), true).getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            ft.add(mDatePickerDialog, getClass().getName() + "@" + getKey());
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return mDatePickerDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mDatePickerDialog = null;
        super.onDismiss(dialog);
    }

    public void onDateSet(DatePickerDialog datePicker, long date, int year, int month, int day) {
        if (mOnDateSetListener == null
                || mOnDateSetListener.onDateSet(this, date, year, month, day)) {
            persistLong(date);
        }
    }

    @Override
    protected String onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        if (value == null || value.length() == 0) {
            value = String.valueOf(getDefaultDate());
        }
        return value;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            defaultValue = getPersistedLong(getDefaultDate());
        }
        long time;
        try {
            if (defaultValue instanceof Long) {
                time = ((Long) defaultValue).longValue();
            } else {
                time = Long.parseLong(String.valueOf(defaultValue));
            }
            time = Long.parseLong(String.valueOf(defaultValue));
        } catch (Exception e) {
            time = getDefaultDate();
        }
        setTime(time);
    }

    public void resetDefaultDate() {
        mDefaultDateSetted = false;
    }

    private void setTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateDialogState();
    }

    protected void updateDialogState() {
        final DatePickerDialog dialog = getDatePickerDialog(false);
        if (dialog != null) {
            dialog.setDate(mYear, mMonth, mDay);
        }
    }

    public static interface OnDateSetListener {
        public boolean onDateSet(DatePreference preference, long date, int year, int month, int day);
    }
}
