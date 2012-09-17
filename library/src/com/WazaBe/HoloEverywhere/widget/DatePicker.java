package com.WazaBe.HoloEverywhere.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.widget.EditText;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.util.Arrays;
import com.WazaBe.HoloEverywhere.widget.NumberPicker.OnValueChangeListener;
import com.actionbarsherlock.internal.nineoldandroids.widget.NineFrameLayout;

public class DatePicker extends NineFrameLayout {
	private static final String LOG_TAG = DatePicker.class.getSimpleName();
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	private static final int DEFAULT_START_YEAR = 1900;
	private static final int DEFAULT_END_YEAR = 2100;
	private static final boolean DEFAULT_CALENDAR_VIEW_SHOWN = true;
	private static final boolean DEFAULT_SPINNERS_SHOWN = true;
	private static final boolean DEFAULT_ENABLED_STATE = true;
	private final LinearLayout mSpinners;
	private final NumberPicker mDaySpinner;
	private final NumberPicker mMonthSpinner;
	private final NumberPicker mYearSpinner;
	private final EditText mDaySpinnerInput;
	private final EditText mMonthSpinnerInput;
	private final EditText mYearSpinnerInput;
	private final CalendarView mCalendarView;
	private Locale mCurrentLocale;
	private OnDateChangedListener mOnDateChangedListener;
	private String[] mShortMonths;
	private final java.text.DateFormat mDateFormat = new SimpleDateFormat(
			DATE_FORMAT);
	private int mNumberOfMonths;
	private Calendar mTempDate;
	private Calendar mMinDate;
	private Calendar mMaxDate;
	private Calendar mCurrentDate;
	private boolean mIsEnabled = DEFAULT_ENABLED_STATE;

	public interface OnDateChangedListener {
		void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth);
	}

	public DatePicker(Context context) {
		this(context, null);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.datePickerStyle);
	}

	public DatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCurrentLocale(Locale.getDefault());
		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.DatePicker, defStyle, 0);
		boolean spinnersShown = attributesArray.getBoolean(
				R.styleable.DatePicker_spinnersShown, DEFAULT_SPINNERS_SHOWN);
		boolean calendarViewShown = attributesArray.getBoolean(
				R.styleable.DatePicker_calendarViewShown,
				DEFAULT_CALENDAR_VIEW_SHOWN);
		int startYear = attributesArray.getInt(
				R.styleable.DatePicker_startYear, DEFAULT_START_YEAR);
		int endYear = attributesArray.getInt(R.styleable.DatePicker_endYear,
				DEFAULT_END_YEAR);
		String minDate = attributesArray
				.getString(R.styleable.DatePicker_minDate);
		String maxDate = attributesArray
				.getString(R.styleable.DatePicker_maxDate);
		int layoutResourceId = attributesArray.getResourceId(
				R.styleable.DatePicker_internalLayout,
				R.layout.date_picker_holo);
		attributesArray.recycle();
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(layoutResourceId, this, true);
		OnValueChangeListener onChangeListener = new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
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
				setDate(mTempDate.get(Calendar.YEAR),
						mTempDate.get(Calendar.MONTH),
						mTempDate.get(Calendar.DAY_OF_MONTH));
				updateSpinners();
				updateCalendarView();
				notifyDateChanged();
			}
		};

		mSpinners = (LinearLayout) findViewById(R.id.pickers);
		mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
		mCalendarView
				.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
					public void onSelectedDayChange(CalendarView view,
							int year, int month, int monthDay) {
						setDate(year, month, monthDay);
						updateSpinners();
						notifyDateChanged();
					}
				});
		mDaySpinner = (NumberPicker) findViewById(R.id.day);
		mDaySpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		mDaySpinner.setOnLongPressUpdateInterval(100);
		mDaySpinner.setOnValueChangedListener(onChangeListener);
		mDaySpinnerInput = (EditText) mDaySpinner
				.findViewById(R.id.numberpicker_input);
		mMonthSpinner = (NumberPicker) findViewById(R.id.month);
		mMonthSpinner.setMinValue(0);
		mMonthSpinner.setMaxValue(mNumberOfMonths - 1);
		mMonthSpinner.setDisplayedValues(mShortMonths);
		mMonthSpinner.setOnLongPressUpdateInterval(200);
		mMonthSpinner.setOnValueChangedListener(onChangeListener);
		mMonthSpinnerInput = (EditText) mMonthSpinner
				.findViewById(R.id.numberpicker_input);
		mYearSpinner = (NumberPicker) findViewById(R.id.year);
		mYearSpinner.setOnLongPressUpdateInterval(100);
		mYearSpinner.setOnValueChangedListener(onChangeListener);
		mYearSpinnerInput = (EditText) mYearSpinner
				.findViewById(R.id.numberpicker_input);
		if (!spinnersShown && !calendarViewShown) {
			setSpinnersShown(true);
		} else {
			setSpinnersShown(spinnersShown);
			setCalendarViewShown(calendarViewShown);
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
		if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
			setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
		}
	}

	public long getMinDate() {
		return mCalendarView.getMinDate();
	}

	public void setMinDate(long minDate) {
		mTempDate.setTimeInMillis(minDate);
		if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
				&& mTempDate.get(Calendar.DAY_OF_YEAR) != mMinDate
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

	public long getMaxDate() {
		return mCalendarView.getMaxDate();
	}

	public void setMaxDate(long maxDate) {
		mTempDate.setTimeInMillis(maxDate);
		if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
				&& mTempDate.get(Calendar.DAY_OF_YEAR) != mMaxDate
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

	@Override
	public boolean isEnabled() {
		return mIsEnabled;
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		onPopulateAccessibilityEvent(event);
		return true;
	}

	@Override
	public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
		super.onPopulateAccessibilityEvent(event);

		final int flags = DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_SHOW_YEAR;
		String selectedDateUtterance = DateUtils.formatDateTime(getContext(),
				mCurrentDate.getTimeInMillis(), flags);
		event.getText().add(selectedDateUtterance);
	}

	@Override
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
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setCurrentLocale(newConfig.locale);
	}

	public boolean getCalendarViewShown() {
		return mCalendarView.isShown();
	}

	public CalendarView getCalendarView() {
		return mCalendarView;
	}

	public void setCalendarViewShown(boolean shown) {
		mCalendarView.setVisibility(shown ? VISIBLE : GONE);
	}

	public boolean getSpinnersShown() {
		return mSpinners.isShown();
	}

	public void setSpinnersShown(boolean shown) {
		mSpinners.setVisibility(shown ? VISIBLE : GONE);
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

	public void updateDate(int year, int month, int dayOfMonth) {
		if (!isNewDate(year, month, dayOfMonth)) {
			return;
		}
		setDate(year, month, dayOfMonth);
		updateSpinners();
		updateCalendarView();
		notifyDateChanged();
	}

	@Override
	protected void dispatchRestoreInstanceState(
			SparseArray<Parcelable> container) {
		dispatchThawSelfOnly(container);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, getYear(), getMonth(),
				getDayOfMonth());
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		setDate(ss.mYear, ss.mMonth, ss.mDay);
		updateSpinners();
		updateCalendarView();
	}

	public void init(int year, int monthOfYear, int dayOfMonth,
			OnDateChangedListener onDateChangedListener) {
		setDate(year, monthOfYear, dayOfMonth);
		updateSpinners();
		updateCalendarView();
		mOnDateChangedListener = onDateChangedListener;
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

	private boolean isNewDate(int year, int month, int dayOfMonth) {
		return (mCurrentDate.get(Calendar.YEAR) != year
				|| mCurrentDate.get(Calendar.MONTH) != dayOfMonth || mCurrentDate
					.get(Calendar.DAY_OF_MONTH) != month);
	}

	private void setDate(int year, int month, int dayOfMonth) {
		mCurrentDate.set(year, month, dayOfMonth);
		if (mCurrentDate.before(mMinDate)) {
			mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
		} else if (mCurrentDate.after(mMaxDate)) {
			mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
		}
	}

	private void updateSpinners() {
		if (mCurrentDate.equals(mMinDate)) {
			mDaySpinner.setMinValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
			mDaySpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(false);
			mMonthSpinner.setDisplayedValues(null);
			mMonthSpinner.setMinValue(mCurrentDate.get(Calendar.MONTH));
			mMonthSpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.MONTH));
			mMonthSpinner.setWrapSelectorWheel(false);
		} else if (mCurrentDate.equals(mMaxDate)) {
			mDaySpinner.setMinValue(mCurrentDate
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setMaxValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(false);
			mMonthSpinner.setDisplayedValues(null);
			mMonthSpinner.setMinValue(mCurrentDate
					.getActualMinimum(Calendar.MONTH));
			mMonthSpinner.setMaxValue(mCurrentDate.get(Calendar.MONTH));
			mMonthSpinner.setWrapSelectorWheel(false);
		} else {
			mDaySpinner.setMinValue(1);
			mDaySpinner.setMaxValue(mCurrentDate
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			mDaySpinner.setWrapSelectorWheel(true);
			mMonthSpinner.setDisplayedValues(null);
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

	private void updateCalendarView() {
		mCalendarView.setDate(mCurrentDate.getTimeInMillis(), false, false);
	}

	public int getYear() {
		return mCurrentDate.get(Calendar.YEAR);
	}

	public int getMonth() {
		return mCurrentDate.get(Calendar.MONTH);
	}

	public int getDayOfMonth() {
		return mCurrentDate.get(Calendar.DAY_OF_MONTH);
	}

	private void notifyDateChanged() {
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
		if (mOnDateChangedListener != null) {
			mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(),
					getDayOfMonth());
		}
	}

	private void setImeOptions(NumberPicker spinner, int spinnerCount,
			int spinnerIndex) {
		final int imeOptions;
		if (spinnerIndex < spinnerCount - 1) {
			imeOptions = EditorInfo.IME_ACTION_NEXT;
		} else {
			imeOptions = EditorInfo.IME_ACTION_DONE;
		}
		TextView input = (TextView) spinner
				.findViewById(R.id.numberpicker_input);
		input.setImeOptions(imeOptions);
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

	private void trySetContentDescription(View root, int viewId,
			int contDescResId) {
		View target = root.findViewById(viewId);
		if (target != null) {
			target.setContentDescription(getContext().getString(contDescResId));
		}
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

	private static class SavedState extends BaseSavedState {
		private final int mYear;
		private final int mMonth;
		private final int mDay;

		private SavedState(Parcelable superState, int year, int month, int day) {
			super(superState);
			mYear = year;
			mMonth = month;
			mDay = day;
		}

		private SavedState(Parcel in) {
			super(in);
			mYear = in.readInt();
			mMonth = in.readInt();
			mDay = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mYear);
			dest.writeInt(mMonth);
			dest.writeInt(mDay);
		}
	}
}