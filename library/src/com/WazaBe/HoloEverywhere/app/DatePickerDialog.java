package com.WazaBe.HoloEverywhere.app;

import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.DatePicker;
import com.WazaBe.HoloEverywhere.widget.DatePicker.OnDateChangedListener;

public class DatePickerDialog extends AlertDialog implements OnClickListener,
		OnDateChangedListener {
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private final DatePicker mDatePicker;
	private final OnDateSetListener mCallBack;
	private final Calendar mCalendar;
	private boolean mTitleNeedsUpdate = true;

	public interface OnDateSetListener {
		void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth);
	}

	public DatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
	}

	public DatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme);
		mCallBack = callBack;
		mCalendar = Calendar.getInstance();
		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE,
				themeContext.getText(R.string.date_time_done), this);
		setIcon(0);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.date_picker_dialog, null);
		setView(view);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
		updateTitle(year, monthOfYear, dayOfMonth);
	}

	public void onClick(DialogInterface dialog, int which) {
		tryNotifyDateSet();
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mDatePicker.init(year, month, day, this);
		updateTitle(year, month, day);
	}

	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
		}
	}

	@Override
	protected void onStop() {
		tryNotifyDateSet();
		super.onStop();
	}

	private void updateTitle(int year, int month, int day) {
		if (!mDatePicker.getCalendarViewShown()) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, month);
			mCalendar.set(Calendar.DAY_OF_MONTH, day);
			String title = DateUtils.formatDateTime(getContext(),
					mCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_SHOW_YEAR
							| DateUtils.FORMAT_ABBREV_MONTH
							| DateUtils.FORMAT_ABBREV_WEEKDAY);
			setTitle(title);
			mTitleNeedsUpdate = true;
		} else {
			if (mTitleNeedsUpdate) {
				mTitleNeedsUpdate = false;
				setTitle(R.string.date_picker_dialog_title);
			}
		}
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
	}
}