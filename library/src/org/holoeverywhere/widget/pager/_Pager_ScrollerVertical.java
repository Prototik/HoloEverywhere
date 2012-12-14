
package org.holoeverywhere.widget.pager;

import org.holoeverywhere.widget.Pager;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class _Pager_ScrollerVertical extends _Pager_ScrollerHorizontal {
    private static final float PAGE_SCROLL_SLOP = .2f;

    public _Pager_ScrollerVertical(Pager pager) {
        super(pager);
    }

    @Override
    protected boolean checkDrag(float dx, float dy) {
        return dx > dy;
    }

    @Override
    public int checkScrollX(int x) {
        return 0;
    }

    @Override
    public int checkScrollY(int y) {
        return checkScrollBySide(y);
    }

    @Override
    public int getPositionForScroll(int x, int y, boolean floor) {
        float f = (float) checkScrollY(y) / getSide();
        return (int) (floor ? Math.floor(f) : Math.round(f));
    }

    @Override
    public int getScroll() {
        return getPager().getScrollY();
    }

    @Override
    public Point getScrollForPosition(int position) {
        return new Point(0, checkScrollY(getSide() * position));
    }

    @Override
    public int getSide() {
        return getPager().getHeight();
    }

    @Override
    public void layout(View view, int page, int left, int top, int right, int bottom) {
        view.layout(left, top + page * getSide(), right, bottom + page * getSide());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        getPager().scrollBy(0, (int) dy);
        wasScroll = true;
        return true;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        wasScroll = false;
        int scroll = getSide();
        scroll *= iDownPoint.y < e.getY() ? -PAGE_SCROLL_SLOP : PAGE_SCROLL_SLOP;
        lazyScroll(0, scroll);
        getPager().setScrollState(Pager.ScrollState.Idle);
        return true;
    }
}
