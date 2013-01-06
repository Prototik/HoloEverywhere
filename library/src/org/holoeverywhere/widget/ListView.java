
package org.holoeverywhere.widget;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.IHoloActivity.OnWindowFocusChangeListener;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.util.LongSparseArray;
import org.holoeverywhere.widget.HeaderViewListAdapter.ViewInfo;
import org.holoeverywhere.widget.ListAdapterWrapper.OnPrepareViewListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListAdapter;

import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder.ContextMenuInfoGetter;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ListView extends android.widget.ListView implements OnWindowFocusChangeListener,
        ContextMenuInfoGetter, OnPrepareViewListener {
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked);
    }

    private final class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mWrapped.onActionItemClicked(mode, item);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (mWrapped.onCreateActionMode(mode, menu)) {
                setLongClickable(false);
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mWrapped.onDestroyActionMode(mode);
            mChoiceActionMode = null;
            clearChoices();
            invalidateViews();
            setLongClickable(true);
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                int position, long id, boolean checked) {
            mWrapped.onItemCheckedStateChanged(mode, position, id, checked);
            if (getCheckedItemCount() == 0) {
                mode.finish();
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mWrapped.onPrepareActionMode(mode, menu);
        }

        public void setWrapped(MultiChoiceModeListener wrapped) {
            mWrapped = wrapped;
        }
    }

    private final class OnItemLongClickListenerWrapper implements OnItemLongClickListener {
        private OnItemLongClickListener wrapped;

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            return performItemLongClick(view, position, id);
        }

        public void setWrapped(OnItemLongClickListener wrapped) {
            this.wrapped = wrapped;
            if (wrapped != null) {
                setLongClickable(true);
            }
        }
    }

    public static final int CHOICE_MODE_MULTIPLE_MODAL = AbsListView.CHOICE_MODE_MULTIPLE_MODAL;
    private static final boolean USE_ACTIVATED = VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    private Activity mActivity;
    private ListAdapterWrapper mAdapter;
    private boolean mAdapterHasStableIds;
    private LongSparseArray<Integer> mCheckedIdStates;
    private int mCheckedItemCount;
    private SparseBooleanArray mCheckStates;
    private ActionMode mChoiceActionMode;
    private int mChoiceMode;
    private ContextMenuInfo mContextMenuInfo;
    private Drawable mDivider;
    private boolean mEnableModalBackgroundWrapper;
    private boolean mFastScrollEnabled;
    private final List<ViewInfo> mFooterViewInfos = new ArrayList<ViewInfo>(),
            mHeaderViewInfos = new ArrayList<ViewInfo>();
    private boolean mForceHeaderListAdapter = false;
    private boolean mIsAttached;
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private MultiChoiceModeWrapper mMultiChoiceModeCallback;
    private final OnItemLongClickListenerWrapper mOnItemLongClickListenerWrapper;
    private OnScrollListener mOnScrollListener;

    public ListView(Context context) {
        this(context, null);
    }

    public ListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    @SuppressLint("NewApi")
    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (context instanceof Activity) {
            setActivity((Activity) context);
        }
        if (Application.config().isDisableOverscrollEffects() && VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        mOnItemLongClickListenerWrapper = new OnItemLongClickListenerWrapper();
        super.setOnItemLongClickListener(mOnItemLongClickListenerWrapper);
        setLongClickable(false);

        mDivider = super.getDivider();
        super.setDivider(null);
        setDivider(mDivider);
    }

    @Override
    public Drawable getDivider() {
        return mDivider;
    }

    private boolean mDividerIsOpaque;
    private int mDividerHeight;

    @Override
    public void setDividerHeight(int height) {
        mDividerHeight = height;
        requestLayout();
        invalidate();
    }

    @Override
    public int getDividerHeight() {
        return mDividerHeight;
    }

    private final Rect mTempRect = new Rect();
    private boolean mHeaderDividersEnabled;

    public boolean isHeaderDividersEnabled() {
        return mHeaderDividersEnabled;
    }

    @Override
    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        if (mHeaderDividersEnabled != headerDividersEnabled) {
            mHeaderDividersEnabled = headerDividersEnabled;
            requestLayout();
            invalidate();
        }
    }

    private boolean mFooterDividersEnabled;

    public boolean isFooterDividersEnabled() {
        return mFooterDividersEnabled;
    }

    @Override
    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        if (mFooterDividersEnabled != footerDividersEnabled) {
            mFooterDividersEnabled = footerDividersEnabled;
            requestLayout();
            invalidate();
        }
    }

    private boolean mClipToPadding;

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(mClipToPadding = clipToPadding);
    }

    private Paint mDividerPaint;

    protected void dispatchDraw(Canvas canvas) {
        final int dividerHeight = mDividerHeight;
        final boolean drawDividers = dividerHeight > 0 && mDivider != null;

        if (drawDividers) {
            final Rect bounds = mTempRect;
            bounds.left = getPaddingLeft();
            bounds.right = getRight() - getLeft() - getPaddingRight();

            final int count = getChildCount();
            final int headerCount = mHeaderViewInfos.size();
            final int itemCount = getCount();
            final int footerLimit = itemCount - mFooterViewInfos.size() - 1;
            final boolean headerDividers = mHeaderDividersEnabled;
            final boolean footerDividers = mFooterDividersEnabled;
            final int first = getFirstVisiblePosition();
            final boolean areAllItemsSelectable = mAdapter.areAllItemsEnabled();
            final ListAdapter adapter = mAdapter;
            final boolean fillForMissingDividers = isOpaque() && !super.isOpaque();
            if (fillForMissingDividers && mDividerPaint == null && mIsCacheColorOpaque) {
                mDividerPaint = new Paint();
                mDividerPaint.setColor(getCacheColorHint());
            }
            final Paint paint = mDividerPaint;

            int effectivePaddingTop = 0;
            int effectivePaddingBottom = 0;
            if (mClipToPadding) {
                effectivePaddingTop = getListPaddingTop();
                effectivePaddingBottom = mListPadding.bottom;
            }

            final int listBottom = mBottom - mTop - effectivePaddingBottom + mScrollY;
            if (!mStackFromBottom) {
                int bottom = 0;

                // Draw top divider or header for overscroll
                final int scrollY = mScrollY;
                if (count > 0 && scrollY < 0) {
                    if (drawOverscrollHeader) {
                        bounds.bottom = 0;
                        bounds.top = scrollY;
                        drawOverscrollHeader(canvas, overscrollHeader, bounds);
                    } else if (drawDividers) {
                        bounds.bottom = 0;
                        bounds.top = -dividerHeight;
                        drawDivider(canvas, bounds, -1);
                    }
                }

                for (int i = 0; i < count; i++) {
                    if ((headerDividers || first + i >= headerCount) &&
                            (footerDividers || first + i < footerLimit)) {
                        View child = getChildAt(i);
                        bottom = child.getBottom();
                        // Don't draw dividers next to items that are not
                        // enabled

                        if (drawDividers &&
                                (bottom < listBottom && !(drawOverscrollFooter && i == count - 1))) {
                            if ((areAllItemsSelectable || (adapter.isEnabled(first + i) && (i == count - 1 || adapter
                                    .isEnabled(first + i + 1))))) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                drawDivider(canvas, bounds, i);
                            } else if (fillForMissingDividers) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                }

                final int overFooterBottom = mBottom + mScrollY;
                if (drawOverscrollFooter && first + count == itemCount &&
                        overFooterBottom > bottom) {
                    bounds.top = bottom;
                    bounds.bottom = overFooterBottom;
                    drawOverscrollFooter(canvas, overscrollFooter, bounds);
                }
            } else {
                int top;

                final int scrollY = mScrollY;

                if (count > 0 && drawOverscrollHeader) {
                    bounds.top = scrollY;
                    bounds.bottom = getChildAt(0).getTop();
                    drawOverscrollHeader(canvas, overscrollHeader, bounds);
                }

                final int start = drawOverscrollHeader ? 1 : 0;
                for (int i = start; i < count; i++) {
                    if ((headerDividers || first + i >= headerCount) &&
                            (footerDividers || first + i < footerLimit)) {
                        View child = getChildAt(i);
                        top = child.getTop();
                        // Don't draw dividers next to items that are not
                        // enabled
                        if (top > effectivePaddingTop) {
                            if ((areAllItemsSelectable || (adapter.isEnabled(first + i) && (i == count - 1 || adapter
                                    .isEnabled(first + i + 1))))) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                // Give the method the child ABOVE the divider,
                                // so we
                                // subtract one from our child
                                // position. Give -1 when there is no child
                                // above the
                                // divider.
                                drawDivider(canvas, bounds, i - 1);
                            } else if (fillForMissingDividers) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                }

                if (count > 0 && scrollY > 0) {
                    if (drawOverscrollFooter) {
                        final int absListBottom = mBottom;
                        bounds.top = absListBottom;
                        bounds.bottom = absListBottom + scrollY;
                        drawOverscrollFooter(canvas, overscrollFooter, bounds);
                    } else if (drawDividers) {
                        bounds.top = listBottom;
                        bounds.bottom = listBottom + dividerHeight;
                        drawDivider(canvas, bounds, -1);
                    }
                }
            }
        }

        // Draw the indicators (these should be drawn above the dividers) and
        // children
        super.dispatchDraw(canvas);
    }

    @Override
    public void setDivider(Drawable divider) {
        if (mDivider != null) {
            mDividerHeight = mDivider.getIntrinsicHeight();
        } else {
            mDividerHeight = 0;
        }
        mDivider = divider;
        mDividerIsOpaque = mDivider == null || mDivider.getOpacity() == PixelFormat.OPAQUE;
        requestLayout();
        invalidate();
    }

    @Override
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    @Override
    public void addFooterView(View v, Object data, boolean isSelectable) {
        if (mAdapter != null && !(mAdapter instanceof HeaderViewListAdapter)) {
            throw new IllegalStateException(
                    "Cannot add footer view to list -- setAdapter has already been called.");
        }
        ViewInfo info = new ViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);
        if (mAdapter != null) {
            invalidateViews();
        }
    }

    @Override
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (mAdapter != null && !(mAdapter instanceof HeaderViewListAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to list -- setAdapter has already been called.");
        }
        ViewInfo info = new ViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        if (mAdapter != null) {
            invalidateViews();
        }
    }

    @Override
    public void clearChoices() {
        if (mCheckStates != null) {
            mCheckStates.clear();
        }
        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }
        mCheckedItemCount = 0;
    }

    protected ContextMenuInfo createContextMenuInfo(View view, int position,
            long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    /**
     * Don't used
     */
    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        final Drawable divider = mDivider;
        divider.setBounds(bounds);
        divider.draw(canvas);
    }

    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public int getCheckedItemCount() {
        return mCheckedItemCount;
    }

    @Override
    public long[] getCheckedItemIds() {
        if (mChoiceMode == CHOICE_MODE_NONE || mCheckedIdStates == null || mAdapter == null) {
            return new long[0];
        }
        final LongSparseArray<Integer> idStates = mCheckedIdStates;
        final int count = idStates.size();
        final long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = idStates.keyAt(i);
        }
        return ids;
    }

    @Override
    public int getCheckedItemPosition() {
        if (mChoiceMode == CHOICE_MODE_SINGLE && mCheckStates != null && mCheckStates.size() == 1) {
            return mCheckStates.keyAt(0);
        }
        return INVALID_POSITION;
    }

    @Override
    public SparseBooleanArray getCheckedItemPositions() {
        if (mChoiceMode != CHOICE_MODE_NONE) {
            return mCheckStates;
        }
        return null;
    }

    @Override
    @Deprecated
    public long[] getCheckItemIds() {
        return getCheckedItemIds();
    }

    @Override
    public int getChoiceMode() {
        return mChoiceMode;
    }

    @Override
    public ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    @Override
    public int getFooterViewsCount() {
        return mFooterViewInfos.size();
    }

    @Override
    public int getHeaderViewsCount() {
        return mHeaderViewInfos.size();
    }

    public boolean isAttached() {
        return mIsAttached;
    }

    @Override
    @ExportedProperty
    public boolean isFastScrollEnabled() {
        return mFastScrollEnabled;
    }

    public boolean isForceHeaderListAdapter() {
        return mForceHeaderListAdapter;
    }

    public boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    @Override
    public boolean isItemChecked(int position) {
        if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
            return mCheckStates.get(position);
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttached = false;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && getSelectedItemPosition() < 0 && !isInTouchMode()) {
            if (!mIsAttached && mAdapter != null) {
                invalidateViews();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (!isEnabled()) {
                    return true;
                }
                if (isClickable() && isPressed() &&
                        getSelectedItemPosition() >= 0 && mAdapter != null &&
                        getSelectedItemPosition() < mAdapter.getCount()) {
                    final View view = getChildAt(getSelectedItemPosition()
                            - getFirstVisiblePosition());
                    if (view != null) {
                        performItemClick(view, getSelectedItemPosition(), getSelectedItemId());
                        view.setPressed(false);
                    }
                    setPressed(false);
                    return true;
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public View onPrepareView(View view, int position) {
        if (mEnableModalBackgroundWrapper && !(view instanceof ModalBackgroundWrapper)) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            ModalBackgroundWrapper wrapper = new ModalBackgroundWrapper(getContext());
            wrapper.addView(view);
            view = wrapper;
        } else if (!mEnableModalBackgroundWrapper && view instanceof ModalBackgroundWrapper) {
            view = ((ModalBackgroundWrapper) view).getChildAt(0);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
        if (mCheckStates != null) {
            setStateOnView(view, mCheckStates.get(position));
        } else {
            setStateOnView(view, false);
        }
        return view;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            invalidate();
            invalidateViews();
        }
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        boolean handled = false;
        boolean dispatchItemClick = true;
        if (mChoiceMode != CHOICE_MODE_NONE) {
            handled = true;
            boolean checkedStateChanged = false;
            if (mChoiceMode == CHOICE_MODE_MULTIPLE ||
                    mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode != null) {
                boolean newValue = !mCheckStates.get(position, false);
                mCheckStates.put(position, newValue);
                if (mCheckedIdStates != null && mAdapter.hasStableIds()) {
                    if (newValue) {
                        mCheckedIdStates.put(mAdapter.getItemId(position), position);
                    } else {
                        mCheckedIdStates.delete(mAdapter.getItemId(position));
                    }
                }
                if (newValue) {
                    mCheckedItemCount++;
                } else {
                    mCheckedItemCount--;
                }
                if (mChoiceActionMode != null) {
                    mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode,
                            position, id, newValue);
                    dispatchItemClick = false;
                }
                checkedStateChanged = true;
            } else if (mChoiceMode == CHOICE_MODE_SINGLE) {
                boolean newValue = !mCheckStates.get(position, false);
                if (newValue) {
                    mCheckStates.clear();
                    mCheckStates.put(position, true);
                    if (mCheckedIdStates != null && mAdapter.hasStableIds()) {
                        mCheckedIdStates.clear();
                        mCheckedIdStates.put(mAdapter.getItemId(position), position);
                    }
                    mCheckedItemCount = 1;
                } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
                    mCheckedItemCount = 0;
                }
                checkedStateChanged = true;
            }
            if (checkedStateChanged) {
                updateOnScreenCheckedViews();
            }
        }
        if (dispatchItemClick) {
            handled |= super.performItemClick(view, position, id);
        }
        return handled;
    }

    public boolean performItemLongClick(final View child,
            final int longPressPosition, final long longPressId) {
        if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            if (mChoiceActionMode == null &&
                    (mChoiceActionMode = startActionMode(mMultiChoiceModeCallback)) != null) {
                setItemChecked(longPressPosition, true);
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;
        }
        boolean handled = false;
        if (mOnItemLongClickListenerWrapper.wrapped != null) {
            handled = mOnItemLongClickListenerWrapper.wrapped.onItemLongClick(ListView.this, child,
                    longPressPosition, longPressId);
        }
        if (!handled) {
            mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(ListView.this);
        }
        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }

    @Override
    public boolean removeFooterView(View v) {
        if (mFooterViewInfos.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewListAdapter) mAdapter).removeFooter(v)) {
                invalidateViews();
                result = true;
            }
            removeViewInfo(v, mFooterViewInfos);
            return result;
        }
        return false;
    }

    @Override
    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewListAdapter) mAdapter).removeHeader(v)) {
                invalidateViews();
                result = true;
            }
            removeViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeViewInfo(View v, List<ViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            ViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    protected void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mOnScrollListener != null) {
                mLastScrollState = newState;
                mOnScrollListener.onScrollStateChanged(this, newState);
            }
        }
    }

    public final void setActivity(Activity activity) {
        mActivity = activity;
        if (mActivity != null) {
            mActivity.addOnWindowFocusChangeListener(this);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter == null) {
            mAdapter = null;
        } else if (mForceHeaderListAdapter || mHeaderViewInfos.size() > 0
                || mFooterViewInfos.size() > 0) {
            mAdapter = new HeaderViewListAdapter(mHeaderViewInfos, mFooterViewInfos, adapter,
                    this);
        } else {
            mAdapter = new ListAdapterWrapper(adapter, this);
        }
        if (mAdapter != null) {
            mAdapterHasStableIds = mAdapter.hasStableIds();
            if (mChoiceMode != CHOICE_MODE_NONE && mAdapterHasStableIds &&
                    mCheckedIdStates == null) {
                mCheckedIdStates = new LongSparseArray<Integer>();
            }
        }
        if (mCheckStates != null) {
            mCheckStates.clear();
        }
        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }
        super.setAdapter(mAdapter);
    }

    @Override
    public void setChoiceMode(int choiceMode) {
        mChoiceMode = choiceMode;
        if (mChoiceActionMode != null) {
            mChoiceActionMode.finish();
            mChoiceActionMode = null;
        }
        if (mChoiceMode != CHOICE_MODE_NONE) {
            if (mCheckStates == null) {
                mCheckStates = new SparseBooleanArray();
            }
            if (mCheckedIdStates == null && mAdapter != null && mAdapter.hasStableIds()) {
                mCheckedIdStates = new LongSparseArray<Integer>();
            }
            if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
                clearChoices();
                setLongClickable(true);
                setEnableModalBackgroundWrapper(true);
            }
        }
    }

    public void setEnableModalBackgroundWrapper(boolean enableModalBackgroundWrapper) {
        if (enableModalBackgroundWrapper == mEnableModalBackgroundWrapper) {
            return;
        }
        mEnableModalBackgroundWrapper = enableModalBackgroundWrapper;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setForceHeaderListAdapter(boolean forceHeaderListAdapter) {
        mForceHeaderListAdapter = forceHeaderListAdapter;
    }

    @Override
    public void setItemChecked(int position, boolean value) {
        if (mChoiceMode == CHOICE_MODE_NONE) {
            return;
        }
        if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode == null) {
            mChoiceActionMode = startActionMode(mMultiChoiceModeCallback);
        }
        if (mChoiceMode == CHOICE_MODE_MULTIPLE || mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
            boolean oldValue = mCheckStates.get(position);
            mCheckStates.put(position, value);
            if (mCheckedIdStates != null && mAdapter.hasStableIds()) {
                if (value) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                } else {
                    mCheckedIdStates.delete(mAdapter.getItemId(position));
                }
            }
            if (oldValue != value) {
                if (value) {
                    mCheckedItemCount++;
                } else {
                    mCheckedItemCount--;
                }
            }
            if (mChoiceActionMode != null) {
                final long id = mAdapter.getItemId(position);
                mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode,
                        position, id, value);
            }
        } else {
            boolean updateIds = mCheckedIdStates != null && mAdapter.hasStableIds();
            if (value || isItemChecked(position)) {
                mCheckStates.clear();
                if (updateIds) {
                    mCheckedIdStates.clear();
                }
            }
            if (value) {
                mCheckStates.put(position, true);
                if (updateIds) {
                    mCheckedIdStates.put(mAdapter.getItemId(position), position);
                }
                mCheckedItemCount = 1;
            } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
                mCheckedItemCount = 0;
            }
        }
        updateOnScreenCheckedViews();
        invalidateViews();
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (mMultiChoiceModeCallback == null) {
            mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
        }
        mMultiChoiceModeCallback.setWrapped(listener);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListenerWrapper.setWrapped(listener);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(mOnScrollListener = l);
    }

    protected final void setStateOnView(View child, boolean value) {
        if (child instanceof Checkable) {
            ((Checkable) child).setChecked(value);
        } else if (USE_ACTIVATED) {
            child.setActivated(value);
        }
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        final int longPressPosition = getPositionForView(originalView);
        if (longPressPosition >= 0) {
            final long longPressId = mAdapter.getItemId(longPressPosition);
            boolean handled = false;
            if (mOnItemLongClickListenerWrapper.wrapped != null) {
                handled = mOnItemLongClickListenerWrapper.wrapped.onItemLongClick(ListView.this,
                        originalView, longPressPosition, longPressId);
            }
            if (!handled) {
                mContextMenuInfo = createContextMenuInfo(getChildAt(longPressPosition
                        - getFirstVisiblePosition()), longPressPosition, longPressId);
                handled = super.showContextMenuForChild(originalView);
            }
            return handled;
        }
        return false;
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (mActivity != null) {
            return mActivity.startActionMode(callback);
        }
        throw new RuntimeException("HoloEverywhere.ListView (" + this
                + ") don't have reference on Activity");
    }

    private void updateOnScreenCheckedViews() {
        if (mCheckStates == null) {
            return;
        }
        final int firstPos = getFirstVisiblePosition();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int position = firstPos + i;
            final boolean value = mCheckStates.get(position);
            setStateOnView(child, value);
        }
    }
}
