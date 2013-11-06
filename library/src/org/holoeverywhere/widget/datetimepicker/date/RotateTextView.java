package org.holoeverywhere.widget.datetimepicker.date;

import android.content.Context;
import android.util.AttributeSet;

import com.nineoldandroids.view.animation.AnimatorProxy;

import org.holoeverywhere.widget.TextView;

public class RotateTextView extends TextView {
    private final AnimatorProxy mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public RotateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setRotation(270);
    }

    @Override
    public float getRotation() {
        if (mProxy != null) {
            return mProxy.getRotation();
        } else {
            return super.getRotation();
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (mProxy != null) {
            mProxy.setRotation(rotation);
        } else {
            super.setRotation(rotation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
    }
}
