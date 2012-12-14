
package org.holoeverywhere.widget.pager;

import org.holoeverywhere.widget.Pager.OnPageScrollListener;
import org.holoeverywhere.widget.Pager.ScrollState;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public final class _Pager_OnPageScrollListenerWrapper implements OnPageScrollListener {
    private OnPageChangeListener wrapped;

    public _Pager_OnPageScrollListenerWrapper(OnPageChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (wrapped != null) {
            wrapped.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(ScrollState state) {
        if (wrapped != null) {
            switch (state) {
                case Idle:
                    wrapped.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
                    break;
                case Dragging:
                    wrapped.onPageScrollStateChanged(ViewPager.SCROLL_STATE_DRAGGING);
                    break;
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (wrapped != null) {
            wrapped.onPageSelected(position);
        }
    }
}
