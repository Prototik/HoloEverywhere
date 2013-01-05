/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.holoeverywhere.widget;

import java.util.ArrayList;
import java.util.Collections;

import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HeterogeneousExpandableList;

public class ExpandableListConnector extends BaseAdapter implements Filterable {
    /**
     * Metadata about an expanded group to help convert from a flat list
     * position to either a) group position for groups, or b) child position for
     * children
     */
    static class GroupMetadata implements Parcelable, Comparable<GroupMetadata> {
        public static final Parcelable.Creator<GroupMetadata> CREATOR =
                new Parcelable.Creator<GroupMetadata>() {

                    @Override
                    public GroupMetadata createFromParcel(Parcel in) {
                        GroupMetadata gm = GroupMetadata.obtain(
                                in.readInt(),
                                in.readInt(),
                                in.readInt(),
                                in.readLong());
                        return gm;
                    }

                    @Override
                    public GroupMetadata[] newArray(int size) {
                        return new GroupMetadata[size];
                    }
                };

        final static int REFRESH = -1;

        /* firstChildFlPos isn't needed since it's (flPos + 1) */

        static GroupMetadata obtain(int flPos, int lastChildFlPos, int gPos, long gId) {
            GroupMetadata gm = new GroupMetadata();
            gm.flPos = flPos;
            gm.lastChildFlPos = lastChildFlPos;
            gm.gPos = gPos;
            gm.gId = gId;
            return gm;
        }

        /** This group's flat list position */
        int flPos;

        /**
         * This group's id
         */
        long gId;

        /**
         * This group's group position
         */
        int gPos;

        /**
         * This group's last child's flat list position, so basically the range
         * of this group in the flat list
         */
        int lastChildFlPos;

        private GroupMetadata() {
        }

        @Override
        public int compareTo(GroupMetadata another) {
            if (another == null) {
                throw new IllegalArgumentException();
            }

            return gPos - another.gPos;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(flPos);
            dest.writeInt(lastChildFlPos);
            dest.writeInt(gPos);
            dest.writeLong(gId);
        }

    }

    protected class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            refreshExpGroupMetadataList(true, true);

            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            refreshExpGroupMetadataList(true, true);

            notifyDataSetInvalidated();
        }
    }

    /**
     * Data type that contains an expandable list position (can refer to either
     * a group or child) and some extra information regarding referred item
     * (such as where to insert into the flat list, etc.)
     */
    static public class PositionMetadata {

        private static final int MAX_POOL_SIZE = 5;
        private static ArrayList<PositionMetadata> sPool =
                new ArrayList<PositionMetadata>(MAX_POOL_SIZE);

        private static PositionMetadata getRecycledOrCreate() {
            PositionMetadata pm;
            synchronized (sPool) {
                if (sPool.size() > 0) {
                    pm = sPool.remove(0);
                } else {
                    return new PositionMetadata();
                }
            }
            pm.resetState();
            return pm;
        }

        static PositionMetadata obtain(int flatListPos, int type, int groupPos,
                int childPos, GroupMetadata groupMetadata, int groupInsertIndex) {
            PositionMetadata pm = getRecycledOrCreate();
            pm.position = ExpandableListPosition.obtain(type, groupPos, childPos, flatListPos);
            pm.groupMetadata = groupMetadata;
            pm.groupInsertIndex = groupInsertIndex;
            return pm;
        }

        /**
         * For groups that are collapsed, we use this as the index (in
         * mExpGroupMetadataList) to insert this group when we are expanding
         * this group.
         */
        public int groupInsertIndex;

        /**
         * Link back to the expanded GroupMetadata for this group. Useful for
         * removing the group from the list of expanded groups inside the
         * connector when we collapse the group, and also as a check to see if
         * the group was expanded or collapsed (this will be null if the group
         * is collapsed since we don't keep that group's metadata)
         */
        public GroupMetadata groupMetadata;

        /** Data type to hold the position and its type (child/group) */
        public ExpandableListPosition position;

        /**
         * Use {@link #obtain(int, int, int, int, GroupMetadata, int)}
         */
        private PositionMetadata() {
        }

        /**
         * Checks whether the group referred to in this object is expanded, or
         * not (at the time this object was created)
         * 
         * @return whether the group at groupPos is expanded or not
         */
        public boolean isExpanded() {
            return groupMetadata != null;
        }

        public void recycle() {
            resetState();
            synchronized (sPool) {
                if (sPool.size() < MAX_POOL_SIZE) {
                    sPool.add(this);
                }
            }
        }

        private void resetState() {
            if (position != null) {
                position.recycle();
                position = null;
            }
            groupMetadata = null;
            groupInsertIndex = 0;
        }
    }

    private final DataSetObserver mDataSetObserver = new MyDataSetObserver();
    private ExpandableListAdapter mExpandableListAdapter;

    private ArrayList<GroupMetadata> mExpGroupMetadataList;

    private int mMaxExpGroupCount = Integer.MAX_VALUE;

    private int mTotalExpChildrenCount;

    public ExpandableListConnector(ExpandableListAdapter expandableListAdapter) {
        mExpGroupMetadataList = new ArrayList<GroupMetadata>();
        setExpandableListAdapter(expandableListAdapter);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mExpandableListAdapter.areAllItemsEnabled();
    }

    /**
     * Collapse a group in the grouped list view
     * 
     * @param groupPos position of the group to collapse
     */
    boolean collapseGroup(int groupPos) {
        ExpandableListPosition elGroupPos = ExpandableListPosition.obtain(
                ExpandableListPosition.GROUP, groupPos, -1, -1);
        PositionMetadata pm = getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        if (pm == null) {
            return false;
        }

        boolean retValue = collapseGroup(pm);
        pm.recycle();
        return retValue;
    }

    boolean collapseGroup(PositionMetadata posMetadata) {
        /*
         * Collapsing requires removal from mExpGroupMetadataList
         */

        /*
         * If it is null, it must be already collapsed. This group metadata
         * object should have been set from the search that returned the
         * position metadata object.
         */
        if (posMetadata.groupMetadata == null) {
            return false;
        }

        // Remove the group from the list of expanded groups
        mExpGroupMetadataList.remove(posMetadata.groupMetadata);

        // Refresh the metadata
        refreshExpGroupMetadataList(false, false);

        // Notify of change
        notifyDataSetChanged();

        // Give the callback
        mExpandableListAdapter.onGroupCollapsed(posMetadata.groupMetadata.gPos);

        return true;
    }

    /**
     * Expand a group in the grouped list view
     * 
     * @param groupPos the group to be expanded
     */
    boolean expandGroup(int groupPos) {
        ExpandableListPosition elGroupPos = ExpandableListPosition.obtain(
                ExpandableListPosition.GROUP, groupPos, -1, -1);
        PositionMetadata pm = getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        boolean retValue = expandGroup(pm);
        pm.recycle();
        return retValue;
    }

    boolean expandGroup(PositionMetadata posMetadata) {
        /*
         * Expanding requires insertion into the mExpGroupMetadataList
         */

        if (posMetadata.position.groupPos < 0) {
            // TODO clean exit
            throw new RuntimeException("Need group");
        }

        if (mMaxExpGroupCount == 0) {
            return false;
        }

        // Check to see if it's already expanded
        if (posMetadata.groupMetadata != null) {
            return false;
        }

        /* Restrict number of expanded groups to mMaxExpGroupCount */
        if (mExpGroupMetadataList.size() >= mMaxExpGroupCount) {
            /* Collapse a group */
            // TODO: Collapse something not on the screen instead of the first
            // one?
            // TODO: Could write overloaded function to take GroupMetadata to
            // collapse
            GroupMetadata collapsedGm = mExpGroupMetadataList.get(0);

            int collapsedIndex = mExpGroupMetadataList.indexOf(collapsedGm);

            collapseGroup(collapsedGm.gPos);

            /* Decrement index if it is after the group we removed */
            if (posMetadata.groupInsertIndex > collapsedIndex) {
                posMetadata.groupInsertIndex--;
            }
        }

        GroupMetadata expandedGm = GroupMetadata.obtain(
                GroupMetadata.REFRESH,
                GroupMetadata.REFRESH,
                posMetadata.position.groupPos,
                mExpandableListAdapter.getGroupId(posMetadata.position.groupPos));

        mExpGroupMetadataList.add(posMetadata.groupInsertIndex, expandedGm);

        // Refresh the metadata
        refreshExpGroupMetadataList(false, false);

        // Notify of change
        notifyDataSetChanged();

        // Give the callback
        mExpandableListAdapter.onGroupExpanded(expandedGm.gPos);

        return true;
    }

    /**
     * Searches the expandable list adapter for a group position matching the
     * given group ID. The search starts at the given seed position and then
     * alternates between moving up and moving down until 1) we find the right
     * position, or 2) we run out of time, or 3) we have looked at every
     * position
     * 
     * @return Position of the row that matches the given row ID, or
     *         {@link AdapterView#INVALID_POSITION} if it can't be found
     * @see AdapterView#findSyncPosition()
     */
    int findGroupPosition(long groupIdToMatch, int seedGroupPosition) {
        int count = mExpandableListAdapter.getGroupCount();

        if (count == 0) {
            return AdapterView.INVALID_POSITION;
        }

        // If there isn't a selection don't hunt for it
        if (groupIdToMatch == AdapterView.INVALID_ROW_ID) {
            return AdapterView.INVALID_POSITION;
        }

        // Pin seed to reasonable values
        seedGroupPosition = Math.max(0, seedGroupPosition);
        seedGroupPosition = Math.min(count - 1, seedGroupPosition);

        long endTime = SystemClock.uptimeMillis() + AdapterView.SYNC_MAX_DURATION_MILLIS;

        long rowId;

        // first position scanned so far
        int first = seedGroupPosition;

        // last position scanned so far
        int last = seedGroupPosition;

        // True if we should move down on the next iteration
        boolean next = false;

        // True when we have looked at the first item in the data
        boolean hitFirst;

        // True when we have looked at the last item in the data
        boolean hitLast;

        // Get the item ID locally (instead of getItemIdAtPosition), so
        // we need the adapter
        ExpandableListAdapter adapter = getAdapter();
        if (adapter == null) {
            return AdapterView.INVALID_POSITION;
        }

        while (SystemClock.uptimeMillis() <= endTime) {
            rowId = adapter.getGroupId(seedGroupPosition);
            if (rowId == groupIdToMatch) {
                // Found it!
                return seedGroupPosition;
            }

            hitLast = last == count - 1;
            hitFirst = first == 0;

            if (hitLast && hitFirst) {
                // Looked at everything
                break;
            }

            if (hitFirst || next && !hitLast) {
                // Either we hit the top, or we are trying to move down
                last++;
                seedGroupPosition = last;
                // Try going up next time
                next = false;
            } else if (hitLast || !next && !hitFirst) {
                // Either we hit the bottom, or we are trying to move up
                first--;
                seedGroupPosition = first;
                // Try going down next time
                next = true;
            }

        }

        return AdapterView.INVALID_POSITION;
    }

    ExpandableListAdapter getAdapter() {
        return mExpandableListAdapter;
    }

    @Override
    public int getCount() {
        /*
         * Total count for the list view is the number groups plus the number of
         * children from currently expanded groups (a value we keep cached in
         * this class)
         */
        return mExpandableListAdapter.getGroupCount() + mTotalExpChildrenCount;
    }

    ArrayList<GroupMetadata> getExpandedGroupMetadataList() {
        return mExpGroupMetadataList;
    }

    @Override
    public Filter getFilter() {
        ExpandableListAdapter adapter = getAdapter();
        if (adapter instanceof Filterable) {
            return ((Filterable) adapter).getFilter();
        } else {
            return null;
        }
    }

    PositionMetadata getFlattenedPos(final ExpandableListPosition pos) {
        final ArrayList<GroupMetadata> egml = mExpGroupMetadataList;
        final int numExpGroups = egml.size();
        int leftExpGroupIndex = 0;
        int rightExpGroupIndex = numExpGroups - 1;
        int midExpGroupIndex = 0;
        GroupMetadata midExpGm;
        if (numExpGroups == 0) {
            return PositionMetadata.obtain(pos.groupPos, pos.type,
                    pos.groupPos, pos.childPos, null, 0);
        }
        while (leftExpGroupIndex <= rightExpGroupIndex) {
            midExpGroupIndex = (rightExpGroupIndex - leftExpGroupIndex) / 2 + leftExpGroupIndex;
            midExpGm = egml.get(midExpGroupIndex);

            if (pos.groupPos > midExpGm.gPos) {
                /*
                 * It's after the current middle group, so search right
                 */
                leftExpGroupIndex = midExpGroupIndex + 1;
            } else if (pos.groupPos < midExpGm.gPos) {
                /*
                 * It's before the current middle group, so search left
                 */
                rightExpGroupIndex = midExpGroupIndex - 1;
            } else if (pos.groupPos == midExpGm.gPos) {
                /*
                 * It's this middle group, exact hit
                 */

                if (pos.type == ExpandableListPosition.GROUP) {
                    /* If it's a group, give them this matched group's flPos */
                    return PositionMetadata.obtain(midExpGm.flPos, pos.type,
                            pos.groupPos, pos.childPos, midExpGm, midExpGroupIndex);
                } else if (pos.type == ExpandableListPosition.CHILD) {
                    /* If it's a child, calculate the flat list pos */
                    return PositionMetadata.obtain(midExpGm.flPos + pos.childPos
                            + 1, pos.type, pos.groupPos, pos.childPos,
                            midExpGm, midExpGroupIndex);
                } else {
                    return null;
                }
            }
        }

        /*
         * If we've reached here, it means there was no match in the expanded
         * groups, so it must be a collapsed group that they're search for
         */
        if (pos.type != ExpandableListPosition.GROUP) {
            /* If it isn't a group, return null */
            return null;
        }

        /*
         * To figure out exact insertion and prior group positions, we need to
         * determine how we broke out of the binary search. We backtrack to see
         * this.
         */
        if (leftExpGroupIndex > midExpGroupIndex) {

            /*
             * This would occur in the first conditional, so the flat list
             * insertion position is after the left group. The leftGroupPos is
             * one more than it should be (from the binary search loop) so we
             * subtract 1 to get the actual left group. Since the insertion
             * point is AFTER the left group, we keep this +1 value as the
             * insertion point
             */
            final GroupMetadata leftExpGm = egml.get(leftExpGroupIndex - 1);
            final int flPos =
                    leftExpGm.lastChildFlPos
                            + pos.groupPos - leftExpGm.gPos;

            return PositionMetadata.obtain(flPos, pos.type, pos.groupPos,
                    pos.childPos, null, leftExpGroupIndex);
        } else if (rightExpGroupIndex < midExpGroupIndex) {

            /*
             * This would occur in the second conditional, so the flat list
             * insertion position is before the right group. Also, the
             * rightGroupPos is one less than it should be (from binary search
             * loop), so we increment to it.
             */
            final GroupMetadata rightExpGm = egml.get(++rightExpGroupIndex);
            final int flPos =
                    rightExpGm.flPos
                            - (rightExpGm.gPos - pos.groupPos);
            return PositionMetadata.obtain(flPos, pos.type, pos.groupPos,
                    pos.childPos, null, rightExpGroupIndex);
        } else {
            return null;
        }
    }

    @Override
    public Object getItem(int flatListPos) {
        final PositionMetadata posMetadata = getUnflattenedPos(flatListPos);

        Object retValue;
        if (posMetadata.position.type == ExpandableListPosition.GROUP) {
            retValue = mExpandableListAdapter
                    .getGroup(posMetadata.position.groupPos);
        } else if (posMetadata.position.type == ExpandableListPosition.CHILD) {
            retValue = mExpandableListAdapter.getChild(posMetadata.position.groupPos,
                    posMetadata.position.childPos);
        } else {
            // TODO: clean exit
            throw new RuntimeException("Flat list position is of unknown type");
        }

        posMetadata.recycle();

        return retValue;
    }

    @Override
    public long getItemId(int flatListPos) {
        final PositionMetadata posMetadata = getUnflattenedPos(flatListPos);
        final long groupId = mExpandableListAdapter.getGroupId(posMetadata.position.groupPos);

        long retValue;
        if (posMetadata.position.type == ExpandableListPosition.GROUP) {
            retValue = mExpandableListAdapter.getCombinedGroupId(groupId);
        } else if (posMetadata.position.type == ExpandableListPosition.CHILD) {
            final long childId = mExpandableListAdapter.getChildId(posMetadata.position.groupPos,
                    posMetadata.position.childPos);
            retValue = mExpandableListAdapter.getCombinedChildId(groupId, childId);
        } else {
            // TODO: clean exit
            throw new RuntimeException("Flat list position is of unknown type");
        }

        posMetadata.recycle();

        return retValue;
    }

    @Override
    public int getItemViewType(int flatListPos) {
        final PositionMetadata metadata = getUnflattenedPos(flatListPos);
        final ExpandableListPosition pos = metadata.position;

        int retValue;
        if (mExpandableListAdapter instanceof HeterogeneousExpandableList) {
            HeterogeneousExpandableList adapter =
                    (HeterogeneousExpandableList) mExpandableListAdapter;
            if (pos.type == ExpandableListPosition.GROUP) {
                retValue = adapter.getGroupType(pos.groupPos);
            } else {
                final int childType = adapter.getChildType(pos.groupPos, pos.childPos);
                retValue = adapter.getGroupTypeCount() + childType;
            }
        } else {
            if (pos.type == ExpandableListPosition.GROUP) {
                retValue = 0;
            } else {
                retValue = 1;
            }
        }

        metadata.recycle();

        return retValue;
    }

    PositionMetadata getUnflattenedPos(final int flPos) {
        final ArrayList<GroupMetadata> egml = mExpGroupMetadataList;
        final int numExpGroups = egml.size();
        int leftExpGroupIndex = 0;
        int rightExpGroupIndex = numExpGroups - 1;
        int midExpGroupIndex = 0;
        GroupMetadata midExpGm;
        if (numExpGroups == 0) {
            return PositionMetadata.obtain(flPos, ExpandableListPosition.GROUP, flPos,
                    -1, null, 0);
        }
        while (leftExpGroupIndex <= rightExpGroupIndex) {
            midExpGroupIndex =
                    (rightExpGroupIndex - leftExpGroupIndex) / 2
                            + leftExpGroupIndex;
            midExpGm = egml.get(midExpGroupIndex);
            if (flPos > midExpGm.lastChildFlPos) {
                leftExpGroupIndex = midExpGroupIndex + 1;
            } else if (flPos < midExpGm.flPos) {
                rightExpGroupIndex = midExpGroupIndex - 1;
            } else if (flPos == midExpGm.flPos) {
                return PositionMetadata.obtain(flPos, ExpandableListPosition.GROUP,
                        midExpGm.gPos, -1, midExpGm, midExpGroupIndex);
            } else if (flPos <= midExpGm.lastChildFlPos) {
                final int childPos = flPos - (midExpGm.flPos + 1);
                return PositionMetadata.obtain(flPos, ExpandableListPosition.CHILD,
                        midExpGm.gPos, childPos, midExpGm, midExpGroupIndex);
            }
        }

        int insertPosition = 0;
        int groupPos = 0;
        if (leftExpGroupIndex > midExpGroupIndex) {
            final GroupMetadata leftExpGm = egml.get(leftExpGroupIndex - 1);
            insertPosition = leftExpGroupIndex;
            groupPos =
                    flPos - leftExpGm.lastChildFlPos + leftExpGm.gPos;
        } else if (rightExpGroupIndex < midExpGroupIndex) {
            final GroupMetadata rightExpGm = egml.get(++rightExpGroupIndex);
            insertPosition = rightExpGroupIndex;
            groupPos = rightExpGm.gPos - (rightExpGm.flPos - flPos);
        } else {
            throw new RuntimeException("Unknown state");
        }
        return PositionMetadata.obtain(flPos, ExpandableListPosition.GROUP, groupPos, -1,
                null, insertPosition);
    }

    @Override
    public View getView(int flatListPos, View convertView, ViewGroup parent) {
        final PositionMetadata posMetadata = getUnflattenedPos(flatListPos);

        View retValue;
        if (posMetadata.position.type == ExpandableListPosition.GROUP) {
            retValue = mExpandableListAdapter.getGroupView(posMetadata.position.groupPos,
                    posMetadata.isExpanded(), convertView, parent);
        } else if (posMetadata.position.type == ExpandableListPosition.CHILD) {
            final boolean isLastChild = posMetadata.groupMetadata.lastChildFlPos == flatListPos;

            retValue = mExpandableListAdapter.getChildView(posMetadata.position.groupPos,
                    posMetadata.position.childPos, isLastChild, convertView, parent);
        } else {
            // TODO: clean exit
            throw new RuntimeException("Flat list position is of unknown type");
        }

        posMetadata.recycle();

        return retValue;
    }

    @Override
    public int getViewTypeCount() {
        if (mExpandableListAdapter instanceof HeterogeneousExpandableList) {
            HeterogeneousExpandableList adapter =
                    (HeterogeneousExpandableList) mExpandableListAdapter;
            return adapter.getGroupTypeCount() + adapter.getChildTypeCount();
        } else {
            return 2;
        }
    }

    @Override
    public boolean hasStableIds() {
        return mExpandableListAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        ExpandableListAdapter adapter = getAdapter();
        return adapter != null ? adapter.isEmpty() : true;
    }

    @Override
    public boolean isEnabled(int flatListPos) {
        final PositionMetadata metadata = getUnflattenedPos(flatListPos);
        final ExpandableListPosition pos = metadata.position;

        boolean retValue;
        if (pos.type == ExpandableListPosition.CHILD) {
            retValue = mExpandableListAdapter.isChildSelectable(pos.groupPos, pos.childPos);
        } else {
            // Groups are always selectable
            retValue = true;
        }

        metadata.recycle();

        return retValue;
    }

    /**
     * Whether the given group is currently expanded.
     * 
     * @param groupPosition The group to check.
     * @return Whether the group is currently expanded.
     */
    public boolean isGroupExpanded(int groupPosition) {
        GroupMetadata groupMetadata;
        for (int i = mExpGroupMetadataList.size() - 1; i >= 0; i--) {
            groupMetadata = mExpGroupMetadataList.get(i);

            if (groupMetadata.gPos == groupPosition) {
                return true;
            }
        }

        return false;
    }

    /**
     * Traverses the expanded group metadata list and fills in the flat list
     * positions.
     * 
     * @param forceChildrenCountRefresh Forces refreshing of the children count
     *            for all expanded groups.
     * @param syncGroupPositions Whether to search for the group positions based
     *            on the group IDs. This should only be needed when calling this
     *            from an onChanged callback.
     */
    @SuppressWarnings("unchecked")
    private void refreshExpGroupMetadataList(boolean forceChildrenCountRefresh,
            boolean syncGroupPositions) {
        final ArrayList<GroupMetadata> egml = mExpGroupMetadataList;
        int egmlSize = egml.size();
        int curFlPos = 0;

        /* Update child count as we go through */
        mTotalExpChildrenCount = 0;

        if (syncGroupPositions) {
            // We need to check whether any groups have moved positions
            boolean positionsChanged = false;

            for (int i = egmlSize - 1; i >= 0; i--) {
                GroupMetadata curGm = egml.get(i);
                int newGPos = findGroupPosition(curGm.gId, curGm.gPos);
                if (newGPos != curGm.gPos) {
                    if (newGPos == AdapterView.INVALID_POSITION) {
                        // Doh, just remove it from the list of expanded groups
                        egml.remove(i);
                        egmlSize--;
                    }

                    curGm.gPos = newGPos;
                    if (!positionsChanged) {
                        positionsChanged = true;
                    }
                }
            }

            if (positionsChanged) {
                // At least one group changed positions, so re-sort
                Collections.sort(egml);
            }
        }

        int gChildrenCount;
        int lastGPos = 0;
        for (int i = 0; i < egmlSize; i++) {
            /* Store in local variable since we'll access freq */
            GroupMetadata curGm = egml.get(i);

            /*
             * Get the number of children, try to refrain from calling another
             * class's method unless we have to (so do a subtraction)
             */
            if (curGm.lastChildFlPos == GroupMetadata.REFRESH || forceChildrenCountRefresh) {
                gChildrenCount = mExpandableListAdapter.getChildrenCount(curGm.gPos);
            } else {
                /*
                 * Num children for this group is its last child's fl pos minus
                 * the group's fl pos
                 */
                gChildrenCount = curGm.lastChildFlPos - curGm.flPos;
            }

            /* Update */
            mTotalExpChildrenCount += gChildrenCount;

            /*
             * This skips the collapsed groups and increments the flat list
             * position (for subsequent exp groups) by accounting for the
             * collapsed groups
             */
            curFlPos += curGm.gPos - lastGPos;
            lastGPos = curGm.gPos;

            /* Update the flat list positions, and the current flat list pos */
            curGm.flPos = curFlPos;
            curFlPos += gChildrenCount;
            curGm.lastChildFlPos = curFlPos;
        }
    }

    public void setExpandableListAdapter(ExpandableListAdapter expandableListAdapter) {
        if (mExpandableListAdapter != null) {
            mExpandableListAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mExpandableListAdapter = expandableListAdapter;
        expandableListAdapter.registerDataSetObserver(mDataSetObserver);
    }

    void setExpandedGroupMetadataList(ArrayList<GroupMetadata> expandedGroupMetadataList) {

        if (expandedGroupMetadataList == null || mExpandableListAdapter == null) {
            return;
        }

        // Make sure our current data set is big enough for the previously
        // expanded groups, if not, ignore this request
        int numGroups = mExpandableListAdapter.getGroupCount();
        for (int i = expandedGroupMetadataList.size() - 1; i >= 0; i--) {
            if (expandedGroupMetadataList.get(i).gPos >= numGroups) {
                // Doh, for some reason the client doesn't have some of the
                // groups
                return;
            }
        }

        mExpGroupMetadataList = expandedGroupMetadataList;
        refreshExpGroupMetadataList(true, false);
    }

    /**
     * Set the maximum number of groups that can be expanded at any given time
     */
    public void setMaxExpGroupCount(int maxExpGroupCount) {
        mMaxExpGroupCount = maxExpGroupCount;
    }
}
