package org.holoeverywhere.slider;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

class SliderMenuGroupIndicatorView extends ImageView {
    public SliderMenuGroupIndicatorView(Context context) {
        super(context);
    }

    public SliderMenuGroupIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SliderMenuGroupIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean mExpanded = false;

    public void setExpanded(boolean expanded) {
        if (mExpanded != expanded) {
            mExpanded = expanded;
            refreshDrawableState();
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        return ViewCompat.mergeDrawableStates(
                super.onCreateDrawableState(extraSpace + 1),
                new int[]{mExpanded ? android.R.attr.state_expanded : -android.R.attr.state_expanded});
    }
}
