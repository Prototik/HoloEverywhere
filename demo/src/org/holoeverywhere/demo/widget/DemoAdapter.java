
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class DemoAdapter extends ArrayAdapter<DemoItem> implements OnItemClickListener,
        OnItemLongClickListener {
    public DemoAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(position, convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        DemoItem item = getItem(position - ((ListView) adapterView).getHeaderViewsCount());
        item.lastView = view;
        item.onClick();
        item.lastView = null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        DemoItem item = getItem(position - ((ListView) adapterView).getHeaderViewsCount());
        if (item.longClickable) {
            item.lastView = view;
            boolean result = item.onLongClick();
            item.lastView = null;
            return result;
        } else {
            return false;
        }
    }
}
