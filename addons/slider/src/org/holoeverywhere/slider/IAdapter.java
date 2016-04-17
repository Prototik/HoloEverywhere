package org.holoeverywhere.slider;

import org.holoeverywhere.widget.ListView;

interface IAdapter<T extends ListView, B> {
    void notifyDataSetInvalidated();

    void notifyDataSetChanged();

    void bind(T listView);
    
	void setOnItemChildClickListener(B listener);
}
