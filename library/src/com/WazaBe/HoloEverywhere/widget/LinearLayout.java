package com.WazaBe.HoloEverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.WazaBe.HoloEverywhere.R;
import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public class LinearLayout extends android.widget.LinearLayout {
	public static final int SHOW_DIVIDER_ALL = 7;
	public static final int SHOW_DIVIDER_BEGINNING = 1;
	public static final int SHOW_DIVIDER_END = 4;
	public static final int SHOW_DIVIDER_MIDDLE = 2;
	public static final int SHOW_DIVIDER_NONE = 0;
	private Drawable mDivider;
	private int mDividerHeight;
	private int mDividerPadding;
	private int mDividerWidth;
	private final AnimatorProxy mProxy;
	private int mShowDividers;

	public LinearLayout(Context context) {
		this(context, null, 0);
	}

	public LinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LinearLayout(Context context, AttributeSet attrs, int defStyleRes) {
		super(context, attrs);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
		init(attrs, defStyleRes);
	}

	void drawDividersHorizontal(Canvas canvas) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child != null && child.getVisibility() != GONE) {
				if (hasDividerBeforeChildAt(i)) {
					final LayoutParams lp = (LayoutParams) child
							.getLayoutParams();
					final int left = child.getLeft() - lp.leftMargin
							- mDividerWidth;
					drawVerticalDivider(canvas, left);
				}
			}
		}
		if (hasDividerBeforeChildAt(count)) {
			final View child = getChildAt(count - 1);
			int right = 0;
			if (child == null) {
				right = getWidth() - getPaddingRight() - mDividerWidth;
			} else {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				right = child.getRight() + lp.rightMargin;
			}
			drawVerticalDivider(canvas, right);
		}
	}

	void drawDividersVertical(Canvas canvas) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);

			if (child != null && child.getVisibility() != GONE) {
				if (hasDividerBeforeChildAt(i)) {
					final LayoutParams lp = (LayoutParams) child
							.getLayoutParams();
					final int top = child.getTop() - lp.topMargin
							- mDividerHeight;
					drawHorizontalDivider(canvas, top);
				}
			}
		}
		if (hasDividerBeforeChildAt(count)) {
			final View child = getChildAt(count - 1);
			int bottom = 0;
			if (child == null) {
				bottom = getHeight() - getPaddingBottom() - mDividerHeight;
			} else {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				bottom = child.getBottom() + lp.bottomMargin;
			}
			drawHorizontalDivider(canvas, bottom);
		}
	}

	void drawHorizontalDivider(Canvas canvas, int top) {
		mDivider.setBounds(getPaddingLeft() + mDividerPadding, top, getWidth()
				- getPaddingRight() - mDividerPadding, top + mDividerHeight);
		mDivider.draw(canvas);
	}

	void drawVerticalDivider(Canvas canvas, int left) {
		mDivider.setBounds(left, getPaddingTop() + mDividerPadding, left
				+ mDividerWidth, getHeight() - getPaddingBottom()
				- mDividerPadding);
		mDivider.draw(canvas);
	}

	@Override
	@SuppressLint("NewApi")
	public float getAlpha() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getAlpha();
		} else {
			return super.getAlpha();
		}
	}

	@Override
	public int getDividerPadding() {
		return mDividerPadding;
	}

	public int getDividerWidth() {
		return mDividerWidth;
	}

	@Override
	public int getShowDividers() {
		return mShowDividers;
	}

	@Override
	@SuppressLint("NewApi")
	public float getTranslationX() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getTranslationX();
		} else {
			return super.getTranslationX();
		}
	}

	@Override
	@SuppressLint("NewApi")
	public float getTranslationY() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getTranslationY();
		} else {
			return super.getTranslationY();
		}
	}

	protected boolean hasDividerBeforeChildAt(int childIndex) {
		if (childIndex == 0) {
			return (mShowDividers & SHOW_DIVIDER_BEGINNING) != 0;
		} else if (childIndex == getChildCount()) {
			return (mShowDividers & SHOW_DIVIDER_END) != 0;
		} else if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
			boolean hasVisibleViewBefore = false;
			for (int i = childIndex - 1; i >= 0; i--) {
				if (getChildAt(i).getVisibility() != GONE) {
					hasVisibleViewBefore = true;
					break;
				}
			}
			return hasVisibleViewBefore;
		}
		return false;
	}

	@SuppressLint("NewApi")
	protected void init(AttributeSet attrs, int defStyleRes) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.LinearLayout, defStyleRes, 0);
		setDividerDrawable(a
				.getDrawable(R.styleable.LinearLayout_android_divider));
		if (a.hasValue(R.styleable.LinearLayout_android_showDividers)) {
			mShowDividers = a.getInt(
					R.styleable.LinearLayout_android_showDividers,
					SHOW_DIVIDER_NONE);
		} else {
			mShowDividers = a.getInt(R.styleable.LinearLayout_showDividers,
					SHOW_DIVIDER_NONE);
		}
		if (a.hasValue(R.styleable.LinearLayout_android_dividerPadding)) {
			mDividerPadding = a.getDimensionPixelSize(
					R.styleable.LinearLayout_dividerPadding, 0);
		} else {
			mDividerPadding = a.getDimensionPixelSize(
					R.styleable.LinearLayout_dividerPadding, 0);
		}
		a.recycle();
	}

	@SuppressLint("NewApi")
	protected boolean isVisibleToUser(Rect boundInView) {
		Rect visibleRect = new Rect();
		getWindowVisibleDisplayFrame(visibleRect);
		boolean isVisible = getWindowVisibility() == View.VISIBLE
				&& getAlpha() > 0 && isShown()
				&& getGlobalVisibleRect(visibleRect);
		if (isVisible && boundInView != null) {
			isVisible &= boundInView.intersect(visibleRect);
		}
		return isVisible;
	}

	@Override
	protected void measureChildWithMargins(View child,
			int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {
		final int index = indexOfChild(child);
		final int orientation = getOrientation();
		final LayoutParams params = (LayoutParams) child.getLayoutParams();
		if (hasDividerBeforeChildAt(index)) {
			if (orientation == VERTICAL) {
				params.topMargin = mDividerHeight;
			} else {
				params.leftMargin = mDividerWidth;
			}
		}
		final int count = getChildCount();
		if (index == count - 1) {
			if (hasDividerBeforeChildAt(count)) {
				if (orientation == VERTICAL) {
					params.bottomMargin = mDividerHeight;
				} else {
					params.rightMargin = mDividerWidth;
				}
			}
		}
		super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
				parentHeightMeasureSpec, heightUsed);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDivider != null) {
			if (getOrientation() == VERTICAL) {
				drawDividersVertical(canvas);
			} else {
				drawDividersHorizontal(canvas);
			}
		}
		super.onDraw(canvas);
	}

	@Override
	@SuppressLint("NewApi")
	public void setAlpha(float alpha) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setAlpha(alpha);
		} else {
			super.setAlpha(alpha);
		}
	}

	@Override
	public void setDividerDrawable(Drawable divider) {
		if (divider == mDivider) {
			return;
		}
		mDivider = divider;
		if (divider != null) {
			mDividerWidth = divider.getIntrinsicWidth();
			mDividerHeight = divider.getIntrinsicHeight();
		} else {
			mDividerWidth = 0;
			mDividerHeight = 0;
		}
		setWillNotDraw(divider == null);
		requestLayout();
	}

	@Override
	public void setDividerPadding(int padding) {
		mDividerPadding = padding;
	}

	@Override
	public void setShowDividers(int showDividers) {
		if (showDividers != mShowDividers) {
			requestLayout();
			invalidate();
		}
		mShowDividers = showDividers;
	}

	@Override
	@SuppressLint("NewApi")
	public void setTranslationX(float translationX) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setTranslationX(translationX);
		} else {
			super.setTranslationX(translationX);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void setTranslationY(float translationY) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setTranslationY(translationY);
		} else {
			super.setTranslationY(translationY);
		}
	}

	@Override
	public void setVisibility(int visibility) {
		if (mProxy != null) {
			if (visibility == GONE) {
				clearAnimation();
			} else if (visibility == VISIBLE) {
				setAnimation(mProxy);
			}
		}
		super.setVisibility(visibility);
	}
}
