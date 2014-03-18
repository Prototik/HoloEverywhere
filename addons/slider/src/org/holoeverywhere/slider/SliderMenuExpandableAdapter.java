package org.holoeverywhere.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;

final class SliderMenuExpandableAdapter extends BaseExpandableListAdapter implements IAdapter<ExpandableListView>,
        SliderMenu.OnPageChangeListener,
        ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener {
    private final int mDefaultTextAppearance;
    private final int mDefaultTextAppearanceInverse;
    private final LayoutInflater mLayoutInflater;
    private final SliderMenu mMenu;
    private ExpandableListView mListView;
    private int mSelectedPage = 0, mHighlightedPage;
    private int[] mDecodedPageTemp = new int[2];

    SliderMenuExpandableAdapter(Context context, SliderMenu menu) {
        mMenu = menu;
        mMenu.internalSetOnPageChangeListener(this);

        mLayoutInflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(R.styleable.SliderMenu);
        mDefaultTextAppearance = a.getResourceId(
                R.styleable.SliderMenu_textAppearanceSliderItem,
                R.style.Holo_TextAppearance_Medium);
        mDefaultTextAppearanceInverse = a.getResourceId(
                R.styleable.SliderMenu_textAppearanceSliderItemInverse,
                R.style.Holo_TextAppearance_Medium_Inverse);
        a.recycle();
    }

    @Override
    public int getGroupCount() {
        return mMenu.mItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).mSubItems.size();
    }

    @Override
    public SliderItem getGroup(int groupPosition) {
        return mMenu.mItems.get(groupPosition);
    }

    @Override
    public SliderSubItem getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).mSubItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (groupPosition << 4) | childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final SliderItem item = getGroup(groupPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(item.mCustomLayout != 0 ? item.mCustomLayout
                    : R.layout.slider_menu_expandable_item, parent, false);
        }
        SliderMenuGroupIndicatorView groupIndicator = (SliderMenuGroupIndicatorView) convertView.findViewById(R.id.slider_menu_group_indicator);
        if (groupIndicator != null) {
            groupIndicator.setExpanded(isExpanded);
        }

        mMenu.decodePage(mHighlightedPage, mDecodedPageTemp);
        final boolean selected = groupPosition == mDecodedPageTemp[0];
        return mMenu.bindView(item, convertView, selected, mDefaultTextAppearance, mDefaultTextAppearanceInverse);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final SliderSubItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(item.mCustomLayout != 0 ? item.mCustomLayout
                    : R.layout.slider_menu_expandable_child_item, parent, false);
        }
        final int encodedPage = mMenu.encodePage(groupPosition, childPosition);
        final boolean selected = encodedPage == mSelectedPage;
        return mMenu.bindView(item, convertView, selected, mDefaultTextAppearance, mDefaultTextAppearanceInverse);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).isSelectable();
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        final SliderItem item = getGroup(groupPosition);
        final boolean hasSubItems = item.hasSubItems(), hasVisiblePage = item.hasVisiblePage();
        // Some logical magic...
        if (hasVisiblePage || !hasSubItems) {
            mMenu.setCurrentPage(mMenu.encodePage(groupPosition, hasSubItems || !hasVisiblePage ? 0 : SliderMenu.CHILD_INVALID), hasSubItems, !hasVisiblePage || !hasSubItems);
        } else {
            mHighlightedPage = mMenu.encodePage(groupPosition, SliderMenu.CHILD_INVALID);
            notifyDataSetInvalidated();
            return false;
        }
        return hasVisiblePage;
    }

    private int mLastExpandedGroup = -1;

    @Override
    public void onGroupExpand(int groupPosition) {
        if (mLastExpandedGroup != -1 && mLastExpandedGroup != groupPosition && mMenu.flag(SliderMenu.FLAG_EXPAND_ONE_GROUP)) {
            mListView.collapseGroup(mLastExpandedGroup);
        }
        mLastExpandedGroup = groupPosition;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        mMenu.setCurrentPage(mMenu.encodePage(groupPosition, childPosition), false, true);
        return true;
    }

    @Override
    public void bind(ExpandableListView listView) {
        mListView = listView;
        listView.setAdapter(this);
        listView.setOnGroupClickListener(this);
        listView.setOnGroupExpandListener(this);
        listView.setOnChildClickListener(this);
    }

    @Override
    public void onPageChange(int lastPage, int currentPage) {
        mSelectedPage = mHighlightedPage = currentPage;
    }
}
