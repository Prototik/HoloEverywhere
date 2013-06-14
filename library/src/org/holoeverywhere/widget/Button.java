
package org.holoeverywhere.widget;

import org.holoeverywhere.FontLoader.FontStyleProvider;
import org.holoeverywhere.R;
import org.holoeverywhere.text.AllCapsTransformationMethod;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class Button extends android.widget.Button implements FontStyleProvider {
    private int mFontStyle;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
        TextView.setTextAppearance(this, context,
                a.getResourceId(R.styleable.TextView_android_textAppearance, 0));
        TextView.setTextAppearance(this,
                context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0));
        a.recycle();
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    @Override
    public void setFontStyle(int mFontStyle) {
        this.mFontStyle = mFontStyle;
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
