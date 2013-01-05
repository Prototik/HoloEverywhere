
package org.holoeverywhere.widget;

import java.util.ArrayList;

import org.holoeverywhere.R;
import org.holoeverywhere.widget.ExpandableListConnector.PositionMetadata;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorTreeAdapter;

/**
 * A view that shows items in a vertically scrolling two-level list. This
 * differs from the {@link ListView} by allowing two levels: groups which can
 * individually be expanded to show its children. The items come from the
 * {@link ExpandableListAdapter} associated with this view.
 * <p>
 * Expandable lists are able to show an indicator beside each item to display
 * the item's current state (the states are usually one of expanded group,
 * collapsed group, child, or last child). Use
 * {@link #setChildIndicator(Drawable)} or {@link #setGroupIndicator(Drawable)}
 * (or the corresponding XML attributes) to set these indicators (see the docs
 * for each method to see additional state that each Drawable can have). The
 * default style for an {@link ExpandableListView} provides indicators which
 * will be shown next to Views given to the {@link ExpandableListView}. The
 * layouts android.R.layout.simple_expandable_list_item_1 and
 * android.R.layout.simple_expandable_list_item_2 (which should be used with
 * {@link SimpleCursorTreeAdapter}) contain the preferred position information
 * for indicators.
 * <p>
 * The context menu information set by an {@link ExpandableListView} will be a
 * {@link ExpandableListContextMenuInfo} object with
 * {@link ExpandableListContextMenuInfo#packedPosition} being a packed position
 * that can be used with {@link #getPackedPositionType(long)} and the other
 * similar methods.
 * <p>
 * <em><b>Note:</b></em> You cannot use the value <code>wrap_content</code> for
 * the <code>android:layout_height</code> attribute of a ExpandableListView in
 * XML if the parent's size is also not strictly specified (for example, if the
 * parent were ScrollView you could not specify wrap_content since it also can
 * be any length. However, you can use wrap_content if the ExpandableListView
 * parent has a specific size, such as 100 pixels.
 * 
 * @attr ref android.R.styleable#ExpandableListView_groupIndicator
 * @attr ref android.R.styleable#ExpandableListView_indicatorLeft
 * @attr ref android.R.styleable#ExpandableListView_indicatorRight
 * @attr ref android.R.styleable#ExpandableListView_childIndicator
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorLeft
 * @attr ref android.R.styleable#ExpandableListView_childIndicatorRight
 * @attr ref android.R.styleable#ExpandableListView_childDivider
 */
public class ExpandableListView extends ListView {

    /**
     * Extra menu information specific to an {@link ExpandableListView} provided
     * to the
     * {@link android.view.View.OnCreateContextMenuListener#onCreateContextMenu(ContextMenu, View, ContextMenuInfo) }
     * callback when a context menu is brought up for this AdapterView.
     */
    public static class ExpandableListContextMenuInfo implements ContextMenu.ContextMenuInfo {

        /**
         * The ID of the item (group or child) for which the context menu is
         * being displayed.
         */
        public long id;

        /**
         * The packed position in the list represented by the adapter for which
         * the context menu is being displayed. Use the methods
         * {@link ExpandableListView#getPackedPositionType},
         * {@link ExpandableListView#getPackedPositionChild}, and
         * {@link ExpandableListView#getPackedPositionGroup} to unpack this.
         */
        public long packedPosition;

        /**
         * The view for which the context menu is being displayed. This will be
         * one of the children Views of this {@link ExpandableListView}.
         */
        public View targetView;

        public ExpandableListContextMenuInfo(View targetView, long packedPosition, long id) {
            this.targetView = targetView;
            this.packedPosition = packedPosition;
            this.id = id;
        }
    }

    /**
     * Interface definition for a callback to be invoked when a child in this
     * expandable list has been clicked.
     */
    public interface OnChildClickListener {
        /**
         * Callback method to be invoked when a child in this expandable list
         * has been clicked.
         * 
         * @param parent The ExpandableListView where the click happened
         * @param v The view within the expandable list/ListView that was
         *            clicked
         * @param groupPosition The group position that contains the child that
         *            was clicked
         * @param childPosition The child position within the group
         * @param id The row id of the child that was clicked
         * @return True if the click was handled
         */
        boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                int childPosition, long id);
    }

    /**
     * Interface definition for a callback to be invoked when a group in this
     * expandable list has been clicked.
     */
    public interface OnGroupClickListener {
        /**
         * Callback method to be invoked when a group in this expandable list
         * has been clicked.
         * 
         * @param parent The ExpandableListConnector where the click happened
         * @param v The view within the expandable list/ListView that was
         *            clicked
         * @param groupPosition The group position that was clicked
         * @param id The row id of the group that was clicked
         * @return True if the click was handled
         */
        boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                long id);
    }

    /** Used for being notified when a group is collapsed */
    public interface OnGroupCollapseListener {
        /**
         * Callback method to be invoked when a group in this expandable list
         * has been collapsed.
         * 
         * @param groupPosition The group position that was collapsed
         */
        void onGroupCollapse(int groupPosition);
    }

    /** Used for being notified when a group is expanded */
    public interface OnGroupExpandListener {
        /**
         * Callback method to be invoked when a group in this expandable list
         * has been expanded.
         * 
         * @param groupPosition The group position that was expanded
         */
        void onGroupExpand(int groupPosition);
    }

    static class SavedState extends BaseSavedState {
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

        ArrayList<ExpandableListConnector.GroupMetadata> expandedGroupMetadataList;

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            expandedGroupMetadataList = new ArrayList<ExpandableListConnector.GroupMetadata>();
            in.readList(expandedGroupMetadataList, ExpandableListConnector.class.getClassLoader());
        }

        /**
         * Constructor called from
         * {@link ExpandableListView#onSaveInstanceState()}
         */
        SavedState(
                Parcelable superState,
                ArrayList<ExpandableListConnector.GroupMetadata> expandedGroupMetadataList) {
            super(superState);
            this.expandedGroupMetadataList = expandedGroupMetadataList;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(expandedGroupMetadataList);
        }
    }

    /**
     * Denotes when a child indicator should inherit this bound from the generic
     * indicator bounds
     */
    public static final int CHILD_INDICATOR_INHERIT = -1;

    /** State indicating the child is the last within its group. */
    private static final int[] CHILD_LAST_STATE_SET =
    {
            android.R.attr.state_last
    };

    private static final int[] EMPTY_STATE_SET = {};

    /** State indicating the group is empty (has no children). */
    private static final int[] GROUP_EMPTY_STATE_SET =
    {
            android.R.attr.state_empty
    };

    /** State indicating the group is expanded and empty (has no children). */
    private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET =
    {
            android.R.attr.state_expanded, android.R.attr.state_empty
    };

    /** State indicating the group is expanded. */
    private static final int[] GROUP_EXPANDED_STATE_SET =
    {
            android.R.attr.state_expanded
    };

    /** States for the group where the 0th bit is expanded and 1st bit is empty. */
    private static final int[][] GROUP_STATE_SETS = {
            EMPTY_STATE_SET, // 00
            GROUP_EXPANDED_STATE_SET, // 01
            GROUP_EMPTY_STATE_SET, // 10
            GROUP_EXPANDED_EMPTY_STATE_SET
            // 11
    };

    /** The mask (in integer child position representation) for the child */
    private static final long PACKED_POSITION_INT_MASK_CHILD = 0xFFFFFFFF;

    /** The mask (in integer group position representation) for the group */
    private static final long PACKED_POSITION_INT_MASK_GROUP = 0x7FFFFFFF;

    /** The mask (in packed position representation) for the child */
    private static final long PACKED_POSITION_MASK_CHILD = 0x00000000FFFFFFFFL;

    /** The mask (in packed position representation) for the group */
    private static final long PACKED_POSITION_MASK_GROUP = 0x7FFFFFFF00000000L;

    /** The mask (in packed position representation) for the type */
    private static final long PACKED_POSITION_MASK_TYPE = 0x8000000000000000L;

    /** The shift amount (in packed position representation) for the group */
    private static final long PACKED_POSITION_SHIFT_GROUP = 32;

    /** The shift amount (in packed position representation) for the type */
    private static final long PACKED_POSITION_SHIFT_TYPE = 63;

    /**
     * The packed position represents a child.
     */
    public static final int PACKED_POSITION_TYPE_CHILD = 1;

    /**
     * The packed position represents a group.
     */
    public static final int PACKED_POSITION_TYPE_GROUP = 0;

    /**
     * The packed position represents a neither/null/no preference.
     */
    public static final int PACKED_POSITION_TYPE_NULL = 2;

    /**
     * The value for a packed position that represents neither/null/no
     * preference. This value is not otherwise possible since a group type
     * (first bit 0) should not have a child position filled.
     */
    public static final long PACKED_POSITION_VALUE_NULL = 0x00000000FFFFFFFFL;

    /**
     * Gets the child position from a packed position that is of
     * {@link #PACKED_POSITION_TYPE_CHILD} type (use
     * {@link #getPackedPositionType(long)}). To get the group that this child
     * belongs to, use {@link #getPackedPositionGroup(long)}. See
     * {@link #getPackedPositionForChild(int, int)}.
     * 
     * @param packedPosition The packed position from which the child position
     *            will be returned.
     * @return The child position portion of the packed position. If this does
     *         not contain a child, returns -1.
     */
    public static int getPackedPositionChild(long packedPosition) {
        // Null
        if (packedPosition == PACKED_POSITION_VALUE_NULL) {
            return -1;
        }

        // Group since a group type clears this bit
        if ((packedPosition & PACKED_POSITION_MASK_TYPE) != PACKED_POSITION_MASK_TYPE) {
            return -1;
        }

        return (int) (packedPosition & PACKED_POSITION_MASK_CHILD);
    }

    /**
     * Returns the packed position representation of a child's position.
     * <p>
     * In general, a packed position should be used in situations where the
     * position given to/returned from an {@link ExpandableListAdapter} or
     * {@link ExpandableListView} method can either be a child or group. The two
     * positions are packed into a single long which can be unpacked using
     * {@link #getPackedPositionChild(long)},
     * {@link #getPackedPositionGroup(long)}, and
     * {@link #getPackedPositionType(long)}.
     * 
     * @param groupPosition The child's parent group's position.
     * @param childPosition The child position within the group.
     * @return The packed position representation of the child (and parent
     *         group).
     */
    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return (long) PACKED_POSITION_TYPE_CHILD << PACKED_POSITION_SHIFT_TYPE
                | (groupPosition & PACKED_POSITION_INT_MASK_GROUP)
                << PACKED_POSITION_SHIFT_GROUP
                | childPosition & PACKED_POSITION_INT_MASK_CHILD;
    }

    /**
     * Returns the packed position representation of a group's position. See
     * {@link #getPackedPositionForChild(int, int)}.
     * 
     * @param groupPosition The child's parent group's position.
     * @return The packed position representation of the group.
     */
    public static long getPackedPositionForGroup(int groupPosition) {
        // No need to OR a type in because PACKED_POSITION_GROUP == 0
        return (groupPosition & PACKED_POSITION_INT_MASK_GROUP)
        << PACKED_POSITION_SHIFT_GROUP;
    }

    /**
     * Gets the group position from a packed position. See
     * {@link #getPackedPositionForChild(int, int)}.
     * 
     * @param packedPosition The packed position from which the group position
     *            will be returned.
     * @return The group position portion of the packed position. If this does
     *         not contain a group, returns -1.
     */
    public static int getPackedPositionGroup(long packedPosition) {
        // Null
        if (packedPosition == PACKED_POSITION_VALUE_NULL) {
            return -1;
        }

        return (int) ((packedPosition & PACKED_POSITION_MASK_GROUP) >> PACKED_POSITION_SHIFT_GROUP);
    }

    /**
     * Gets the type of a packed position. See
     * {@link #getPackedPositionForChild(int, int)}.
     * 
     * @param packedPosition The packed position for which to return the type.
     * @return The type of the position contained within the packed position,
     *         either {@link #PACKED_POSITION_TYPE_CHILD},
     *         {@link #PACKED_POSITION_TYPE_GROUP}, or
     *         {@link #PACKED_POSITION_TYPE_NULL}.
     */
    public static int getPackedPositionType(long packedPosition) {
        if (packedPosition == PACKED_POSITION_VALUE_NULL) {
            return PACKED_POSITION_TYPE_NULL;
        }

        return (packedPosition & PACKED_POSITION_MASK_TYPE) == PACKED_POSITION_MASK_TYPE
                ? PACKED_POSITION_TYPE_CHILD
                : PACKED_POSITION_TYPE_GROUP;
    }

    /** Gives us Views through group+child positions */
    private ExpandableListAdapter mAdapter;

    /** Drawable to be used as a divider when it is adjacent to any children */
    private Drawable mChildDivider;

    /** The indicator drawn next to a child. */
    private Drawable mChildIndicator;

    /**
     * Left bound for drawing the indicator of a child. Value of
     * {@link #CHILD_INDICATOR_INHERIT} means use mIndicatorLeft.
     */
    private int mChildIndicatorLeft;

    /**
     * Right bound for drawing the indicator of a child. Value of
     * {@link #CHILD_INDICATOR_INHERIT} means use mIndicatorRight.
     */
    private int mChildIndicatorRight;

    private boolean mClipToPadding = false;

    /**
     * Serves as the glue/translator between a ListView and an
     * ExpandableListView
     */
    private ExpandableListConnector mConnector;

    /** The indicator drawn next to a group. */
    private Drawable mGroupIndicator;

    /** Left bound for drawing the indicator. */
    private int mIndicatorLeft;

    // Bounds of the indicator to be drawn
    private final Rect mIndicatorRect = new Rect();

    /** Right bound for drawing the indicator. */
    private int mIndicatorRight;

    private OnChildClickListener mOnChildClickListener;

    private OnGroupClickListener mOnGroupClickListener;

    private OnGroupCollapseListener mOnGroupCollapseListener;

    private OnGroupExpandListener mOnGroupExpandListener;

    public ExpandableListView(Context context) {
        this(context, null);
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.expandableListViewStyle);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableListView,
                defStyle, R.style.Holo_ExpandableListView);
        mGroupIndicator = a
                .getDrawable(R.styleable.ExpandableListView_android_groupIndicator);
        mChildIndicator = a
                .getDrawable(R.styleable.ExpandableListView_android_childIndicator);
        mIndicatorLeft = a
                .getDimensionPixelSize(
                        R.styleable.ExpandableListView_android_indicatorLeft, 0);
        mIndicatorRight = a
                .getDimensionPixelSize(
                        R.styleable.ExpandableListView_android_indicatorRight, 0);
        if (mIndicatorRight == 0 && mGroupIndicator != null) {
            mIndicatorRight = mIndicatorLeft + mGroupIndicator.getIntrinsicWidth();
        }
        mChildIndicatorLeft = a.getDimensionPixelSize(
                R.styleable.ExpandableListView_android_childIndicatorLeft,
                CHILD_INDICATOR_INHERIT);
        mChildIndicatorRight = a.getDimensionPixelSize(
                R.styleable.ExpandableListView_android_childIndicatorRight,
                CHILD_INDICATOR_INHERIT);
        mChildDivider = a
                .getDrawable(R.styleable.ExpandableListView_android_childDivider);

        a.recycle();
    }

    /**
     * Collapse a group in the grouped list view
     * 
     * @param groupPos position of the group to collapse
     * @return True if the group was collapsed, false otherwise (if the group
     *         was already collapsed, this will return false)
     */
    public boolean collapseGroup(int groupPos) {
        boolean retValue = mConnector.collapseGroup(groupPos);

        if (mOnGroupCollapseListener != null) {
            mOnGroupCollapseListener.onGroupCollapse(groupPos);
        }

        return retValue;
    }

    @Override
    protected ContextMenuInfo createContextMenuInfo(View view, int flatListPosition, long id) {
        if (isHeaderOrFooterPosition(flatListPosition)) {
            return super.createContextMenuInfo(view, flatListPosition, id);
        }
        final int adjustedPosition = getFlatPositionForConnector(flatListPosition);
        PositionMetadata pm = mConnector.getUnflattenedPos(adjustedPosition);
        ExpandableListPosition pos = pm.position;
        id = getChildOrGroupId(pos);
        long packedPosition = pos.getPackedPosition();
        pm.recycle();
        return new ExpandableListContextMenuInfo(view, packedPosition, id);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw children, etc.
        super.dispatchDraw(canvas);

        // If we have any indicators to draw, we do it here
        if (mChildIndicator == null && mGroupIndicator == null) {
            return;
        }

        int saveCount = 0;

        final boolean clipToPadding = mClipToPadding;
        if (clipToPadding) {
            saveCount = canvas.save();
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
            canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                    scrollX + getRight() - getLeft() - getPaddingRight(),
                    scrollY + getBottom() - getTop() - getPaddingBottom());
        }

        final int headerViewsCount = getHeaderViewsCount();

        final int lastChildFlPos = getCount() - getFooterViewsCount() - headerViewsCount - 1;

        final int myB = getBottom();

        PositionMetadata pos;
        View item;
        Drawable indicator;
        int t, b;

        // Start at a value that is neither child nor group
        int lastItemType = ~(ExpandableListPosition.CHILD | ExpandableListPosition.GROUP);

        final Rect indicatorRect = mIndicatorRect;

        // The "child" mentioned in the following two lines is this
        // View's child, not referring to an expandable list's
        // notion of a child (as opposed to a group)
        final int childCount = getChildCount();
        for (int i = 0, childFlPos = getFirstVisiblePosition() - headerViewsCount; i < childCount; i++, childFlPos++) {

            if (childFlPos < 0) {
                // This child is header
                continue;
            } else if (childFlPos > lastChildFlPos) {
                // This child is footer, so are all subsequent children
                break;
            }

            item = getChildAt(i);
            t = item.getTop();
            b = item.getBottom();

            // This item isn't on the screen
            if (b < 0 || t > myB) {
                continue;
            }

            // Get more expandable list-related info for this item
            pos = mConnector.getUnflattenedPos(childFlPos);

            // If this item type and the previous item type are different, then
            // we need to change
            // the left & right bounds
            if (pos.position.type != lastItemType) {
                if (pos.position.type == ExpandableListPosition.CHILD) {
                    indicatorRect.left = mChildIndicatorLeft == CHILD_INDICATOR_INHERIT ?
                            mIndicatorLeft : mChildIndicatorLeft;
                    indicatorRect.right = mChildIndicatorRight == CHILD_INDICATOR_INHERIT ?
                            mIndicatorRight : mChildIndicatorRight;
                } else {
                    indicatorRect.left = mIndicatorLeft;
                    indicatorRect.right = mIndicatorRight;
                }

                indicatorRect.left += getPaddingLeft();
                indicatorRect.right += getPaddingLeft();

                lastItemType = pos.position.type;
            }

            if (indicatorRect.left != indicatorRect.right) {
                // Use item's full height + the divider height
                if (isStackFromBottom()) {
                    // See ListView#dispatchDraw
                    indicatorRect.top = t;// - mDividerHeight;
                    indicatorRect.bottom = b;
                } else {
                    indicatorRect.top = t;
                    indicatorRect.bottom = b;// + mDividerHeight;
                }

                // Get the indicator (with its state set to the item's state)
                indicator = getIndicator(pos);
                if (indicator != null) {
                    // Draw the indicator
                    indicator.setBounds(indicatorRect);
                    indicator.draw(canvas);
                }
            }
            pos.recycle();
        }

        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
        }
    }

    /**
     * Expand a group in the grouped list view
     * 
     * @param groupPos the group to be expanded
     * @return True if the group was expanded, false otherwise (if the group was
     *         already expanded, this will return false)
     */
    public boolean expandGroup(int groupPos) {
        return expandGroup(groupPos, false);
    }

    /**
     * Expand a group in the grouped list view
     * 
     * @param groupPos the group to be expanded
     * @param animate true if the expanding group should be animated in
     * @return True if the group was expanded, false otherwise (if the group was
     *         already expanded, this will return false)
     */
    public boolean expandGroup(int groupPos, boolean animate) {
        ExpandableListPosition elGroupPos = ExpandableListPosition.obtain(
                ExpandableListPosition.GROUP, groupPos, -1, -1);
        PositionMetadata pm = mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        boolean retValue = mConnector.expandGroup(pm);

        if (mOnGroupExpandListener != null) {
            mOnGroupExpandListener.onGroupExpand(groupPos);
        }

        if (animate) {
            final int groupFlatPos = pm.position.flatListPos;

            final int shiftedGroupPosition = groupFlatPos + getHeaderViewsCount();
            smoothScrollToPosition(shiftedGroupPosition + mAdapter.getChildrenCount(groupPos),
                    shiftedGroupPosition);
        }
        pm.recycle();

        return retValue;
    }

    /**
     * Converts a group/child flat position into an absolute flat position, that
     * takes into account the possible headers.
     * 
     * @param flatListPosition The child/group flat position
     * @return An absolute flat position.
     */
    private int getAbsoluteFlatPosition(int flatListPosition) {
        return flatListPosition + getHeaderViewsCount();
    }

    /**
     * This method should not be used, use {@link #getExpandableListAdapter()}.
     */
    @Override
    public ListAdapter getAdapter() {
        /*
         * The developer should never really call this method on an
         * ExpandableListView, so it would be nice to throw a RuntimeException,
         * but AdapterView calls this
         */
        return super.getAdapter();
    }

    private long getChildOrGroupId(ExpandableListPosition position) {
        if (position.type == ExpandableListPosition.CHILD) {
            return mAdapter.getChildId(position.groupPos, position.childPos);
        } else {
            return mAdapter.getGroupId(position.groupPos);
        }
    }

    /**
     * Gets the adapter that provides data to this view.
     * 
     * @return The adapter that provides data to this view.
     */
    public ExpandableListAdapter getExpandableListAdapter() {
        return mAdapter;
    }

    /**
     * Converts a flat list position (the raw position of an item (child or
     * group) in the list) to a group and/or child position (represented in a
     * packed position). This is useful in situations where the caller needs to
     * use the underlying {@link ListView}'s methods. Use
     * {@link ExpandableListView#getPackedPositionType} ,
     * {@link ExpandableListView#getPackedPositionChild},
     * {@link ExpandableListView#getPackedPositionGroup} to unpack.
     * 
     * @param flatListPosition The flat list position to be converted.
     * @return The group and/or child position for the given flat list position
     *         in packed position representation. #PACKED_POSITION_VALUE_NULL if
     *         the position corresponds to a header or a footer item.
     */
    public long getExpandableListPosition(int flatListPosition) {
        if (isHeaderOrFooterPosition(flatListPosition)) {
            return PACKED_POSITION_VALUE_NULL;
        }

        final int adjustedPosition = getFlatPositionForConnector(flatListPosition);
        PositionMetadata pm = mConnector.getUnflattenedPos(adjustedPosition);
        long packedPos = pm.position.getPackedPosition();
        pm.recycle();
        return packedPos;
    }

    /**
     * Converts a group and/or child position to a flat list position. This is
     * useful in situations where the caller needs to use the underlying
     * {@link ListView}'s methods.
     * 
     * @param packedPosition The group and/or child positions to be converted in
     *            packed position representation. Use
     *            {@link #getPackedPositionForChild(int, int)} or
     *            {@link #getPackedPositionForGroup(int)}.
     * @return The flat list position for the given child or group.
     */
    public int getFlatListPosition(long packedPosition) {
        ExpandableListPosition elPackedPos = ExpandableListPosition
                .obtainPosition(packedPosition);
        PositionMetadata pm = mConnector.getFlattenedPos(elPackedPos);
        elPackedPos.recycle();
        final int flatListPosition = pm.position.flatListPos;
        pm.recycle();
        return getAbsoluteFlatPosition(flatListPosition);
    }

    /**
     * Converts an absolute item flat position into a group/child flat position,
     * shifting according to the number of header items.
     * 
     * @param flatListPosition The absolute flat position
     * @return A group/child flat position as expected by the connector.
     */
    private int getFlatPositionForConnector(int flatListPosition) {
        return flatListPosition - getHeaderViewsCount();
    }

    /**
     * Gets the indicator for the item at the given position. If the indicator
     * is stateful, the state will be given to the indicator.
     * 
     * @param pos The flat list position of the item whose indicator should be
     *            returned.
     * @return The indicator in the proper state.
     */
    private Drawable getIndicator(PositionMetadata pos) {
        Drawable indicator;

        if (pos.position.type == ExpandableListPosition.GROUP) {
            indicator = mGroupIndicator;

            if (indicator != null && indicator.isStateful()) {
                // Empty check based on availability of data. If the
                // groupMetadata isn't null,
                // we do a check on it. Otherwise, the group is collapsed so we
                // consider it
                // empty for performance reasons.
                boolean isEmpty = pos.groupMetadata == null ||
                        pos.groupMetadata.lastChildFlPos == pos.groupMetadata.flPos;

                final int stateSetIndex =
                        (pos.isExpanded() ? 1 : 0) | // Expanded?
                                (isEmpty ? 2 : 0); // Empty?
                indicator.setState(GROUP_STATE_SETS[stateSetIndex]);
            }
        } else {
            indicator = mChildIndicator;

            if (indicator != null && indicator.isStateful()) {
                // No need for a state sets array for the child since it only
                // has two states
                final int stateSet[] = pos.position.flatListPos == pos.groupMetadata.lastChildFlPos
                        ? CHILD_LAST_STATE_SET
                        : EMPTY_STATE_SET;
                indicator.setState(stateSet);
            }
        }

        return indicator;
    }

    /**
     * Gets the ID of the currently selected group or child. Can return -1 if no
     * selection.
     * 
     * @return The ID of the currently selected group or child. -1 if no
     *         selection.
     */
    public long getSelectedId() {
        long packedPos = getSelectedPosition();
        if (packedPos == PACKED_POSITION_VALUE_NULL) {
            return -1;
        }

        int groupPos = getPackedPositionGroup(packedPos);

        if (getPackedPositionType(packedPos) == PACKED_POSITION_TYPE_GROUP) {
            // It's a group
            return mAdapter.getGroupId(groupPos);
        } else {
            // It's a child
            return mAdapter.getChildId(groupPos, getPackedPositionChild(packedPos));
        }
    }

    /**
     * Gets the position of the currently selected group or child (along with
     * its type). Can return {@link #PACKED_POSITION_VALUE_NULL} if no
     * selection.
     * 
     * @return A packed position containing the currently selected group or
     *         child's position and type. #PACKED_POSITION_VALUE_NULL if no
     *         selection or if selection is on a header or a footer item.
     */
    public long getSelectedPosition() {
        final int selectedPos = getSelectedItemPosition();

        // The case where there is no selection (selectedPos == -1) is also
        // handled here.
        return getExpandableListPosition(selectedPos);
    }

    /**
     * This will either expand/collapse groups (if a group was clicked) or pass
     * on the click to the proper child (if a child was clicked)
     * 
     * @param position The flat list position. This has already been factored to
     *            remove the header/footer.
     * @param id The ListAdapter ID, not the group or child ID.
     */
    boolean handleItemClick(View v, int position, long id) {
        final PositionMetadata posMetadata = mConnector.getUnflattenedPos(position);

        id = getChildOrGroupId(posMetadata.position);

        boolean returnValue;
        if (posMetadata.position.type == ExpandableListPosition.GROUP) {
            /* It's a group, so handle collapsing/expanding */

            /* It's a group click, so pass on event */
            if (mOnGroupClickListener != null) {
                if (mOnGroupClickListener.onGroupClick(this, v,
                        posMetadata.position.groupPos, id)) {
                    posMetadata.recycle();
                    return true;
                }
            }

            if (posMetadata.isExpanded()) {
                /* Collapse it */
                mConnector.collapseGroup(posMetadata);

                playSoundEffect(SoundEffectConstants.CLICK);

                if (mOnGroupCollapseListener != null) {
                    mOnGroupCollapseListener.onGroupCollapse(posMetadata.position.groupPos);
                }
            } else {
                /* Expand it */
                mConnector.expandGroup(posMetadata);

                playSoundEffect(SoundEffectConstants.CLICK);

                if (mOnGroupExpandListener != null) {
                    mOnGroupExpandListener.onGroupExpand(posMetadata.position.groupPos);
                }

                final int groupPos = posMetadata.position.groupPos;
                final int groupFlatPos = posMetadata.position.flatListPos;

                final int shiftedGroupPosition = groupFlatPos + getHeaderViewsCount();
                smoothScrollToPosition(shiftedGroupPosition + mAdapter.getChildrenCount(groupPos),
                        shiftedGroupPosition);
            }

            returnValue = true;
        } else {
            /* It's a child, so pass on event */
            if (mOnChildClickListener != null) {
                playSoundEffect(SoundEffectConstants.CLICK);
                return mOnChildClickListener.onChildClick(this, v, posMetadata.position.groupPos,
                        posMetadata.position.childPos, id);
            }

            returnValue = false;
        }

        posMetadata.recycle();

        return returnValue;
    }

    /**
     * Whether the given group is currently expanded.
     * 
     * @param groupPosition The group to check.
     * @return Whether the group is currently expanded.
     */
    public boolean isGroupExpanded(int groupPosition) {
        return mConnector.isGroupExpanded(groupPosition);
    }

    /**
     * @param position An absolute (including header and footer) flat list
     *            position.
     * @return true if the position corresponds to a header or a footer item.
     */
    private boolean isHeaderOrFooterPosition(int position) {
        final int footerViewsStart = getCount() - getFooterViewsCount();
        return position < getHeaderViewsCount() || position >= footerViewsStart;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ExpandableListView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ExpandableListView.class.getName());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mConnector != null && ss.expandedGroupMetadataList != null) {
            mConnector.setExpandedGroupMetadataList(ss.expandedGroupMetadataList);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState,
                mConnector != null ? mConnector.getExpandedGroupMetadataList() : null);
    }

    @Override
    public boolean performItemClick(View v, int position, long id) {
        // Ignore clicks in header/footers
        if (isHeaderOrFooterPosition(position)) {
            // Clicked on a header/footer, so ignore pass it on to super
            return super.performItemClick(v, position, id);
        }

        // Internally handle the item click
        final int adjustedPosition = getFlatPositionForConnector(position);
        return handleItemClick(v, adjustedPosition, id);
    }

    /**
     * Sets the adapter that provides data to this view.
     * 
     * @param adapter The adapter that provides data to this view.
     */
    public void setAdapter(ExpandableListAdapter adapter) {
        // Set member variable
        mAdapter = adapter;

        if (adapter != null) {
            // Create the connector
            mConnector = new ExpandableListConnector(adapter);
        } else {
            mConnector = null;
        }

        // Link the ListView (superclass) to the expandable list data through
        // the connector
        super.setAdapter(mConnector);
    }

    /**
     * TODO
     * 
     * @Override void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
     *           int flatListPosition = childIndex + mFirstPosition; // Only
     *           proceed as possible child if the divider isn't above all items
     *           // (if it is above // all items, then the item below it has to
     *           be a group) if (flatListPosition >= 0) { final int
     *           adjustedPosition =
     *           getFlatPositionForConnector(flatListPosition); PositionMetadata
     *           pos = mConnector.getUnflattenedPos(adjustedPosition); // If
     *           this item is a child, or it is a non-empty group that is //
     *           expanded if ((pos.position.type ==
     *           ExpandableListPosition.CHILD) || (pos.isExpanded() &&
     *           pos.groupMetadata.lastChildFlPos != pos.groupMetadata.flPos)) {
     *           // These are the cases where we draw the child divider final
     *           Drawable divider = mChildDivider; divider.setBounds(bounds);
     *           divider.draw(canvas); pos.recycle(); return; } pos.recycle(); }
     *           // Otherwise draw the default divider super.drawDivider(canvas,
     *           bounds, flatListPosition); }
     */
    /**
     * This overloaded method should not be used, instead use
     * {@link #setAdapter(ExpandableListAdapter)}.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException(
                "For ExpandableListView, use setAdapter(ExpandableListAdapter) instead of " +
                        "setAdapter(ListAdapter)");
    }

    /**
     * Sets the drawable that will be drawn adjacent to every child in the list.
     * This will be drawn using the same height as the normal divider (
     * {@link #setDivider(Drawable)}) or if it does not have an intrinsic
     * height, the height set by {@link #setDividerHeight(int)}.
     * 
     * @param childDivider The drawable to use.
     */
    public void setChildDivider(Drawable childDivider) {
        mChildDivider = childDivider;
    }

    public void setChildIndicator(Drawable childIndicator) {
        mChildIndicator = childIndicator;
    }

    /**
     * Sets the drawing bounds for the child indicator. For either, you can
     * specify {@link #CHILD_INDICATOR_INHERIT} to use inherit from the general
     * indicator's bounds.
     * 
     * @see #setIndicatorBounds(int, int)
     * @param left The left position (relative to the left bounds of this View)
     *            to start drawing the indicator.
     * @param right The right position (relative to the left bounds of this
     *            View) to end the drawing of the indicator.
     */
    public void setChildIndicatorBounds(int left, int right) {
        mChildIndicatorLeft = left;
        mChildIndicatorRight = right;
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(mClipToPadding = clipToPadding);
    }

    /**
     * Sets the indicator to be drawn next to a group.
     * 
     * @param groupIndicator The drawable to be used as an indicator. If the
     *            group is empty, the state {@link android.R.attr#state_empty}
     *            will be set. If the group is expanded, the state
     *            {@link android.R.attr#state_expanded} will be set.
     */
    public void setGroupIndicator(Drawable groupIndicator) {
        mGroupIndicator = groupIndicator;
        if (mIndicatorRight == 0 && mGroupIndicator != null) {
            mIndicatorRight = mIndicatorLeft + mGroupIndicator.getIntrinsicWidth();
        }
    }

    /**
     * Sets the drawing bounds for the indicators (at minimum, the group
     * indicator is affected by this; the child indicator is affected by this if
     * the child indicator bounds are set to inherit).
     * 
     * @see #setChildIndicatorBounds(int, int)
     * @param left The left position (relative to the left bounds of this View)
     *            to start drawing the indicator.
     * @param right The right position (relative to the left bounds of this
     *            View) to end the drawing of the indicator.
     */
    public void setIndicatorBounds(int left, int right) {
        mIndicatorLeft = left;
        mIndicatorRight = right;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        mOnGroupClickListener = onGroupClickListener;
    }

    public void setOnGroupCollapseListener(
            OnGroupCollapseListener onGroupCollapseListener) {
        mOnGroupCollapseListener = onGroupCollapseListener;
    }

    public void setOnGroupExpandListener(
            OnGroupExpandListener onGroupExpandListener) {
        mOnGroupExpandListener = onGroupExpandListener;
    }

    /**
     * Register a callback to be invoked when an item has been clicked and the
     * caller prefers to receive a ListView-style position instead of a group
     * and/or child position. In most cases, the caller should use
     * {@link #setOnGroupClickListener} and/or {@link #setOnChildClickListener}.
     * <p />
     * {@inheritDoc}
     */
    @Override
    public void setOnItemClickListener(OnItemClickListener l) {
        super.setOnItemClickListener(l);
    }

    /**
     * Sets the selection to the specified child. If the child is in a collapsed
     * group, the group will only be expanded and child subsequently selected if
     * shouldExpandGroup is set to true, otherwise the method will return false.
     * 
     * @param groupPosition The position of the group that contains the child.
     * @param childPosition The position of the child within the group.
     * @param shouldExpandGroup Whether the child's group should be expanded if
     *            it is collapsed.
     * @return Whether the selection was successfully set on the child.
     */
    public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup) {
        ExpandableListPosition elChildPos = ExpandableListPosition.obtainChildPosition(
                groupPosition, childPosition);
        PositionMetadata flatChildPos = mConnector.getFlattenedPos(elChildPos);

        if (flatChildPos == null) {
            // The child's group isn't expanded

            // Shouldn't expand the group, so return false for we didn't set the
            // selection
            if (!shouldExpandGroup) {
                return false;
            }

            expandGroup(groupPosition);

            flatChildPos = mConnector.getFlattenedPos(elChildPos);

            // Sanity check
            if (flatChildPos == null) {
                throw new IllegalStateException("Could not find child");
            }
        }

        int absoluteFlatPosition = getAbsoluteFlatPosition(flatChildPos.position.flatListPos);
        super.setSelection(absoluteFlatPosition);

        elChildPos.recycle();
        flatChildPos.recycle();

        return true;
    }

    /**
     * Sets the selection to the specified group.
     * 
     * @param groupPosition The position of the group that should be selected.
     */
    public void setSelectedGroup(int groupPosition) {
        ExpandableListPosition elGroupPos = ExpandableListPosition
                .obtainGroupPosition(groupPosition);
        PositionMetadata pm = mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        final int absoluteFlatPosition = getAbsoluteFlatPosition(pm.position.flatListPos);
        super.setSelection(absoluteFlatPosition);
        pm.recycle();
    }
}
