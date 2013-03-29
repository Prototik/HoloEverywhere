
package org.holoeverywhere.slider;

import org.holoeverywhere.widget.FrameLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DrawerView extends FrameLayout {
    public interface Drawer {
        public void onPostDraw(DrawerView view, Canvas canvas);

        public void onPreDraw(DrawerView view, Canvas canvas);
    }

    private Drawer mDrawer;

    public DrawerView(Context context) {
        this(context, null);
    }

    public DrawerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawer == null) {
            super.draw(canvas);
        } else {
            mDrawer.onPreDraw(this, canvas);
            super.draw(canvas);
            mDrawer.onPostDraw(this, canvas);
            postInvalidate();
        }
    }

    public Drawer getDrawer() {
        return mDrawer;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setDrawer(Drawer drawer) {
        mDrawer = drawer;
        postInvalidate();
    }
}
