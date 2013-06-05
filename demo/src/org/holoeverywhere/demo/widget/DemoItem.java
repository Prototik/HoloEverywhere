
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.demo.R;

import android.view.View;
import android.view.ViewGroup;

public class DemoItem {
    public CharSequence label;
    public View lastView;
    public boolean longClickable = false;
    public int selectionHandlerColor = -1;
    public boolean selectionHandlerVisible = false;

    public DemoItem() {

    }

    public int getItemViewType() {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DemoListRowView view = makeView(convertView, parent);
        view.setLabel(label);
        view.setSelectionHandlerVisiblity(selectionHandlerVisible);
        if (selectionHandlerVisible) {
            if (selectionHandlerColor > 0) {
                view.setSelectionHandlerColor(selectionHandlerColor);
            } else {
                view.setSelectionHandlerColorResource(R.color.transparent);
            }
        }
        return view;
    }

    protected DemoListRowView makeView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            return FontLoader.applyDefaultFont(new DemoListRowView(parent.getContext()));
        } else {
            return (DemoListRowView) convertView;
        }
    }

    public void onClick() {

    }

    public boolean onLongClick() {
        return false;
    }
}
