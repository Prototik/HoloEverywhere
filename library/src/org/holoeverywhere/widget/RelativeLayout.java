package org.holoeverywhere.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.holoeverywhere.drawable.DrawableCompat;

public class RelativeLayout extends android.widget.RelativeLayout implements DrawableCompat.IStateOverlay {
    private final DrawableCompat.StateOverlay mStateOverlay;

    public RelativeLayout(Context context) {
        this(context, null);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
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
}
