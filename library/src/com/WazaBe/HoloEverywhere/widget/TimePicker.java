package com.WazaBe.HoloEverywhere.widget;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.internal.NumberPickerEditText;

public class TimePicker extends FrameLayout {
	public interface OnTimeChangedListener {
		void onTimeChanged(TimePicker view, int hourOfDay, int minute);
	}

	private static class SavedState extends BaseSavedState {
		@SuppressWarnings("unused")
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

		private final int mHour, mMinute;

		private SavedState(Parcel in) {
			super(in);
			mHour = in.readInt();
			mMinute = in.readInt();
		}

		private SavedState(Parcelable superState, int hour, int minute) {
			super(superState);
			mHour = hour;
			mMinute = minute;
		}

		public int getHour() {
			return mHour;
		}

		public int getMinute() {
			return mMinute;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mHour);
			dest.writeInt(mMinute);
		}
	}

	private static final boolean DEFAULT_ENABLED_STATE = true;
	private static final int HOURS_IN_HALF_DAY = 12;
	private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {
		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		}
	};
	private final Button mAmPmButton;
	private final String[] mAmPmStrings;
	private Context mContext;
	private Locale mCurrentLocale;
	private final TextView mDivider;
	private final NumberPicker mHourSpinner, mMinuteSpinner, mAmPmSpinner;
	private final NumberPickerEditText mHourSpinnerInput, mMinuteSpinnerInput,
			mAmPmSpinnerInput;
	private boolean mIs24HourView, mIsAm;
	private boolean mIsEnabled = DEFAULT_ENABLED_STATE;

	private OnTimeChangedListener mOnTimeChangedListener;

	private Calendar mTempCalendar;

	public TimePicker(Context context) {
		this(context, null);
	}

	public TimePicker(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.timePickerStyle);
	}

	public TimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setCurrentLocale(Locale.getDefault());
		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.TimePicker, defStyle, 0);
		int layoutResourceId = attributesArray.getResourceId(
				R.styleable.TimePicker_internalLayout,
				R.layout.time_picker_holo);
		attributesArray.recycle();
		LayoutInflater.inflate(mContext, layoutResourceId, this, true);
		FontLoader.apply(this);
		mHourSpinner = (NumberPicker) findViewById(R.id.hour);
		mHourSpinner
				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker spinner, int oldVal,
							int newVal) {
						updateInputState();
						if (!is24HourView()) {
							if (oldVal == HOURS_IN_HALF_DAY - 1
									&& newVal == HOURS_IN_HALF_DAY
									|| oldVal == HOURS_IN_HALF_DAY
									&& newVal == HOURS_IN_HALF_DAY - 1) {
								mIsAm = !mIsAm;
								updateAmPmControl();
							}
						}
						onTimeChanged();
					}
				});
		mHourSpinnerInput = mHourSpinner.getInputField();
		mHourSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mDivider = (TextView) findViewById(R.id.divider);
		if (mDivider != null) {
			mDivider.setText(R.string.time_picker_separator);
		}
		mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
		mMinuteSpinner.setMinValue(0);
		mMinuteSpinner.setMaxValue(59);
		mMinuteSpinner.setOnLongPressUpdateInterval(100);
		mMinuteSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		mMinuteSpinner
				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker spinner, int oldVal,
							int newVal) {
						updateInputState();
						int minValue = mMinuteSpinner.getMinValue();
						int maxValue = mMinuteSpinner.getMaxValue();
						if (oldVal == maxValue && newVal == minValue) {
							int newHour = mHourSpinner.getValue() + 1;
							if (!is24HourView() && newHour == HOURS_IN_HALF_DAY) {
								mIsAm = !mIsAm;
								updateAmPmControl();
							}
							mHourSpinner.setValue(newHour);
						} else if (oldVal == minValue && newVal == maxValue) {
							int newHour = mHourSpinner.getValue() - 1;
							if (!is24HourView()
									&& newHour == HOURS_IN_HALF_DAY - 1) {
								mIsAm = !mIsAm;
								updateAmPmControl();
							}
							mHourSpinner.setValue(newHour);
						}
						onTimeChanged();
					}
				});
		mMinuteSpinnerInput = mMinuteSpinner.getInputField();
		mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mAmPmStrings = new DateFormatSymbols().getAmPmStrings();
		View amPmView = findViewById(R.id.amPm);
		if (amPmView instanceof Button) {
			mAmPmSpinner = null;
			mAmPmSpinnerInput = null;
			mAmPmButton = (Button) amPmView;
			mAmPmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View button) {
					button.requestFocus();
					mIsAm = !mIsAm;
					updateAmPmControl();
					onTimeChanged();
				}
			});
		} else {
			mAmPmButton = null;
			mAmPmSpinner = (NumberPicker) amPmView;
			mAmPmSpinner.setMinValue(0);
			mAmPmSpinner.setMaxValue(1);
			mAmPmSpinner.setDisplayedValues(mAmPmStrings);
			mAmPmSpinner
					.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
						@Override
						public void onValueChange(NumberPicker picker,
								int oldVal, int newVal) {
							updateInputState();
							picker.requestFocus();
							mIsAm = !mIsAm;
							updateAmPmControl();
							onTimeChanged();
						}
					});
			mAmPmSpinnerInput = mAmPmSpinner.getInputField();
			mAmPmSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		}
		updateHourControl();
		updateAmPmControl();
		setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
		setCurrentHour(mTempCalendar.get(Calendar.HOUR_OF_DAY));
		setCurrentMinute(mTempCalendar.get(Calendar.MINUTE));
		if (!isEnabled()) {
			setEnabled(false);
		}
		setContentDescriptions();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		if (VERSION.SDK_INT >= 14) {
			onPopulateAccessibilityEvent(event);
			return true;
		} else {
			return super.dispatchPopulateAccessibilityEvent(event);
		}
	}

	@Override
	public int getBaseline() {
		return mHourSpinner.getBaseline();
	}

	public Integer getCurrentHour() {
		int currentHour = mHourSpinner.getValue();
		if (is24HourView()) {
			return currentHour;
		} else if (mIsAm) {
			return currentHour % HOURS_IN_HALF_DAY;
		} else {
			return currentHour % HOURS_IN_HALF_DAY + HOURS_IN_HALF_DAY;
		}
	}

	public Integer getCurrentMinute() {
		return mMinuteSpinner.getValue();
	}

	public boolean is24HourView() {
		return mIs24HourView;
	}

	@Override
	public boolean isEnabled() {
		return mIsEnabled;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setCurrentLocale(newConfig.locale);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
		super.onPopulateAccessibilityEvent(event);
		int flags = DateUtils.FORMAT_SHOW_TIME;
		if (mIs24HourView) {
			flags |= DateUtils.FORMAT_24HOUR;
		} else {
			flags |= DateUtils.FORMAT_12HOUR;
		}
		mTempCalendar.set(Calendar.HOUR_OF_DAY, getCurrentHour());
		mTempCalendar.set(Calendar.MINUTE, getCurrentMinute());
		String selectedDateUtterance = DateUtils.formatDateTime(mContext,
				mTempCalendar.getTimeInMillis(), flags);
		event.getText().add(selectedDateUtterance);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		setCurrentHour(ss.getHour());
		setCurrentMinute(ss.getMinute());
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, getCurrentHour(), getCurrentMinute());
	}

	private void onTimeChanged() {
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
		if (mOnTimeChangedListener != null) {
			mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(),
					getCurrentMinute());
		}
	}

	private void setContentDescriptions() {
		String text;
		text = mContext.getString(R.string.time_picker_increment_minute_button);
		mMinuteSpinner.findViewById(R.id.increment).setContentDescription(text);
		text = mContext.getString(R.string.time_picker_decrement_minute_button);
		mMinuteSpinner.findViewById(R.id.decrement).setContentDescription(text);
		text = mContext.getString(R.string.time_picker_increment_hour_button);
		mHourSpinner.findViewById(R.id.increment).setContentDescription(text);
		text = mContext.getString(R.string.time_picker_decrement_hour_button);
		mHourSpinner.findViewById(R.id.decrement).setContentDescription(text);
		if (mAmPmSpinner != null) {
			text = mContext
					.getString(R.string.time_picker_increment_set_pm_button);
			mAmPmSpinner.findViewById(R.id.increment).setContentDescription(
					text);
			text = mContext
					.getString(R.string.time_picker_decrement_set_am_button);
			mAmPmSpinner.findViewById(R.id.decrement).setContentDescription(
					text);
		}
	}

	public void setCurrentHour(Integer currentHour) {
		if (currentHour == null || currentHour == getCurrentHour()) {
			return;
		}
		if (!is24HourView()) {
			if (currentHour >= HOURS_IN_HALF_DAY) {
				mIsAm = false;
				if (currentHour > HOURS_IN_HALF_DAY) {
					currentHour = currentHour - HOURS_IN_HALF_DAY;
				}
			} else {
				mIsAm = true;
				if (currentHour == 0) {
					currentHour = HOURS_IN_HALF_DAY;
				}
			}
			updateAmPmControl();
		}
		mHourSpinner.setValue(currentHour);
		onTimeChanged();
	}

	private void setCurrentLocale(Locale locale) {
		if (locale.equals(mCurrentLocale)) {
			return;
		}
		mCurrentLocale = locale;
		mTempCalendar = Calendar.getInstance(locale);
	}

	public void setCurrentMinute(Integer currentMinute) {
		if (currentMinute == getCurrentMinute()) {
			return;
		}
		mMinuteSpinner.setValue(currentMinute);
		onTimeChanged();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (mIsEnabled == enabled) {
			return;
		}
		super.setEnabled(enabled);
		mMinuteSpinner.setEnabled(enabled);
		if (mDivider != null) {
			mDivider.setEnabled(enabled);
		}
		mHourSpinner.setEnabled(enabled);
		if (mAmPmSpinner != null) {
			mAmPmSpinner.setEnabled(enabled);
		} else {
			mAmPmButton.setEnabled(enabled);
		}
		mIsEnabled = enabled;
	}

	public void setIs24HourView(Boolean is24HourView) {
		if (mIs24HourView == is24HourView) {
			return;
		}
		mIs24HourView = is24HourView;
		int currentHour = getCurrentHour();
		updateHourControl();
		setCurrentHour(currentHour);
		updateAmPmControl();
	}

	public void setOnTimeChangedListener(
			OnTimeChangedListener onTimeChangedListener) {
		mOnTimeChangedListener = onTimeChangedListener;
	}

	private void updateAmPmControl() {
		if (is24HourView()) {
			if (mAmPmSpinner != null) {
				mAmPmSpinner.setVisibility(View.GONE);
			} else {
				mAmPmButton.setVisibility(View.GONE);
			}
		} else {
			int index = mIsAm ? Calendar.AM : Calendar.PM;
			if (mAmPmSpinner != null) {
				mAmPmSpinner.setValue(index);
				mAmPmSpinner.setVisibility(View.VISIBLE);
			} else {
				mAmPmButton.setText(mAmPmStrings[index]);
				mAmPmButton.setVisibility(View.VISIBLE);
			}
		}
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
	}

	private void updateHourControl() {
		if (is24HourView()) {
			mHourSpinner.setMinValue(0);
			mHourSpinner.setMaxValue(23);
			mHourSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		} else {
			mHourSpinner.setMinValue(1);
			mHourSpinner.setMaxValue(12);
			mHourSpinner.setFormatter(null);
		}
	}

	private void updateInputState() {
		InputMethodManager inputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			if (inputMethodManager.isActive(mHourSpinnerInput)) {
				mHourSpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			} else if (inputMethodManager.isActive(mMinuteSpinnerInput)) {
				mMinuteSpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			} else if (inputMethodManager.isActive(mAmPmSpinnerInput)) {
				mAmPmSpinnerInput.clearFocus();
				inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		}
	}
}