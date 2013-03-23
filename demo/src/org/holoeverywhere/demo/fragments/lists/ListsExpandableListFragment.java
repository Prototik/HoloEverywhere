
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class ListsExpandableListFragment extends Fragment {
    public class Adapter extends BaseExpandableListAdapter {
        @Override
        public String getChild(int groupPosition, int childPosition) {
            return CHILDRENS[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CHILDRENS[groupPosition].length;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            View view = LayoutInflater.inflate(getSupportActivity(),
                    R.layout.expandable_list_row_child);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getChild(groupPosition, childPosition).toString());
            return view;
        }

        @Override
        public String getGroup(int groupPosition) {
            return GROUPS[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return GROUPS.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            View view = LayoutInflater.inflate(getSupportActivity(),
                    R.layout.expandable_list_row_group);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getGroup(groupPosition).toString());
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private static final String[][] CHILDRENS = {
            {
                    "Arnold", "Barry", "Chuck", "David"
            },
            {
                    "Ace", "Bandit", "Cha-Cha", "Deuce"
            },
            {
                    "Fluffy", "Snuggles"
            },
            {
                    "Goldy", "Bubbles"
            }
    };
    private static final String[] GROUPS = {
            "People Names", "Dog Names", "Cat Names", "Fish Names"
    };

    private ExpandableListView mExpandableListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.expandable_list_content);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setSubtitle("Lists: Expandable list");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExpandableListView = (ExpandableListView) view.findViewById(android.R.id.list);
        mExpandableListView.setAdapter(new Adapter());
    }
}
