package com.WazaBe.HoloEverywhere.widget;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.internal.NumberPickerEditText;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorListenerAdapter;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;

public class NumberPicker extends LinearLayout {
	class AdjustScrollerCommand implements Runnable {
		@Override
		public void run() {
			mPreviousScrollerY = 0;
			if (mInitialScrollOffset == mCurrentScrollOffset) {
				updateInputTextView();
				showInputControls(mShowInputControlsAnimimationDuration);
				return;
			}
			int deltaY = mInitialScrollOffset - mCurrentScrollOffset;
			if (Math.abs(deltaY) > mSelectorElementHeight / 2) {
				deltaY += deltaY > 0 ? -mSelectorElementHeight
						: mSelectorElementHeight;
			}
			mAdjustScroller.startScroll(0, 0, 0, deltaY,
					SELECTOR_ADJUSTMENT_DURATION_MILLIS);
			invalidate();
		}
	}

	class ChangeCurrentByOneFromLongPressCommand implements Runnable {
		private boolean mIncrement;

		@Override
		public void run() {
			changeCurrentByOne(mIncrement);
			postDelayed(this, mLongPressUpdateInterval);
		}

		private void setIncrement(boolean increment) {
			mIncrement = increment;
		}
	}

	protected static class DigitFormatter implements Formatter {
		protected String formatS;
		protected Locale locale = Locale.getDefault();

		public DigitFormatter(int digits) {
			formatS = "%0" + digits + "d";
		}

		@Override
		public String format(int value) {
			return String.format(locale, formatS, value);
		}
	}

	public interface Formatter {
		public String format(int value);
	}

	class InputTextFilter extends NumberKeyListener {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			if (mDisplayedValues == null) {
				CharSequence filtered = super.filter(source, start, end, dest,
						dstart, dend);
				if (filtered == null) {
					filtered = source.subSequence(start, end);
				}
				String result = String.valueOf(dest.subSequence(0, dstart))
						+ filtered + dest.subSequence(dend, dest.length());
				if ("".equals(result)) {
					return result;
				}
				int val = getSelectedPos(result);
				if (val > mMaxValue) {
					return "";
				} else {
					return filtered;
				}
			} else {
				CharSequence filtered = String.valueOf(source.subSequence(
						start, end));
				if (TextUtils.isEmpty(filtered)) {
					return "";
				}
				String result = String.valueOf(dest.subSequence(0, dstart))
						+ filtered + dest.subSequence(dend, dest.length());
				String str = String.valueOf(result).toLowerCase();
				for (String val : mDisplayedValues) {
					String valLowerCase = val.toLowerCase();
					if (valLowerCase.startsWith(str)) {
						postSetSelectionCommand(result.length(), val.length());
						return val.subSequence(dstart, val.length());
					}
				}
				return "";
			}
		}

		@Override
		protected char[] getAcceptedChars() {
			return DIGIT_CHARACTERS;
		}

		@Override
		public int getInputType() {
			return InputType.TYPE_CLASS_TEXT;
		}
	}

	public interface OnScrollListener {
		public static int SCROLL_STATE_FLING = 2;
		public static int SCROLL_STATE_IDLE = 0;
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;

		public void onScrollStateChange(NumberPicker view, int scrollState);
	}

	public interface OnValueChangeListener {
		void onValueChange(NumberPicker picker, int oldVal, int newVal);
	}

	class SetSelectionCommand implements Runnable {
		private int mSelectionEnd;

		private int mSelectionStart;

		@Override
		public void run() {
			mInputText.setSelection(mSelectionStart, mSelectionEnd);
		}
	}

	private static final int BUTTON_ALPHA_OPAQUE = 1;
	private static final int BUTTON_ALPHA_TRANSPARENT = 0;
	private static final int CHANGE_CURRENT_BY_ONE_SCROLL_DURATION = 300;
	private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
	private static final char[] DIGIT_CHARACTERS = new char[] { '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9' };
	private static final String PROPERTY_BUTTON_ALPHA = "alpha";
	private static final String PROPERTY_SELECTOR_PAINT_ALPHA = "selectorPaintAlpha";
	private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
	private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
	private static final int SELECTOR_MIDDLE_ITEM_INDEX = 2;
	private static final int SELECTOR_WHEEL_BRIGHT_ALPHA = 255;
	private static final int SELECTOR_WHEEL_DIM_ALPHA = 60;
	private static final int SELECTOR_WHEEL_STATE_LARGE = 2;
	private static final int SELECTOR_WHEEL_STATE_NONE = 0;
	private static final int SELECTOR_WHEEL_STATE_SMALL = 1;
	private static final int SHOW_INPUT_CONTROLS_DELAY_MILLIS = ViewConfiguration
			.getDoubleTapTimeout();
	private static final int SIZE_UNSPECIFIED = -1;
	private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
	public static final Formatter TWO_DIGIT_FORMATTER = new DigitFormatter(2);
	private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
	private final Scroller mAdjustScroller;
	private AdjustScrollerCommand mAdjustScrollerCommand;
	private boolean mAdjustScrollerOnUpEvent;
	private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
	private boolean mCheckBeginEditOnUpEvent;
	private final boolean mComputeMaxWidth;
	private final Animator mDimSelectorWheelAnimator;
	private String[] mDisplayedValues;
	private final boolean mFlingable;
	private final Scroller mFlingScroller;
	private Formatter mFormatter;
	private final ImageButton mIncrementButton, mDecrementButton;
	private final NumberPickerEditText mInputText;
	private float mLastDownEventY, mLastMotionEventY;
	private long mLastUpEventTimeMillis;
	private long mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
	private int mMaxWidth, mSelectorTextGapHeight, mMinValue, mMaxValue,
			mValue, mSelectorElementHeight, mCurrentScrollOffset,
			mPreviousScrollerY, mInitialScrollOffset = Integer.MIN_VALUE;
	private final int mMinHeight, mMaxHeight, mMinWidth, mTextSize;
	private OnScrollListener mOnScrollListener;
	private OnValueChangeListener mOnValueChangeListener;
	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private boolean mScrollWheelAndFadingEdgesInitialized;
	private final Drawable mSelectionDivider;
	private final int mSelectionDividerHeight;
	private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray<String>();
	private final int[] mSelectorIndices = new int[] { Integer.MIN_VALUE,
			Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
			Integer.MIN_VALUE };
	private final Paint mSelectorWheelPaint;
	private int mSelectorWheelState;
	private SetSelectionCommand mSetSelectionCommand;
	private final AnimatorSet mShowInputControlsAnimator;
	private final long mShowInputControlsAnimimationDuration;
	private final int mSolidColor;
	private final Rect mTempRect = new Rect();
	private int mTouchSlop, mMinimumFlingVelocity, mMaximumFlingVelocity;
	private VelocityTracker mVelocityTracker;
	private boolean mWrapSelectorWheel;

	public NumberPicker(Context context) {
		this(context, null);
	}

	public NumberPicker(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.numberPickerStyle);
	}

	public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.NumberPicker, R.attr.numberPickerStyle, 0);
		mSolidColor = attributesArray.getColor(
				R.styleable.NumberPicker_solidColor, 0);
		mFlingable = attributesArray.getBoolean(
				R.styleable.NumberPicker_flingable, true);
		mSelectionDivider = attributesArray
				.getDrawable(R.styleable.NumberPicker_selectionDivider);
		int defSelectionDividerHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT, getResources()
						.getDisplayMetrics());
		mSelectionDividerHeight = attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_selectionDividerHeight,
				defSelectionDividerHeight);
		mMinHeight = attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_android_minHeight, SIZE_UNSPECIFIED);
		mMaxHeight = attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_android_maxHeight, SIZE_UNSPECIFIED);
		if (mMinHeight != SIZE_UNSPECIFIED && mMaxHeight != SIZE_UNSPECIFIED
				&& mMinHeight > mMaxHeight) {
			throw new IllegalArgumentException("minHeight > maxHeight");
		}
		mMinWidth = attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_android_minWidth, SIZE_UNSPECIFIED);
		mMaxWidth = attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_android_maxWidth, SIZE_UNSPECIFIED);
		if (mMinWidth != SIZE_UNSPECIFIED && mMaxWidth != SIZE_UNSPECIFIED
				&& mMinWidth > mMaxWidth) {
			throw new IllegalArgumentException("minWidth > maxWidth");
		}
		setFadingEdgeLength(attributesArray.getDimensionPixelSize(
				R.styleable.NumberPicker_android_fadingEdgeLength, 0));
		attributesArray.recycle();
		mComputeMaxWidth = mMaxWidth == Integer.MAX_VALUE;
		mShowInputControlsAnimimationDuration = getResources().getInteger(
				R.integer.config_longAnimTime);
		setWillNotDraw(false);
		setSelectorWheelState(SELECTOR_WHEEL_STATE_NONE);
		LayoutInflater.inflate(context, R.layout.number_picker, this, true);
		FontLoader.apply(this);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager inputMethodManager = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager != null
						&& inputMethodManager.isActive(mInputText)) {
					inputMethodManager.hideSoftInputFromWindow(
							getWindowToken(), 0);
				}
				mInputText.clearFocus();
				if (v.getId() == R.id.increment) {
					changeCurrentByOne(true);
				} else {
					changeCurrentByOne(false);
				}
			}
		};
		OnLongClickListener onLongClickListener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				mInputText.clearFocus();
				if (v.getId() == R.id.increment) {
					postChangeCurrentByOneFromLongPress(true);
				} else {
					postChangeCurrentByOneFromLongPress(false);
				}
				return true;
			}
		};
		mIncrementButton = (ImageButton) findViewById(R.id.increment);
		mIncrementButton.setOnClickListener(onClickListener);
		mIncrementButton.setOnLongClickListener(onLongClickListener);
		mDecrementButton = (ImageButton) findViewById(R.id.decrement);
		mDecrementButton.setOnClickListener(onClickListener);
		mDecrementButton.setOnLongClickListener(onLongClickListener);
		mInputText = (NumberPickerEditText) findViewById(R.id.numberpicker_input);
		mInputText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mInputText.selectAll();
				} else {
					mInputText.setSelection(0, 0);
					validateInputTextView((NumberPickerEditText) v);
				}
			}
		});
		mInputText.setFilters(new InputFilter[] { new InputTextFilter() });
		mInputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		mInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mTouchSlop = ViewConfiguration.getTapTimeout();
		ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity()
				/ SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
		mTextSize = (int) mInputText.getTextSize();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(mTextSize);
		paint.setTypeface(mInputText.getTypeface());
		ColorStateList colors = mInputText.getTextColors();
		int color = colors.getColorForState(ENABLED_STATE_SET, Color.WHITE);
		paint.setColor(color);
		mSelectorWheelPaint = paint;
		mDimSelectorWheelAnimator = ObjectAnimator.ofInt(this,
				PROPERTY_SELECTOR_PAINT_ALPHA, SELECTOR_WHEEL_BRIGHT_ALPHA,
				SELECTOR_WHEEL_DIM_ALPHA);
		final ObjectAnimator showIncrementButton = ObjectAnimator.ofFloat(
				mIncrementButton, PROPERTY_BUTTON_ALPHA,
				BUTTON_ALPHA_TRANSPARENT, BUTTON_ALPHA_OPAQUE);
		final ObjectAnimator showDecrementButton = ObjectAnimator.ofFloat(
				mDecrementButton, PROPERTY_BUTTON_ALPHA,
				BUTTON_ALPHA_TRANSPARENT, BUTTON_ALPHA_OPAQUE);
		mShowInputControlsAnimator = new AnimatorSet();
		mShowInputControlsAnimator.playTogether(mDimSelectorWheelAnimator,
				showIncrementButton, showDecrementButton);
		mShowInputControlsAnimator.addListener(new AnimatorListenerAdapter() {
			private boolean mCanceled = false;

			@Override
			public void onAnimationCancel(Animator animation) {
				if (mShowInputControlsAnimator.isRunning()) {
					mCanceled = true;
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (!mCanceled) {
					setSelectorWheelState(SELECTOR_WHEEL_STATE_SMALL);
				}
				mCanceled = false;
			}
		});
		mFlingScroller = new Scroller(getContext(), null, true);
		mAdjustScroller = new Scroller(getContext(),
				new DecelerateInterpolator(2.5f));
		updateInputTextView();
		updateIncrementAndDecrementButtonsVisibilityState();
		if (mFlingable) {
			if (isInEditMode()) {
				setSelectorWheelState(SELECTOR_WHEEL_STATE_SMALL);
			} else {
				setSelectorWheelState(SELECTOR_WHEEL_STATE_LARGE);
				hideInputControls();
			}
		}
		setMinimumWidth(mMinWidth);
		setVerticalFadingEdgeEnabled(true);
	}

	private void changeCurrent(int current) {
		if (mValue == current) {
			return;
		}
		if (mWrapSelectorWheel) {
			current = getWrappedSelectorIndex(current);
		}
		int previous = mValue;
		setValue(current);
		notifyChange(previous, current);
	}

	private void changeCurrentByOne(boolean increment) {
		if (mFlingable) {
			mDimSelectorWheelAnimator.cancel();
			mInputText.setVisibility(View.INVISIBLE);
			mSelectorWheelPaint.setAlpha(SELECTOR_WHEEL_BRIGHT_ALPHA);
			mPreviousScrollerY = 0;
			forceCompleteChangeCurrentByOneViaScroll();
			if (increment) {
				mFlingScroller.startScroll(0, 0, 0, -mSelectorElementHeight,
						CHANGE_CURRENT_BY_ONE_SCROLL_DURATION);
			} else {
				mFlingScroller.startScroll(0, 0, 0, mSelectorElementHeight,
						CHANGE_CURRENT_BY_ONE_SCROLL_DURATION);
			}
			invalidate();
		} else {
			if (increment) {
				changeCurrent(mValue + 1);
			} else {
				changeCurrent(mValue - 1);
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mSelectorWheelState == SELECTOR_WHEEL_STATE_NONE) {
			return;
		}
		Scroller scroller = mFlingScroller;
		if (scroller.isFinished()) {
			scroller = mAdjustScroller;
			if (scroller.isFinished()) {
				return;
			}
		}
		scroller.computeScrollOffset();
		int currentScrollerY = scroller.getCurrY();
		if (mPreviousScrollerY == 0) {
			mPreviousScrollerY = scroller.getStartY();
		}
		scrollBy(0, currentScrollerY - mPreviousScrollerY);
		mPreviousScrollerY = currentScrollerY;
		if (scroller.isFinished()) {
			onScrollerFinished(scroller);
		} else {
			invalidate();
		}
	}

	private void decrementSelectorIndices(int[] selectorIndices) {
		for (int i = selectorIndices.length - 1; i > 0; i--) {
			selectorIndices[i] = selectorIndices[i - 1];
		}
		int nextScrollSelectorIndex = selectorIndices[1] - 1;
		if (mWrapSelectorWheel && nextScrollSelectorIndex < mMinValue) {
			nextScrollSelectorIndex = mMaxValue;
		}
		selectorIndices[0] = nextScrollSelectorIndex;
		ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			removeAllCallbacks();
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mSelectorWheelState == SELECTOR_WHEEL_STATE_LARGE) {
				removeAllCallbacks();
				forceCompleteChangeCurrentByOneViaScroll();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			removeAllCallbacks();
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			removeAllCallbacks();
		}
		return super.dispatchTrackballEvent(event);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (mShowInputControlsAnimator.isRunning()
				|| mSelectorWheelState != SELECTOR_WHEEL_STATE_LARGE) {
			long drawTime = getDrawingTime();
			for (int i = 0, count = getChildCount(); i < count; i++) {
				View child = getChildAt(i);
				if (!child.isShown()) {
					continue;
				}
				drawChild(canvas, getChildAt(i), drawTime);
			}
		}
	}

	private void ensureCachedScrollSelectorValue(int selectorIndex) {
		SparseArray<String> cache = mSelectorIndexToStringCache;
		String scrollSelectorValue = cache.get(selectorIndex);
		if (scrollSelectorValue != null) {
			return;
		}
		if (selectorIndex < mMinValue || selectorIndex > mMaxValue) {
			scrollSelectorValue = "";
		} else {
			if (mDisplayedValues != null) {
				int displayedValueIndex = selectorIndex - mMinValue;
				scrollSelectorValue = mDisplayedValues[displayedValueIndex];
			} else {
				scrollSelectorValue = formatNumber(selectorIndex);
			}
		}
		cache.put(selectorIndex, scrollSelectorValue);
	}

	private void fadeSelectorWheel(long animationDuration) {
		mInputText.setVisibility(VISIBLE);
		mDimSelectorWheelAnimator.setDuration(animationDuration);
		mDimSelectorWheelAnimator.start();
	}

	private void fling(int velocityY) {
		mPreviousScrollerY = 0;
		if (velocityY > 0) {
			mFlingScroller
					.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
		} else {
			mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0,
					Integer.MAX_VALUE);
		}
		invalidate();
	}

	private void forceCompleteChangeCurrentByOneViaScroll() {
		Scroller scroller = mFlingScroller;
		if (!scroller.isFinished()) {
			final int yBeforeAbort = scroller.getCurrY();
			scroller.abortAnimation();
			final int yDelta = scroller.getCurrY() - yBeforeAbort;
			scrollBy(0, yDelta);
		}
	}

	private String formatNumber(int value) {
		return mFormatter != null ? mFormatter.format(value) : String
				.valueOf(value);
	}

	@Override
	protected float getBottomFadingEdgeStrength() {
		return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
	}

	public String[] getDisplayedValues() {
		return mDisplayedValues;
	}

	public NumberPickerEditText getInputField() {
		return mInputText;
	}

	public int getMaxValue() {
		return mMaxValue;
	}

	public int getMinValue() {
		return mMinValue;
	}

	private int getSelectedPos(String value) {
		if (mDisplayedValues == null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		} else {
			for (int i = 0; i < mDisplayedValues.length; i++) {
				value = value.toLowerCase();
				if (mDisplayedValues[i].toLowerCase().startsWith(value)) {
					return mMinValue + i;
				}
			}
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		}
		return mMinValue;
	}

	@Override
	public int getSolidColor() {
		return mSolidColor;
	}

	@Override
	protected float getTopFadingEdgeStrength() {
		return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
	}

	public int getValue() {
		return mValue;
	}

	private int getWrappedSelectorIndex(int selectorIndex) {
		if (selectorIndex > mMaxValue) {
			return mMinValue + (selectorIndex - mMaxValue)
					% (mMaxValue - mMinValue) - 1;
		} else if (selectorIndex < mMinValue) {
			return mMaxValue - (mMinValue - selectorIndex)
					% (mMaxValue - mMinValue) + 1;
		}
		return selectorIndex;
	}

	public boolean getWrapSelectorWheel() {
		return mWrapSelectorWheel;
	}

	private void hideInputControls() {
		mShowInputControlsAnimator.cancel();
		mIncrementButton.setVisibility(INVISIBLE);
		mDecrementButton.setVisibility(INVISIBLE);
		mInputText.setVisibility(INVISIBLE);
	}

	private void incrementSelectorIndices(int[] selectorIndices) {
		for (int i = 0; i < selectorIndices.length - 1; i++) {
			selectorIndices[i] = selectorIndices[i + 1];
		}
		int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
		if (mWrapSelectorWheel && nextScrollSelectorIndex > mMaxValue) {
			nextScrollSelectorIndex = mMinValue;
		}
		selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
		ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
	}

	private void initializeFadingEdges() {
		setVerticalFadingEdgeEnabled(true);
		setFadingEdgeLength((getBottom() - getTop() - mTextSize) / 2);
	}

	private void initializeSelectorWheel() {
		initializeSelectorWheelIndices();
		int[] selectorIndices = mSelectorIndices;
		int totalTextHeight = selectorIndices.length * mTextSize;
		float totalTextGapHeight = getBottom() - getTop() - totalTextHeight;
		float textGapCount = selectorIndices.length - 1;
		mSelectorTextGapHeight = (int) (totalTextGapHeight / textGapCount + 0.5f);
		mSelectorElementHeight = mTextSize + mSelectorTextGapHeight;
		int editTextTextPosition = mInputText.getBaseline()
				+ mInputText.getTop();
		mInitialScrollOffset = editTextTextPosition - mSelectorElementHeight
				* SELECTOR_MIDDLE_ITEM_INDEX;
		mCurrentScrollOffset = mInitialScrollOffset;
		updateInputTextView();
	}

	private void initializeSelectorWheelIndices() {
		mSelectorIndexToStringCache.clear();
		int current = getValue();
		for (int i = 0; i < mSelectorIndices.length; i++) {
			int selectorIndex = current + i - SELECTOR_MIDDLE_ITEM_INDEX;
			if (mWrapSelectorWheel) {
				selectorIndex = getWrappedSelectorIndex(selectorIndex);
			}
			mSelectorIndices[i] = selectorIndex;
			ensureCachedScrollSelectorValue(mSelectorIndices[i]);
		}
	}

	private boolean isEventInVisibleViewHitRect(MotionEvent event, View view) {
		if (view.getVisibility() == VISIBLE) {
			view.getHitRect(mTempRect);
			return mTempRect.contains((int) event.getX(), (int) event.getY());
		}
		return false;
	}

	private int makeMeasureSpec(int measureSpec, int maxSize) {
		if (maxSize == SIZE_UNSPECIFIED) {
			return measureSpec;
		}
		final int size = MeasureSpec.getSize(measureSpec);
		final int mode = MeasureSpec.getMode(measureSpec);
		switch (mode) {
		case MeasureSpec.EXACTLY:
			return measureSpec;
		case MeasureSpec.AT_MOST:
			return MeasureSpec.makeMeasureSpec(Math.min(size, maxSize),
					MeasureSpec.EXACTLY);
		case MeasureSpec.UNSPECIFIED:
			return MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.EXACTLY);
		default:
			throw new IllegalArgumentException("Unknown measure mode: " + mode);
		}
	}

	private void notifyChange(int previous, int current) {
		if (mOnValueChangeListener != null) {
			mOnValueChangeListener.onValueChange(this, previous, mValue);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mFlingable && !isInEditMode()) {
			showInputControls(mShowInputControlsAnimimationDuration * 2);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		removeAllCallbacks();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (mSelectorWheelState == SELECTOR_WHEEL_STATE_NONE) {
			return;
		}
		float x = (getRight() - getLeft()) / 2;
		float y = mCurrentScrollOffset;
		final int restoreCount = canvas.save();
		if (mSelectorWheelState == SELECTOR_WHEEL_STATE_SMALL) {
			Rect clipBounds = canvas.getClipBounds();
			clipBounds.inset(0, mSelectorElementHeight);
			canvas.clipRect(clipBounds);
		}
		int[] selectorIndices = mSelectorIndices;
		for (int i = 0; i < selectorIndices.length; i++) {
			int selectorIndex = selectorIndices[i];
			String scrollSelectorValue = mSelectorIndexToStringCache
					.get(selectorIndex);
			if (i != SELECTOR_MIDDLE_ITEM_INDEX
					|| mInputText.getVisibility() != VISIBLE) {
				canvas.drawText(scrollSelectorValue, x, y, mSelectorWheelPaint);
			}
			y += mSelectorElementHeight;
		}
		if (mSelectionDivider != null) {
			int topOfTopDivider = (getHeight() - mSelectorElementHeight - mSelectionDividerHeight) / 2;
			int bottomOfTopDivider = topOfTopDivider + mSelectionDividerHeight;
			mSelectionDivider.setBounds(0, topOfTopDivider, getRight(),
					bottomOfTopDivider);
			mSelectionDivider.draw(canvas);
			int topOfBottomDivider = topOfTopDivider + mSelectorElementHeight;
			int bottomOfBottomDivider = bottomOfTopDivider
					+ mSelectorElementHeight;
			mSelectionDivider.setBounds(0, topOfBottomDivider, getRight(),
					bottomOfBottomDivider);
			mSelectionDivider.draw(canvas);
		}
		canvas.restoreToCount(restoreCount);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (!isEnabled() || !mFlingable) {
			return false;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionEventY = mLastDownEventY = event.getY();
			removeAllCallbacks();
			mShowInputControlsAnimator.cancel();
			mDimSelectorWheelAnimator.cancel();
			mCheckBeginEditOnUpEvent = false;
			mAdjustScrollerOnUpEvent = true;
			if (mSelectorWheelState == SELECTOR_WHEEL_STATE_LARGE) {
				mSelectorWheelPaint.setAlpha(SELECTOR_WHEEL_BRIGHT_ALPHA);
				boolean scrollersFinished = mFlingScroller.isFinished()
						&& mAdjustScroller.isFinished();
				if (!scrollersFinished) {
					mFlingScroller.forceFinished(true);
					mAdjustScroller.forceFinished(true);
					onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
				}
				mCheckBeginEditOnUpEvent = scrollersFinished;
				mAdjustScrollerOnUpEvent = true;
				hideInputControls();
				return true;
			}
			if (isEventInVisibleViewHitRect(event, mIncrementButton)
					|| isEventInVisibleViewHitRect(event, mDecrementButton)) {
				return false;
			}
			mAdjustScrollerOnUpEvent = false;
			setSelectorWheelState(SELECTOR_WHEEL_STATE_LARGE);
			hideInputControls();
			return true;
		case MotionEvent.ACTION_MOVE:
			float currentMoveY = event.getY();
			int deltaDownY = (int) Math.abs(currentMoveY - mLastDownEventY);
			if (deltaDownY > mTouchSlop) {
				mCheckBeginEditOnUpEvent = false;
				onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				setSelectorWheelState(SELECTOR_WHEEL_STATE_LARGE);
				hideInputControls();
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int msrdWdth = getMeasuredWidth();
		final int msrdHght = getMeasuredHeight();
		final int inctBtnMsrdWdth = mIncrementButton.getMeasuredWidth();
		final int incrBtnLeft = (msrdWdth - inctBtnMsrdWdth) / 2;
		final int incrBtnTop = 0;
		final int incrBtnRight = incrBtnLeft + inctBtnMsrdWdth;
		final int incrBtnBottom = incrBtnTop
				+ mIncrementButton.getMeasuredHeight();
		mIncrementButton.layout(incrBtnLeft, incrBtnTop, incrBtnRight,
				incrBtnBottom);
		final int inptTxtMsrdWdth = mInputText.getMeasuredWidth();
		final int inptTxtMsrdHght = mInputText.getMeasuredHeight();
		final int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2;
		final int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
		final int inptTxtRight = inptTxtLeft + inptTxtMsrdWdth;
		final int inptTxtBottom = inptTxtTop + inptTxtMsrdHght;
		mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtRight, inptTxtBottom);
		final int decrBtnMsrdWdth = mIncrementButton.getMeasuredWidth();
		final int decrBtnLeft = (msrdWdth - decrBtnMsrdWdth) / 2;
		final int decrBtnTop = msrdHght - mDecrementButton.getMeasuredHeight();
		final int decrBtnRight = decrBtnLeft + decrBtnMsrdWdth;
		final int decrBtnBottom = msrdHght;
		mDecrementButton.layout(decrBtnLeft, decrBtnTop, decrBtnRight,
				decrBtnBottom);
		if (!mScrollWheelAndFadingEdgesInitialized) {
			mScrollWheelAndFadingEdgesInitialized = true;
			initializeSelectorWheel();
			initializeFadingEdges();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec,
				mMaxWidth);
		final int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec,
				mMaxHeight);
		super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
		final int widthSize = resolveSizeAndStateRespectingMinSize(mMinWidth,
				getMeasuredWidth(), widthMeasureSpec);
		final int heightSize = resolveSizeAndStateRespectingMinSize(mMinHeight,
				getMeasuredHeight(), heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);
	}

	private void onScrollerFinished(Scroller scroller) {
		if (scroller == mFlingScroller) {
			if (mSelectorWheelState == SELECTOR_WHEEL_STATE_LARGE) {
				postAdjustScrollerCommand(0);
				onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			} else {
				updateInputTextView();
				fadeSelectorWheel(mShowInputControlsAnimimationDuration);
			}
		} else {
			updateInputTextView();
			showInputControls(mShowInputControlsAnimimationDuration);
		}
		invalidate();
	}

	private void onScrollStateChange(int scrollState) {
		if (mScrollState == scrollState) {
			return;
		}
		mScrollState = scrollState;
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChange(this, scrollState);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			return false;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			float currentMoveY = ev.getY();
			if (mCheckBeginEditOnUpEvent
					|| mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				int deltaDownY = (int) Math.abs(currentMoveY - mLastDownEventY);
				if (deltaDownY > mTouchSlop) {
					mCheckBeginEditOnUpEvent = false;
					onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				}
			}
			int deltaMoveY = (int) (currentMoveY - mLastMotionEventY);
			scrollBy(0, deltaMoveY);
			invalidate();
			mLastMotionEventY = currentMoveY;
			break;
		case MotionEvent.ACTION_UP:
			if (mCheckBeginEditOnUpEvent) {
				mCheckBeginEditOnUpEvent = false;
				final long deltaTapTimeMillis = ev.getEventTime()
						- mLastUpEventTimeMillis;
				if (deltaTapTimeMillis < ViewConfiguration
						.getDoubleTapTimeout()) {
					setSelectorWheelState(SELECTOR_WHEEL_STATE_SMALL);
					showInputControls(mShowInputControlsAnimimationDuration);
					mInputText.requestFocus();
					InputMethodManager inputMethodManager = (InputMethodManager) getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (inputMethodManager != null) {
						inputMethodManager.showSoftInput(mInputText, 0);
					}
					mLastUpEventTimeMillis = ev.getEventTime();
					return true;
				}
			}
			VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
			int initialVelocity = (int) velocityTracker.getYVelocity();
			if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
				fling(initialVelocity);
				onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			} else {
				if (mAdjustScrollerOnUpEvent) {
					if (mFlingScroller.isFinished()
							&& mAdjustScroller.isFinished()) {
						postAdjustScrollerCommand(0);
					}
				} else {
					postAdjustScrollerCommand(SHOW_INPUT_CONTROLS_DELAY_MILLIS);
				}
			}
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			mLastUpEventTimeMillis = ev.getEventTime();
			break;
		}
		return true;
	}

	private void postAdjustScrollerCommand(int delayMillis) {
		if (mAdjustScrollerCommand == null) {
			mAdjustScrollerCommand = new AdjustScrollerCommand();
		} else {
			removeCallbacks(mAdjustScrollerCommand);
		}
		postDelayed(mAdjustScrollerCommand, delayMillis);
	}

	private void postChangeCurrentByOneFromLongPress(boolean increment) {
		mInputText.clearFocus();
		removeAllCallbacks();
		if (mChangeCurrentByOneFromLongPressCommand == null) {
			mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
		}
		mChangeCurrentByOneFromLongPressCommand.setIncrement(increment);
		post(mChangeCurrentByOneFromLongPressCommand);
	}

	private void postSetSelectionCommand(int selectionStart, int selectionEnd) {
		if (mSetSelectionCommand == null) {
			mSetSelectionCommand = new SetSelectionCommand();
		} else {
			removeCallbacks(mSetSelectionCommand);
		}
		mSetSelectionCommand.mSelectionStart = selectionStart;
		mSetSelectionCommand.mSelectionEnd = selectionEnd;
		post(mSetSelectionCommand);
	}

	private void removeAllCallbacks() {
		if (mChangeCurrentByOneFromLongPressCommand != null) {
			removeCallbacks(mChangeCurrentByOneFromLongPressCommand);
		}
		if (mAdjustScrollerCommand != null) {
			removeCallbacks(mAdjustScrollerCommand);
		}
		if (mSetSelectionCommand != null) {
			removeCallbacks(mSetSelectionCommand);
		}
	}

	private int resolveSizeAndStateRespectingMinSize(int minSize,
			int measuredSize, int measureSpec) {
		if (minSize != SIZE_UNSPECIFIED) {
			final int desiredWidth = Math.max(minSize, measuredSize);
			return resolveSize(desiredWidth, measureSpec);
		} else {
			return measuredSize;
		}
	}

	@Override
	public void scrollBy(int x, int y) {
		if (mSelectorWheelState == SELECTOR_WHEEL_STATE_NONE) {
			return;
		}
		int[] selectorIndices = mSelectorIndices;
		if (!mWrapSelectorWheel && y > 0
				&& selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
			mCurrentScrollOffset = mInitialScrollOffset;
			return;
		}
		if (!mWrapSelectorWheel && y < 0
				&& selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
			mCurrentScrollOffset = mInitialScrollOffset;
			return;
		}
		mCurrentScrollOffset += y;
		while (mCurrentScrollOffset - mInitialScrollOffset > mSelectorTextGapHeight) {
			mCurrentScrollOffset -= mSelectorElementHeight;
			decrementSelectorIndices(selectorIndices);
			changeCurrent(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX]);
			if (!mWrapSelectorWheel
					&& selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
				mCurrentScrollOffset = mInitialScrollOffset;
			}
		}
		while (mCurrentScrollOffset - mInitialScrollOffset < -mSelectorTextGapHeight) {
			mCurrentScrollOffset += mSelectorElementHeight;
			incrementSelectorIndices(selectorIndices);
			changeCurrent(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX]);
			if (!mWrapSelectorWheel
					&& selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
				mCurrentScrollOffset = mInitialScrollOffset;
			}
		}
	}

	@Override
	public void sendAccessibilityEvent(int eventType) {
	}

	public void setDisplayedValues(String[] displayedValues) {
		if (mDisplayedValues == displayedValues) {
			return;
		}
		mDisplayedValues = displayedValues;
		if (mDisplayedValues != null) {
			mInputText.setRawInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		} else {
			mInputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		}
		updateInputTextView();
		initializeSelectorWheelIndices();
		tryComputeMaxWidth();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mIncrementButton.setEnabled(enabled);
		mDecrementButton.setEnabled(enabled);
		mInputText.setEnabled(enabled);
	}

	public void setFormatter(Formatter formatter) {
		if (formatter == mFormatter) {
			return;
		}
		mFormatter = formatter;
		initializeSelectorWheelIndices();
		updateInputTextView();
	}

	public void setMaxValue(int maxValue) {
		if (mMaxValue == maxValue) {
			return;
		}
		if (maxValue < 0) {
			throw new IllegalArgumentException("maxValue must be >= 0");
		}
		mMaxValue = maxValue;
		if (mMaxValue < mValue) {
			mValue = mMaxValue;
		}
		boolean wrapSelectorWheel = mMaxValue - mMinValue > mSelectorIndices.length;
		setWrapSelectorWheel(wrapSelectorWheel);
		initializeSelectorWheelIndices();
		updateInputTextView();
		tryComputeMaxWidth();
	}

	public void setMinValue(int minValue) {
		if (mMinValue == minValue) {
			return;
		}
		if (minValue < 0) {
			throw new IllegalArgumentException("minValue must be >= 0");
		}
		mMinValue = minValue;
		if (mMinValue > mValue) {
			mValue = mMinValue;
		}
		boolean wrapSelectorWheel = mMaxValue - mMinValue > mSelectorIndices.length;
		setWrapSelectorWheel(wrapSelectorWheel);
		initializeSelectorWheelIndices();
		updateInputTextView();
		tryComputeMaxWidth();
	}

	public void setOnLongPressUpdateInterval(long intervalMillis) {
		mLongPressUpdateInterval = intervalMillis;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		mOnScrollListener = onScrollListener;
	}

	public void setOnValueChangedListener(
			OnValueChangeListener onValueChangedListener) {
		mOnValueChangeListener = onValueChangedListener;
	}

	public void setSelectorPaintAlpha(int alpha) {
		mSelectorWheelPaint.setAlpha(alpha);
		invalidate();
	}

	private void setSelectorWheelState(int selectorWheelState) {
		mSelectorWheelState = selectorWheelState;
		if (selectorWheelState == SELECTOR_WHEEL_STATE_LARGE) {
			mSelectorWheelPaint.setAlpha(SELECTOR_WHEEL_BRIGHT_ALPHA);
		}
		AccessibilityManager accessibilityManager = (AccessibilityManager) getContext()
				.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (mFlingable && selectorWheelState == SELECTOR_WHEEL_STATE_LARGE
				&& accessibilityManager.isEnabled()) {
			accessibilityManager.interrupt();
			String text = getContext().getString(
					R.string.number_picker_increment_scroll_action);
			mInputText.setContentDescription(text);
			mInputText
					.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
			mInputText.setContentDescription(null);
		}
	}

	public void setValue(int value) {
		if (mValue == value) {
			return;
		}
		if (value < mMinValue) {
			value = mWrapSelectorWheel ? mMaxValue : mMinValue;
		}
		if (value > mMaxValue) {
			value = mWrapSelectorWheel ? mMinValue : mMaxValue;
		}
		mValue = value;
		initializeSelectorWheelIndices();
		updateInputTextView();
		updateIncrementAndDecrementButtonsVisibilityState();
		invalidate();
	}

	public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
		if (wrapSelectorWheel
				&& mMaxValue - mMinValue < mSelectorIndices.length) {
			throw new IllegalStateException(
					"Range less than selector items count.");
		}
		if (wrapSelectorWheel != mWrapSelectorWheel) {
			mWrapSelectorWheel = wrapSelectorWheel;
			updateIncrementAndDecrementButtonsVisibilityState();
		}
	}

	private void showInputControls(long animationDuration) {
		updateIncrementAndDecrementButtonsVisibilityState();
		mInputText.setVisibility(VISIBLE);
		mShowInputControlsAnimator.setDuration(animationDuration);
		mShowInputControlsAnimator.start();
	}

	private void tryComputeMaxWidth() {
		if (!mComputeMaxWidth) {
			return;
		}
		int maxTextWidth = 0;
		if (mDisplayedValues == null) {
			float maxDigitWidth = 0;
			for (int i = 0; i <= 9; i++) {
				final float digitWidth = mSelectorWheelPaint.measureText(String
						.valueOf(i));
				if (digitWidth > maxDigitWidth) {
					maxDigitWidth = digitWidth;
				}
			}
			int numberOfDigits = 0;
			int current = mMaxValue;
			while (current > 0) {
				numberOfDigits++;
				current = current / 10;
			}
			maxTextWidth = (int) (numberOfDigits * maxDigitWidth);
		} else {
			final int valueCount = mDisplayedValues.length;
			for (int i = 0; i < valueCount; i++) {
				final float textWidth = mSelectorWheelPaint
						.measureText(mDisplayedValues[i]);
				if (textWidth > maxTextWidth) {
					maxTextWidth = (int) textWidth;
				}
			}
		}
		maxTextWidth += mInputText.getPaddingLeft()
				+ mInputText.getPaddingRight();
		if (mMaxWidth != maxTextWidth) {
			if (maxTextWidth > mMinWidth) {
				mMaxWidth = maxTextWidth;
			} else {
				mMaxWidth = mMinWidth;
			}
			invalidate();
		}
	}

	private void updateIncrementAndDecrementButtonsVisibilityState() {
		if (mWrapSelectorWheel || mValue < mMaxValue) {
			mIncrementButton.setVisibility(VISIBLE);
		} else {
			mIncrementButton.setVisibility(INVISIBLE);
		}
		if (mWrapSelectorWheel || mValue > mMinValue) {
			mDecrementButton.setVisibility(VISIBLE);
		} else {
			mDecrementButton.setVisibility(INVISIBLE);
		}
	}

	private void updateInputTextView() {
		if (mDisplayedValues == null) {
			mInputText.setText(formatNumber(mValue));
		} else {
			mInputText.setText(mDisplayedValues[mValue - mMinValue]);
		}
		mInputText.setSelection(mInputText.getText().length());
		if (mFlingable
				&& ((AccessibilityManager) getContext().getSystemService(
						Context.ACCESSIBILITY_SERVICE)).isEnabled()) {
			String text = getContext().getString(
					R.string.number_picker_increment_scroll_mode,
					mInputText.getText());
			mInputText.setContentDescription(text);
		}
	}

	private void validateInputTextView(NumberPickerEditText v) {
		String str = String.valueOf(v.getText());
		if (TextUtils.isEmpty(str)) {
			updateInputTextView();
		} else {
			int current = getSelectedPos(str.toString());
			changeCurrent(current);
		}
	}
}