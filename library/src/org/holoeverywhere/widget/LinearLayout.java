
package org.holoeverywhere.widget;

import org.holoeverywhere.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

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
    private int mShowDividers;

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs);
        init(attrs, defStyleRes);
    }

    void drawDividersHorizontal(Canvas canvas) {
        final int count = getChildCount();
        final boolean isLayoutRtl = isLayoutRtl();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int position;
                    if (isLayoutRtl) {
                        position = child.getRight() + lp.rightMargin;
                    } else {
                        position = child.getLeft() - lp.leftMargin - mDividerWidth;
                    }
                    drawVerticalDivider(canvas, position);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int position;
            if (child == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    position = getWidth() - getPaddingRight() - mDividerWidth;
                }
            } else {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position = child.getLeft() - lp.leftMargin - mDividerWidth;
                } else {
                    position = child.getRight() + lp.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int top = child.getTop() - lp.topMargin - mDividerHeight;
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
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
        mDivider.draw(canvas);
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

    protected boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (mShowDividers & LinearLayout.SHOW_DIVIDER_BEGINNING) != 0;
        } else if (childIndex == getChildCount()) {
            return (mShowDividers & LinearLayout.SHOW_DIVIDER_END) != 0;
        } else if ((mShowDividers & LinearLayout.SHOW_DIVIDER_MIDDLE) != 0) {
            boolean hasVisibleViewBefore = false;
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != View.GONE) {
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
                    LinearLayout.SHOW_DIVIDER_NONE);
        } else {
            mShowDividers = a.getInt(R.styleable.LinearLayout_showDividers,
                    LinearLayout.SHOW_DIVIDER_NONE);
        }
        if (a.hasValue(R.styleable.LinearLayout_android_dividerPadding)) {
            mDividerPadding = a.getDimensionPixelSize(
                    R.styleable.LinearLayout_android_dividerPadding, 0);
        } else {
            mDividerPadding = a.getDimensionPixelSize(
                    R.styleable.LinearLayout_dividerPadding, 0);
        }
        a.recycle();
    }

    protected boolean isLayoutRtl() {
        return false;
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
            if (orientation == android.widget.LinearLayout.VERTICAL) {
                params.topMargin = mDividerHeight;
            } else {
                params.leftMargin = mDividerWidth;
            }
        }
        final int count = getChildCount();
        if (index == count - 1) {
            if (hasDividerBeforeChildAt(count)) {
                if (orientation == android.widget.LinearLayout.VERTICAL) {
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
        if (mDivider == null) {
            return;
        }
        if (getOrientation() == android.widget.LinearLayout.VERTICAL) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
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
}
