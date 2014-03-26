package org.holoeverywhere.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ListView;

final class SliderMenuAdapter extends BaseAdapter implements IAdapter<ListView>, AdapterView.OnItemClickListener {
    private final int mDefaultTextAppearance;
    private final int mDefaultTextAppearanceInverse;
    private final LayoutInflater mLayoutInflater;
    private final SliderMenu mMenu;

    SliderMenuAdapter(Context context, SliderMenu menu) {
        mMenu = menu;
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
    public int getCount() {
        return mMenu.mItems.size();
    }

    @Override
    public SliderItem getItem(int position) {
        return mMenu.mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).mCustomLayout != 0 ? position
                : Adapter.IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SliderItem item = getItem(position);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(item.mCustomLayout != 0 ? item.mCustomLayout
                    : R.layout.slider_menu_item, parent, false);
        }
        return mMenu.bindView(item, convertView, position == mMenu.mCurrentPage, mDefaultTextAppearance, mDefaultTextAppearanceInverse);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMenu.setCurrentPage(position, false, true);
    }

    @Override
    public void bind(ListView listView) {
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }
}
