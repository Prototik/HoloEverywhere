
package org.holoeverywhere.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPager extends android.support.v4.view.ViewPager {
    private boolean mSwipeEnabled = true;

    public ViewPager(Context context) {
        this(context, null);
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mSwipeEnabled) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mSwipeEnabled) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        if (mSwipeEnabled == swipeEnabled) {
            return;
        }
        mSwipeEnabled = swipeEnabled;
        if (!swipeEnabled) {
            MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
            super.onTouchEvent(event);
            event.recycle();
        }
    }
}
