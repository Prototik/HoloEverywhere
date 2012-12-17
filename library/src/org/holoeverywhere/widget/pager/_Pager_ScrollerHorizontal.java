
package org.holoeverywhere.widget.pager;

import org.holoeverywhere.graphics.PointF;
import org.holoeverywhere.widget.Pager;
import org.holoeverywhere.widget.PagerScroller;
import org.holoeverywhere.widget.Scroller;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;

public class _Pager_ScrollerHorizontal extends PagerScroller implements
        OnGestureListener {
    private static enum ScrollState {
        Drag, Idle, UnableToDrag, WaitForSlop;
    }

    private static final float PAGE_SCROLL_SLOP = .3f;
    private MotionEvent downMotionEvent;
    protected final GestureDetector gestureDetector;
    protected PointF iDownPoint = new PointF();
    protected final Scroller scroller;
    private ScrollState scrollState = ScrollState.Idle;
    private final float touchSlop;
    private final ViewConfiguration viewConfiguration;
    protected boolean wasScroll = false;

    public _Pager_ScrollerHorizontal(Pager pager) {
        super(pager);
        final Context context = pager.getContext();
        scroller = new Scroller(context);
        gestureDetector = new GestureDetector(context, this);
        viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    protected boolean checkDrag(float dx, float dy) {
        return dx < dy;
    }

    public int checkScrollBySide(int value) {
        final Adapter adapter = getPager().getAdapter();
        final int side = getSide();
        final int minScroll = 0;
        final int maxScroll = Math.max((adapter == null ? 0 : adapter.getCount() - 1)
                * side, 0);
        value = Math.round((float) value / side) * side;
        value = Math.max(value, minScroll);
        value = Math.min(value, maxScroll);
        return value;
    }

    @Override
    public int checkScrollX(int x) {
        return checkScrollBySide(x);
    }

    @Override
    public int checkScrollY(int y) {
        return 0;
    }

    @Override
    public void computePages(int[] a) {
        final Adapter adapter = getPager().getAdapter();
        final int side = getSide();
        final int scroll = getScroll();
        int startPage = (int) Math.floor((float) scroll / side);
        int endPage = (float) scroll % side == 0 ? startPage : startPage + 1;
        startPage = Math.max(startPage, 0);
        endPage = Math.min(endPage, adapter == null ? 0 : adapter.getCount() - 1);
        a[0] = startPage;
        a[1] = endPage;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            getPager().scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }

    @Override
    public int getPositionForScroll(int x, int y, boolean floor) {
        float f = (float) checkScrollX(x) / getSide();
        return (int) (floor ? Math.floor(f) : Math.round(f));
    }

    public int getScroll() {
        return getPager().getScrollX();
    }

    @Override
    public Point getScrollForPosition(int position) {
        return new Point(checkScrollX(getSide() * position), 0);
    }

    public int getSide() {
        return getPager().getWidth();
    }

    @Override
    public boolean isScrolling() {
        return !scroller.isFinished();
    }

    @Override
    public void layout(View view, int page, int left, int top, int right, int bottom) {
        view.layout(left + page * getSide(), top, right + page * getSide(), bottom);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
        }
        wasScroll = false;
        iDownPoint.set(e);
        getPager().setScrollState(Pager.ScrollState.Dragging);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        return false;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
            switch (scrollState) {
                case Drag:
                    return true;
                case UnableToDrag:
                    return false;
            }
        }
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                scrollState = ScrollState.Idle;
                return false;
            case MotionEvent.ACTION_MOVE:
                final PointF movePoint = new PointF(event);
                if (scrollState == ScrollState.WaitForSlop) {
                    final float distance = movePoint.distance(iDownPoint);
                    if (distance > touchSlop) {
                        final float dx = Math.abs(iDownPoint.x - movePoint.x);
                        final float dy = Math.abs(iDownPoint.y - movePoint.y);
                        if (checkDrag(dx, dy)) {
                            scrollState = ScrollState.UnableToDrag;
                        } else {
                            scrollState = ScrollState.Drag;
                            if (downMotionEvent != null) {
                                gestureDetector.onTouchEvent(downMotionEvent);
                            }
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                scrollState = ScrollState.WaitForSlop;
                scroller.computeScrollOffset();
                iDownPoint.set(event);
                downMotionEvent = MotionEvent.obtain(event);
                break;
        }
        return false;
    }

    @Override
    public void onItemSelected(int selected) {
        if (!wasScroll) {
            smoothScrollTo(selected);
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        getPager().scrollBy((int) dx, 0);
        wasScroll = true;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean handled = false;
        handled |= gestureDetector.onTouchEvent(e);
        switch (MotionEventCompat.getActionMasked(e)) {
            case MotionEvent.ACTION_UP:
                handled |= onUp(e);
                break;
        }
        return handled;
    }

    public boolean onUp(MotionEvent e) {
        wasScroll = false;
        int scroll = getSide();
        scroll *= iDownPoint.x < e.getX() ? -PAGE_SCROLL_SLOP : PAGE_SCROLL_SLOP;
        lazyScroll(scroll, 0);
        getPager().setScrollState(Pager.ScrollState.Idle);
        return true;
    }

    @Override
    public void smoothScrollTo(int x, int y, int duration) {
        scroller.startScroll(getPager().getScrollX(), getPager().getScrollY(), x
                - getPager().getScrollX(), y - getPager().getScrollY(),
                duration);
        super.smoothScrollTo(x, y, duration);
    }
}
