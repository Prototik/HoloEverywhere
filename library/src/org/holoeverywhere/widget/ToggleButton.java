
package org.holoeverywhere.widget;

import org.holoeverywhere.FontLoader.FontStyleProvider;

import android.content.Context;
import android.util.AttributeSet;

public class ToggleButton extends android.widget.ToggleButton implements FontStyleProvider {
    private int mFontStyle;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyleToggle);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        TextView.setAllCaps(this, allCaps);
    }

    @Override
    public void setFontStyle(int fontStyle) {
        mFontStyle = fontStyle;
        TextView.setFontStyle(this, fontStyle);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
