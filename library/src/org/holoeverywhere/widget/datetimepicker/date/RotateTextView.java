package org.holoeverywhere.widget.datetimepicker.date;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;

import org.holoeverywhere.widget.TextView;

public class RotateTextView extends TextView {
    private final boolean mTopDown;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final int gravity = getGravity();
        if (Gravity.isVertical(gravity) && (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
            mTopDown = false;
        } else {
            mTopDown = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int count = canvas.save();
        if (mTopDown) {
            canvas.translate(getWidth(), 0f);
            canvas.rotate(90f);
        } else {
            canvas.translate(0f, getHeight());
            canvas.rotate(-90f);
        }
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        getPaint().setColor(getCurrentTextColor());
        final Layout layout = getLayout();
        if (layout != null) {
            layout.draw(canvas);
        }
        canvas.restoreToCount(count);
    }
}
