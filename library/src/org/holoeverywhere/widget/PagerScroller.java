
package org.holoeverywhere.widget;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public abstract class PagerScroller {
    private Pager pager;

    public PagerScroller(Pager pager) {
        this.pager = pager;
    }

    public int checkScrollX(int x) {
        return x;
    }

    public int checkScrollY(int y) {
        return y;
    }

    public void computePages(int[] a) {

    }

    public void computeScroll() {
    }

    public Pager getPager() {
        return pager;
    }

    public int getPositionForScroll(int x, int y, boolean floor) {
        return 0;
    }

    public Point getScrollForPosition(int position) {
        return null;
    }

    public void layout(View view, int page, int left, int top, int right, int bottom) {

    }

    public void lazyScroll(int x, int y) {
        final int currentX = getPager().getScrollX(), currentY = getPager().getScrollY();
        int scrollX = checkScrollX(x + currentX);
        int scrollY = checkScrollY(y + currentY);
        if (scrollX != currentX || scrollY != currentY) {
            smoothScrollTo(scrollX, scrollY, 400);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    public void onItemSelected(int selected) {

    }

    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    public void scrollTo(int position) {
        final Point point = getScrollForPosition(position);
        getPager().scrollTo(point.x, point.y);
    }

    public void smoothScrollTo(int position) {
        final Point point = getScrollForPosition(position);
        smoothScrollTo(point.x, point.y);
    }

    public void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 400);
    }

    public void smoothScrollTo(int x, int y, int duration) {
        getPager().invalidate();
    }
}
