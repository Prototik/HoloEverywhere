package org.holoeverywhere.widget;


import android.content.Context;
import android.util.AttributeSet;

import org.holoeverywhere.drawable.DrawableCompat;

public class ImageButton extends android.widget.ImageButton implements DrawableCompat.IStateOverlay {
    private final DrawableCompat.StateOverlay mStateOverlay;

    public ImageButton(Context context) {
        this(context, null);
    }

    public ImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
    }

    public ImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    public int[] onCreateDrawableState(int extraSpace) {
        return mStateOverlay.onCreateDrawableState(extraSpace);
    }

    @Override
    public int[] superOnCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }
}
