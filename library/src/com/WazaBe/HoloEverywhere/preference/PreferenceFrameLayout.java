package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.WazaBe.HoloEverywhere.R;
import com.actionbarsherlock.internal.nineoldandroids.widget.NineFrameLayout;

public class PreferenceFrameLayout extends NineFrameLayout {
	public static class LayoutParams extends NineFrameLayout.LayoutParams {
		public boolean removeBorders = false;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			TypedArray a = c.obtainStyledAttributes(attrs,
					R.styleable.PreferenceFrameLayout_Layout);
			removeBorders = a
					.getBoolean(
							R.styleable.PreferenceFrameLayout_Layout_layout_removeBorders,
							false);
			a.recycle();
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}
	}

	private static final int DEFAULT_BORDER_BOTTOM = 0;
	private static final int DEFAULT_BORDER_LEFT = 0;
	private static final int DEFAULT_BORDER_RIGHT = 0;
	private static final int DEFAULT_BORDER_TOP = 0;
	private final int mBorderBottom;
	private final int mBorderLeft;
	private final int mBorderRight;
	private final int mBorderTop;

	private boolean mPaddingApplied;

	public PreferenceFrameLayout(Context context) {
		this(context, null);
	}

	public PreferenceFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.preferenceFrameLayoutStyle);
	}

	public PreferenceFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PreferenceFrameLayout, defStyle, 0);
		float density = context.getResources().getDisplayMetrics().density;
		int defaultBorderTop = (int) (density * DEFAULT_BORDER_TOP + 0.5f);
		int defaultBottomPadding = (int) (density * DEFAULT_BORDER_BOTTOM + 0.5f);
		int defaultLeftPadding = (int) (density * DEFAULT_BORDER_LEFT + 0.5f);
		int defaultRightPadding = (int) (density * DEFAULT_BORDER_RIGHT + 0.5f);
		mBorderTop = a.getDimensionPixelSize(
				R.styleable.PreferenceFrameLayout_borderTop, defaultBorderTop);
		mBorderBottom = a.getDimensionPixelSize(
				R.styleable.PreferenceFrameLayout_borderBottom,
				defaultBottomPadding);
		mBorderLeft = a.getDimensionPixelSize(
				R.styleable.PreferenceFrameLayout_borderLeft,
				defaultLeftPadding);
		mBorderRight = a.getDimensionPixelSize(
				R.styleable.PreferenceFrameLayout_borderRight,
				defaultRightPadding);
		a.recycle();
	}

	@Override
	public void addView(View child) {
		int borderTop = getPaddingTop();
		int borderBottom = getPaddingBottom();
		int borderLeft = getPaddingLeft();
		int borderRight = getPaddingRight();

		android.view.ViewGroup.LayoutParams params = child.getLayoutParams();
		LayoutParams layoutParams = params instanceof PreferenceFrameLayout.LayoutParams ? (PreferenceFrameLayout.LayoutParams) child
				.getLayoutParams() : null;
		if (layoutParams != null && layoutParams.removeBorders) {
			if (mPaddingApplied) {
				borderTop -= mBorderTop;
				borderBottom -= mBorderBottom;
				borderLeft -= mBorderLeft;
				borderRight -= mBorderRight;
				mPaddingApplied = false;
			}
		} else {
			if (!mPaddingApplied) {
				borderTop += mBorderTop;
				borderBottom += mBorderBottom;
				borderLeft += mBorderLeft;
				borderRight += mBorderRight;
				mPaddingApplied = true;
			}
		}

		int previousTop = getPaddingTop();
		int previousBottom = getPaddingBottom();
		int previousLeft = getPaddingLeft();
		int previousRight = getPaddingRight();
		if (previousTop != borderTop || previousBottom != borderBottom
				|| previousLeft != borderLeft || previousRight != borderRight) {
			setPadding(borderLeft, borderTop, borderRight, borderBottom);
		}

		super.addView(child);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}
}