
package org.holoeverywhere.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.holoeverywhere.FontLoader.FontStyleProvider;
import org.holoeverywhere.drawable.DrawableCompat;

public class Button extends android.widget.Button implements FontStyleProvider, DrawableCompat.IStateOverlay {
    private final DrawableCompat.StateOverlay mStateOverlay;
    private String mFontFamily;
    private int mFontStyle;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }


    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
        mStateOverlay = new DrawableCompat.StateOverlay(this, context, attrs, defStyle);
    }

    @Override
    public boolean isActivated() {
        return mStateOverlay.isActivated();
    }

    @Override
    public void setActivated(boolean activated) {
        mStateOverlay.setActivated(activated);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if (mStateOverlay == null) {
            return super.onCreateDrawableState(extraSpace);
        }
        return mStateOverlay.onCreateDrawableState(extraSpace);
    }

    @Override
    public int[] superOnCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    public String getFontFamily() {
        return mFontFamily;
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
    public void setFontStyle(String fontFamily, int fontStyle) {
        mFontFamily = fontFamily;
        mFontStyle = fontStyle;
        TextView.setFontStyle(this, fontFamily, fontStyle);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
