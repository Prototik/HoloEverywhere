
package org.holoeverywhere.widget;

import org.holoeverywhere.widget.pager._Pager_OnPageScrollListenerWrapper;
import org.holoeverywhere.widget.pager._Pager_Recycler;
import org.holoeverywhere.widget.pager._Pager_ScrollerHorizontal;
import org.holoeverywhere.widget.pager._Pager_ScrollerVertical;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class Pager extends AdapterView<ListAdapter> {
    public static interface OnAdapterChangeListener {
        public void onAdapterChanged(ListAdapter oldAdapter, ListAdapter newAdapter);
    }

    public static interface OnPageScrollListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageScrollStateChanged(ScrollState state);

        public void onPageSelected(int position);
    }

    public static interface PagerAdapter {
        public int getCachePageCount();

        public void onRecycleView(View child);

        public void onSelectionChanged(int selection);
    }

    private static final class SavedState extends BaseSavedState {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private int selection;

        public SavedState(Parcel parcel) {
            super(parcel);
            selection = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(selection);
        }
    }

    public static enum ScrollerType {
        Horizontal, Vertical;
    }

    public enum ScrollState {
        Dragging, Idle
    }

    private static final PagerAdapter DEFAULT_ADAPTER = new PagerAdapter() {
        @Override
        public int getCachePageCount() {
            return 0;
        }

        @Override
        public void onRecycleView(View child) {

        }

        @Override
        public void onSelectionChanged(int selection) {

        }
    };

    private ListAdapter mAdapter;
    private final PagerDataSetObserver mDataSetObserver;
    private int mLastPosition;
    private ScrollState mLastScrollState = ScrollState.Idle;
    private int[] mLastScrollVisiblePositions;
    private OnAdapterChangeListener mOnAdapterChangeListener;
    private OnPageScrollListener mOnPageScrollListener;
    private PagerScroller mPagerScroller;
    private final _Pager_Recycler mRecycler;
    private int mWidthMeasureSpec, mHeightMeasureSpec;

    public Pager(Context context) {
        this(context, null);
    }

    public Pager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Pager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRecycler = new _Pager_Recycler(this);
        mDataSetObserver = new PagerDataSetObserver();
        mPagerScroller = new _Pager_ScrollerHorizontal(this);
    }

    public Iterable<View> cached() {
        return mRecycler.cached();
    }

    public final class PagerDataSetObserver extends AdapterDataSetObserver {
        public Iterable<View> cached() {
            return Pager.this.cached();
        }

        public void post(Runnable runnable) {
            Pager.this.post(runnable);
        }

        public Iterable<View> visible() {
            return Pager.this.visible();
        }
    }

    @Override
    void selectionChanged() {
        mPagerScroller.onItemSelected(mSelectedPosition);
        getPagerAdapter().onSelectionChanged(mSelectedPosition);
        if (mOnItemSelectedListener != null) {
            if (mSelectedPosition >= 0) {
                mOnItemSelectedListener.onItemSelected(this, getSelectedView(), mSelectedPosition,
                        getItemIdAtPosition(mSelectedPosition));
            } else {
                mOnItemSelectedListener.onNothingSelected(this);
            }
        }
    }

    @Override
    public void computeScroll() {
        mPagerScroller.computeScroll();
        super.computeScroll();
        postInvalidate();
    }

    public void fakeLayout() {
        layout(false, 0, 0, 0, 0, false);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public int getLastVisiblePosition() {
        return mLastPosition;
    }

    public OnAdapterChangeListener getOnAdapterChangeListener() {
        return mOnAdapterChangeListener;
    }

    public OnPageScrollListener getOnPageScrollListener() {
        return mOnPageScrollListener;
    }

    protected View getOrCreateView(int position) {
        View view = mRecycler.get(position);
        if (view == null && mAdapter != null && position >= 0 && position < mAdapter.getCount()) {
            view = mAdapter.getView(position, view, this);
            if (view != null) {
                mRecycler.put(position, view);
            }
        }
        return view;
    }

    public PagerAdapter getPagerAdapter() {
        return mAdapter != null && mAdapter instanceof PagerAdapter ? (PagerAdapter) mAdapter
                : DEFAULT_ADAPTER;
    }

    public PagerScroller getPagerScroller() {
        return mPagerScroller;
    }

    @Override
    public View getSelectedView() {
        return getOrCreateView(mSelectedPosition);
    }

    public View getViewAt(int position) {
        return mRecycler.get(position);
    }

    private void layout(boolean changed, int left, int top, int right, int bottom,
            boolean realLayout) {
        if (mAdapter == null) {
            mRecycler.fullClear();
            removeAllViewsInLayout();
            setSelectedPositionInt(INVALID_POSITION);
            checkSelectionChanged();
            return;
        }

        if (mDataChanged) {
            mDataChanged = false;
            handleDataChanged();
        }

        checkSelectionChanged();

        mPagerScroller.computePages(mVisiblePositions);
        mFirstPosition = mVisiblePositions[0];
        mLastPosition = mVisiblePositions[1];
        synchronized (mRecycler) {
            mRecycler.prepareForClear();
            final int cachePageCount = Math.max(0, getPagerAdapter().getCachePageCount());
            final int selected = mSelectedPosition;
            for (int i = selected - cachePageCount; i <= selected + cachePageCount; i++) {
                View view = getOrCreateView(i);
                if (view != null) {
                    mRecycler.markAsSave(i);
                    if (realLayout) {
                        view.setSelected(selected == i);
                    }
                }
            }
            for (int page = mFirstPosition; page <= mLastPosition; page++) {
                View view = getOrCreateView(page);
                if (view == null) {
                    continue;
                }
                mRecycler.markAsSave(page);
                if (realLayout) {
                    setupView(view);
                    view.setSelected(selected == page);
                    mPagerScroller.layout(view, page, left, top, right, bottom);
                    view.invalidate();
                }
            }
            mRecycler.clear();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mPagerScroller.onInterceptTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layout(changed, left, top, right, bottom, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.EXACTLY);
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec),
                MeasureSpec.EXACTLY);
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable s) {
        SavedState state = (SavedState) s;
        super.onRestoreInstanceState(state.getSuperState());
        setSelectedPositionInt(state.selection);
        checkSelectionChanged();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.selection = mSelectedPosition;
        return state;
    }

    private final int[] mVisiblePositions = new int[2];

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mLastScrollVisiblePositions == null) {
            final int page1 = mPagerScroller.getPositionForScroll(oldl, oldt, false);
            final int page2 = mPagerScroller.getPositionForScroll(l, t, false);
            mLastScrollVisiblePositions = new int[2];
            mLastScrollVisiblePositions[0] = Math.min(page1, page2);
            mLastScrollVisiblePositions[1] = Math.max(page1, page2);
        } else {
            mPagerScroller.computePages(mVisiblePositions);
            if (mLastScrollVisiblePositions[0] != mVisiblePositions[0]
                    || mLastScrollVisiblePositions[1] != mVisiblePositions[1]) {
                mLastScrollVisiblePositions[0] = mVisiblePositions[0];
                mLastScrollVisiblePositions[1] = mVisiblePositions[1];
                requestLayout();
            }
        }
        final int needPosition = mPagerScroller.getPositionForScroll(l, t, true);
        if (mSelectedPosition != needPosition) {
            setSelectedPositionInt(needPosition);
            checkSelectionChanged();
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(getScrollX() + x, getScrollY() + y);
    }

    @Override
    public void scrollTo(int x, int y) {
        final int oldX = getScrollX(), oldY = getScrollY();
        if (oldX != x || oldY != y) {
            super.scrollTo(x, y);
            onScrollChanged(x, y, oldX, oldY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPagerScroller.onTouchEvent(event);
    }

    @Override
    public void removeDetachedView(View child, boolean animate) {
        getPagerAdapter().onRecycleView(child);
        super.removeDetachedView(child, animate);
    }

    public void scrollTo(int position) {
        mPagerScroller.scrollTo(position);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter == adapter) {
            return;
        }
        final ListAdapter mOldAdapter = mAdapter;
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        if (mAdapter != null) {
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            checkFocus();
            final int position = mItemCount > 0 ? 0 : INVALID_POSITION;
            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
        } else {
            checkFocus();
            checkSelectionChanged();
        }
        if (mOnAdapterChangeListener != null) {
            mOnAdapterChangeListener.onAdapterChanged(mOldAdapter, mAdapter);
        }
    }

    public void setOnAdapterChangeListener(OnAdapterChangeListener onAdapterChangeListener) {
        mOnAdapterChangeListener = onAdapterChangeListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        setOnPageScrollListener(new _Pager_OnPageScrollListenerWrapper(onPageChangeListener));
    }

    public void setOnPageScrollListener(OnPageScrollListener onPageScrollListener) {
        mOnPageScrollListener = onPageScrollListener;
    }

    public void setPagerScroller(PagerScroller pagerScroller) {
        if (pagerScroller == null) {
            throw new IllegalArgumentException("PagerScroller cannot be null");
        }
        int selection = getSelectedItemPosition();
        mPagerScroller = pagerScroller;
        setSelection(selection);
    }

    public void setPagerScroller(ScrollerType type) {
        PagerScroller scroller = null;
        switch (type) {
            case Horizontal:
                scroller = new _Pager_ScrollerHorizontal(this);
                break;
            case Vertical:
                scroller = new _Pager_ScrollerVertical(this);
                break;
        }
        setPagerScroller(scroller);
    }

    public void setScrollState(ScrollState state) {
        if (state == mLastScrollState) {
            return;
        }
        mLastScrollState = state;
        if (mOnPageScrollListener != null) {
            mOnPageScrollListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void setSelection(int selection) {
        setSelection(selection, true);
    }

    public void setSelection(int selection, boolean animate) {
        if (animate) {
            smoothScrollTo(selection);
        } else {
            scrollTo(selection);
            setSelectedPositionInt(selection);
            checkSelectionChanged();
            requestLayout();
        }
    }

    protected void setupView(View v) {
        if (v.getParent() != this) {
            if (v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeViewInLayout(v);
            }
            addViewInLayout(v, getChildCount(), generateDefaultLayoutParams());
        }
        v.measure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    public void smoothScrollTo(int position) {
        mPagerScroller.smoothScrollTo(position);
    }

    public void smoothScrollTo(int x, int y) {
        mPagerScroller.smoothScrollTo(x, y);
    }

    public void smoothScrollTo(int x, int y, int duration) {
        mPagerScroller.smoothScrollTo(x, y, duration);
    }

    public Iterable<View> visible() {
        return mRecycler.visible();
    }
}
