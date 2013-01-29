
package org.holoeverywhere.bug.i288;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.HeterogeneousExpandableList;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
    public class MyExpandableListAdapter extends BaseExpandableListAdapter implements
            HeterogeneousExpandableList {
        private String[][] children = {
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
        private String[] groups = {
                "People Names", "Dog Names", "Cat Names", "Fish Names"
        };

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            View view = getGenericView();
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getChild(groupPosition, childPosition).toString());
            return view;
        }

        public View getGenericView() {
            return LayoutInflater.inflate(MainActivity.this, R.layout.row);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            View view = getGenericView();
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

    private ExpandableListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        list = (ExpandableListView) findViewById(R.id.list);
        list.setAdapter(new MyExpandableListAdapter());
    }
}
