package com.WazaBe.HoloEverywhere.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.internal.NumberPickerEditText;
import com.WazaBe.HoloEverywhere.util.Arrays;
import com.WazaBe.HoloEverywhere.widget.CalendarView.OnDateChangeListener;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.OnValueChangeListener;
import com.actionbarsherlock.internal.nineoldandroids.widget.NineFrameLayout;

public class DatePicker extends NineFrameLayout implements
		OnDateChangeListener, OnValueChangeListener {
	public interface OnDateChangedListener {
		void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth);
	}

	private static class SavedState extends BaseSavedState {
		private final int mDay;
		private final int mMonth;
		private final int mYear;

		private SavedState(Parcel in) {
			super(in);
			mYear = in.readInt();
			mMonth = in.readInt();
			mDay = in.readInt();
		}

		private SavedState(Parcelable superState, int year, int month, int day) {
			super(superState);
			mYear = year;
			mMonth = month;
			mDay = day;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mYear);
			dest.writeInt(mMonth);
			dest.writeInt(mDay);
		}
	}

	private static final String DATE_FORMAT = "MM/dd/yyyy";

	private static final String LOG_TAG = DatePicker.class.getSimpleName();
	private final CalendarView mCalendarView;
	private Locale mCurrentLocale;
	private final java.text.DateFormat mDateFormat = new SimpleDateFormat(
			DATE_FORMAT);
	private final NumberPicker mDaySpinner, mMonthSpinner, mYearSpinner;
	private final NumberPickerEditText mDaySpinnerInput, mMonthSpinnerInput,
			mYearSpinnerInput;
	private boolean mIsEnabled = true;
	private Calendar mMaxDate, mMinDate, mTempDate, mCurrentDate;
	private int mNumberOfMonths;
	private OnDateChangedListener mOnDateChangedListener;
	private String[] mShortMonths;
	private final LinearLayout mSpinners;

	public DatePicker(Context context) {
		this(context, null);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.datePickerStyle);
	}

	@SuppressLint("NewApi")
	public DatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCurrentLocale(Locale.getDefault());
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DatePicker, defStyle, R.style.Holo_DatePicker);
		boolean spinnersShown = a.getBoolean(
				R.styleable.DatePicker_spinnersShown, true);
		boolean calendarViewShown = a.getBoolean(
				R.styleable.DatePicker_calendarViewShown, true);
		int startYear = a.getInt(R.styleable.DatePicker_startYear, 1900);
		int endYear = a.getInt(R.styleable.DatePicker_endYear, 2100);
		String minDate = a.getString(R.styleable.DatePicker_minDate);
		String maxDate = a.getString(R.styleable.DatePicker_maxDate);
		int layoutResourceId = a.getResourceId(
				R.styleable.DatePicker_internalLayout,
				R.layout.date_picker_holo);
		a.recycle();
		getLayoutInflater().inflate(layoutResourceId, this, true);
		mSpinners = (LinearLayout) findViewById(R.id.pickers);
		mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
		mDaySpinner = (NumberPicker) findViewById(R.id.day);
		mMonthSpinner = (NumberPicker) findViewById(R.id.month);
		mYearSpinner = (NumberPicker) findViewById(R.id.year);
		mDaySpinnerInput = mDaySpinner.getInputField();
		mMonthSpinnerInput = mMonthSpinner.getInputField();
		mYearSpinnerInput = mYearSpinner.getInputField();
		mCalendarView.setOnDateChangeListener(this);
		mDaySpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		mDaySpinner.setOnLongPressUpdateInterval(100);
		mDaySpinner.setOnValueChangedListener(this);
		mMonthSpinner.setMinValue(0);
		mMonthSpinner.setMaxValue(mNumberOfMonths - 1);
		mMonthSpinner.setDisplayedValues(mShortMonths);
		mMonthSpinner.setOnLongPressUpdateInterval(200);
		mMonthSpinner.setOnValueChangedListener(this);
		mYearSpinner.setOnLongPressUpdateInterval(100);
		mYearSpinner.setOnValueChangedListener(this);
		if (spinnersShown || calendarViewShown) {
			setSpinnersShown(spinnersShown);
			setCalendarViewShown(calendarViewShown);
		} else {
			setSpinnersShown(true);
		}
		mTempDate.clear();
		if (!TextUtils.isEmpty(minDate)) {
			if (!parseDate(minDate, mTempDate)) {
				mTempDate.set(startYear, 0, 1);
			}
		} else {
			mTempDate.set(startYear, 0, 1);
		}
		setMinDate(mTempDate.getTimeInMillis());
		mTempDate.clear();
		if (!TextUtils.isEmpty(maxDate)) {
			if (!parseDate(maxDate, mTempDate)) {
				mTempDate.set(endYear, 11, 31);
			}
		} else {
			mTempDate.set(endYear, 11, 31);
		}
		setMaxDate(mTempDate.getTimeInMillis());
		mCurrentDate.setTimeInMillis(System.currentTimeMillis());
		init(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
				mCurrentDate.get(Calendar.DAY_OF_MONTH), null);
		reorderSpinners();
		setContentDescriptions();
		if (VERSION.SDK_INT >= 16
				&& getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
			setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		onPopulateAccessibilityEvent(event);
		return true;
	}

	@Override
	protected void dispatchRestoreInstanceState(
			SparseArray<Parcelable> container) {
		dispatchThawSelfOnly(container);
	}

	private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
		if (oldCalendar == null) {
			return Calendar.getInstance(locale);
		} else {
			final long currentTimeMillis = oldCalendar.getTimeInMillis();
			Calendar newCalendar = Calendar.getInstance(locale);
			newCalendar.setTimeInMillis(currentTimeMillis);
			return newCalendar;
		}
	}

	public CalendarView getCalendarView() {
		return mCalendarView;
	}

	public boolean getCalendarViewShown() {
		return mCalendarView.isShown();
	}

	public int getDayOfMonth() {
		return mCurrentDate.get(Calendar.DAY_OF_MONTH);
	}

	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(getContext());
	}

	public long getMaxDate() {
		return mCalendarView.getMaxDate();
	}

	public long getMinDate() {
		return mCalendarView.getMinDate();
	}

	public int getMonth() {
		return mCurrentDate.get(Calendar.MONTH);
	}

	public boolean getSpinnersShown() {
		return mSpinners.isShown();
	}

	public int getYear() {
		return mCurrentDate.get(Calendar.YEAR);
	}

	public void init(int year, int monthOfYear, int dayOfMonth,
			OnDateChangedListener onDateChangedListener) {
		setDate(year, monthOfYear, dayOfMonth);
		updateSpinners();
		updateCalendarView();
		mOnDateChangedListener = onDateChangedListener;
	}

	@Override
	public boolean isEnabled() {
		return mIsEnabled;
	}

	private boolean isNewDate(int year, int month, int dayOfMonth) {
		return mCurrentDate.get(Calendar.YEAR) != year
				|| mCurrentDate.get(Calendar.MONTH) != dayOfMonth
				|| mCurrentDate.get(Calendar.DAY_OF_MONTH) != month;
	}

	private void notifyDateChanged() {
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
		if (mOnDateChangedListener != null) {
			mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(),
					getDayOfMonth());
		}
	}

	@Override
	@SuppressLint("NewApi")
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setCurrentLocale(newConfig.locale);
	}

	@Override
	@SuppressLint("NewApi")
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(DatePicker.class.getName());
	}

	@Override
	@SuppressLint("NewApi")
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(DatePicker.class.getName());
	}

	@Override
	@SuppressLint("NewApi")
	public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
		super.onPopulateAccessibilityEvent(event);
		final int flags = DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_SHOW_YEAR;
		String selectedDateUtterance = DateUtils.formatDateTime(getContext(),
				mCurrentDate.getTimeInMillis(), flags);
		event.getText().add(selectedDateUtterance);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		setDate(ss.mYear, ss.mMonth, ss.mDay);
		updateSpinners();
		updateCalendarView();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, getYear(), getMonth(),
				getDayOfMonth());
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month,
			int monthDay) {
		setDate(year, month, monthDay);
		updateSpinners();
		notifyDateChanged();
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		updateInputState();
		mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
		if (picker == mDaySpinner) {
			int maxDayOfMonth = mTempDate
					.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (oldVal == maxDayOfMonth && newVal == 1) {
				mTempDate.add(Calendar.DAY_OF_MONTH, 1);
			} else if (oldVal == 1 && newVal == maxDayOfMonth) {
				mTempDate.add(Calendar.DAY_OF_MONTH, -1);
			} else {
				mTempDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
			}
		} else if (picker == mMonthSpinner) {
			if (oldVal == 11 && newVal == 0) {
				mTempDate.add(Calendar.MONTH, 1);
			} else if (oldVal == 0 && newVal == 11) {
				mTempDate.add(Calendar.MONTH, -1);
			} else {
				mTempDate.add(Calendar.MONTH, newVal - oldVal);
			}
		} else if (picker == mYearSpinner) {
			mTempDate.set(Calendar.YEAR, newVal);
		} else {
			throw new IllegalArgumentException();
		}
		setDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH),
				mTempDate.get(Calendar.DAY_OF_MONTH));
		updateSpinners();
		updateCalendarView();
		notifyDateChanged();
	}

	private boolean parseDate(String date, Calendar outDate) {
		try {
			outDate.setTime(mDateFormat.parse(date));
			return true;
		} catch (ParseException e) {
			Log.w(LOG_TAG, "Date: " + date + " not in format: " + DATE_FORMAT);
			return false;
		}
	}

	private void reorderSpinners() {
		mSpinners.removeAllViews();
		char[] order = DateFormat.getDateFormatOrder(getContext());
		final int spinnerCount = order.length;
		for (int i = 0; i < spinnerCount; i++) {
			switch (order[i]) {
			case DateFormat.DATE:
				mSpinners.addView(mDaySpinner);
				setImeOptions(mDaySpinner, spinnerCount, i);
				break;
			case DateFormat.MONTH:
				mSpinners.addView(mMonthSpinner);
				setImeOptions(mMonthSpinner, spinnerCount, i);
				break;
			case DateFormat.YEAR:
				mSpinners.addView(mYearSpinner);
				setImeOptions(mYearSpinner, spinnerCount, i);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public void setCalendarViewShown(boolean shown) {
		mCalendarView.setVisibility(shown ? VISIBLE : GONE);
	}

	private void setContentDescriptions() {
		trySetContentDescription(mDaySpinner, R.id.increment,
				R.string.date_picker_increment_day_button);
		trySetContentDescription(mDaySpinner, R.id.decrement,
				R.string.date_picker_decrement_day_button);
		trySetContentDescription(mMonthSpinner, R.id.increment,
				R.string.date_picker_increment_month_button);
		trySetContentDescription(mMonthSpinner, R.id.decrement,
				R.string.date_picker_decrement_month_button);
		trySetContentDescription(mYearSpinner, R.id.increment,
				R.string.date_picker_increment_year_button);
		trySetContentDescription(mYearSpinner, R.id.decrement,
				R.string.date_picker_decrement_year_button);
	}

	private void setCurrentLocale(Locale locale) {
		if (locale.equals(mCurrentLocale)) {
			return;
		}
		mCurrentLocale = locale;
		mTempDate = getCalendarForLocale(mTempDate, locale);
		mMinDate = getCalendarForLocale(mMinDate, locale);
		mMaxDate = getCalendarForLocale(mMaxDate, locale);
		mCurrentDate = getCalendarForLocale(mCurrentDate, locale);
		mNumberOfMonths = mTempDate.getActualMaximum(Calendar.MONTH) + 1;
		mShortMonths = new String[mNumberOfMonths];
		for (int i = 0; i < mNumberOfMonths; i++) {
			mShortMonths[i] = DateUtils.getMonthString(Calendar.JANUARY + i,
					DateUtils.LENGTH_MEDIUM);
		}
	}

	private void setDate(int year, int month, int dayOfMonth) {
		mCurrentDate.set(year, month, dayOfMonth);
		if (mCurrentDate.before(mMinDate)) {
			mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
		} else if (mCurrentDate.after(mMaxDate)) {
			mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (mIsEnabled == enabled) {
			return;
		}
		super.setEnabled(enabled);
		mDaySpinner.setEnabled(enabled);
		mMonthSpinner.setEnabled(enabled);
		mYearSpinner.setEnabled(enabled);
		mCalendarView.setEnabled(enabled);
		mIsEnabled = enabled;
	}

	private void setImeOptions(NumberPicker spinner, int spinnerCount,
			int spinnerIndex) {
		NumberPickerEditText input = (NumberPickerEditText) spinner
				.findViewById(R.id.numberpicker_input);
		input.setImeOptions(spinnerIndex < spinnerCount - 1 ? EditorInfo.IME_ACTION_NEXT
				: EditorInfo.IME_ACTION_DONE);
	}

	public void setMaxDate(long maxDate) {
		mTempDate.setTimeInMillis(maxDate);
		if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
				&& mTempDate.get(Calendar.DAY_OF_YEAR) == mMaxDate
						.get(Calendar.DAY_OF_YEAR)) {
			return;
		}
		mMaxDate.setTimeInMillis(maxDate);
		mCalendarView.setMaxDate(maxDate);
		if (mCurrentDate.after(mMaxDate)) {
			mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
			updateCalendarView();
		}
		updateSpinners();
	}

	public void setMinDate(long minDate) {
		mTempDate.setTimeInMillis(minDate);
		if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
				&& mTempDate.get(Calendar.DAY_OF_YEAR) == mMinDate
						.get(Calendar.DAY_OF_YEAR)) {
			return;
		}
		mMinDate.setTimeInMillis(minDate);
		mCalendarView.setMinDate(minDate);
		if (mCurrentDate.before(mMinDate)) {
			mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
			updateCalendarView();
		}
		updateSpinners();
	}

	public void setSpinnersShown(boolean shown) {
		mSpinners.setVisibility(shown ? VISIBLE : GONE);
	}

	private void trySetContentDescription(View root, int viewId,
			int contDescResId) {
		View target = root.findViewById(viewId);
		if (target != null) {
			target.setContentDescription(getContext().getString(contDescResId));
		}
	}

	private void updateCalendarView() {
		mCalendarView.setDate(mCurrentDate.getTimeInMillis(), false, false);
	}

	public void updateDate(int year, int month, int dayOfMonth) {
		if (!isNewDate(year, month, dayOfMonth)) {
			return;
		}
		setDate(year, month, dayOfMonth);
		updateSpinners();
		updateCalendarView();
		notifyDateChanged();
	}

	private void updateInputState() {

		InputMethodManager inputMethodManager = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			if (inputMethodManager.isActive(mYearSpinnerInput)) {
				mYearSpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			} else if (inputMethodManager.isActive(mMonthSpinnerInput)) {
				mMonthSpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			} else if (inputMethodManager.isActive(mDaySpinnerInput)) {
				mDaySpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		}

	}

	private void updateSpinners() {
		if (mCurrentDate.equals(mMinDate)) {
			mDaySpinner.setMinValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
			mDaySpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(false);
			mMonthSpinner.setMinValue(mCurrentDate.get(Calendar.MONTH));
			mMonthSpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.MONTH));
			mMonthSpinner.setWrapSelectorWheel(false);
		} else if (mCurrentDate.equals(mMaxDate)) {
			mDaySpinner.setMinValue(mCurrentDate
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setMaxValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(false);
			mMonthSpinner.setMinValue(mCurrentDate
					.getActualMinimum(Calendar.MONTH));
			mMonthSpinner.setMaxValue(mCurrentDate.get(Calendar.MONTH));
			mMonthSpinner.setWrapSelectorWheel(false);
		} else {
			mDaySpinner.setMinValue(1);
			mDaySpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(true);
			mMonthSpinner.setMinValue(0);
			mMonthSpinner.setMaxValue(11);
			mMonthSpinner.setWrapSelectorWheel(true);
		}
		String[] displayedValues = Arrays.copyOfRange(mShortMonths,
				mMonthSpinner.getMinValue(), mMonthSpinner.getMaxValue() + 1);
		mMonthSpinner.setDisplayedValues(displayedValues);
		mYearSpinner.setMinValue(mMinDate.get(Calendar.YEAR));
		mYearSpinner.setMaxValue(mMaxDate.get(Calendar.YEAR));
		mYearSpinner.setWrapSelectorWheel(false);
		mYearSpinner.setValue(mCurrentDate.get(Calendar.YEAR));
		mMonthSpinner.setValue(mCurrentDate.get(Calendar.MONTH));
		mDaySpinner.setValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
	}
}