package org.holoeverywhere.issues.i625;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import org.holoeverywhere.drawable.DrawableCompat;
import org.holoeverywhere.widget.FrameLayout;

public class FrameLayoutStub extends FrameLayout implements DrawableCompat.StateStub {
    private boolean mActivated;

    public FrameLayoutStub(Context context) {
        this(context, null);
    }

    public FrameLayoutStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameLayoutStub(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isActivated() {
        return mActivated;
    }

    @Override
    public void setActivated(boolean activated) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setActivated(activated);
        }
        if (mActivated == activated) {
            return;
        }
        mActivated = activated;
        invalidate();
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        return DrawableCompat.onCreateDrawableState(this, super.onCreateDrawableState(DrawableCompat.obtainExtraSpace(extraSpace)));
    }
}
