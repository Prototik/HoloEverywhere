package org.holoeverywhere.slider;

import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

interface IAdapter<T extends ListView> {
    void notifyDataSetInvalidated();

    void notifyDataSetChanged();

    void bind(T listView);
}
