
package org.holoeverywhere.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Wrapper for ListAdapter I know about android.widget.WrapperListAdapter, but
 * maven don't want compile it :(
 * 
 * @author prok
 */
public class ListAdapterWrapper implements ListAdapter {
    public static interface OnPrepareViewListener {
        public View onPrepareView(View view, int position);
    }

    private DataSetObserver mLastDataSetObserver;
    private final OnPrepareViewListener mListener;
    private final ListAdapter mWrapped;

    public ListAdapterWrapper(ListAdapter wrapped) {
        this(wrapped, null);
    }

    public ListAdapterWrapper(ListAdapter wrapped, OnPrepareViewListener listener) {
        mWrapped = wrapped;
        mListener = listener;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mWrapped.areAllItemsEnabled();
    }

    @Override
    public int getCount() {
        return mWrapped.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mWrapped.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mWrapped.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mWrapped.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return onPrepareView(mWrapped.getView(position, convertView, parent), position);
    }

    @Override
    public int getViewTypeCount() {
        return mWrapped.getViewTypeCount();
    }

    public ListAdapter getWrappedAdapter() {
        return mWrapped;
    }

    @Override
    public boolean hasStableIds() {
        return mWrapped.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mWrapped.isEmpty();
    }

    @Override
    public boolean isEnabled(int position) {
        return mWrapped.isEnabled(position);
    }

    public void notifyDataSetChanged() {
        if (mLastDataSetObserver != null) {
            mLastDataSetObserver.onChanged();
        }
    }

    public View onPrepareView(View view, int position) {
        if (mListener != null) {
            return mListener.onPrepareView(view, position);
        }
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mWrapped.registerDataSetObserver(mLastDataSetObserver = dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mLastDataSetObserver = null;
        mWrapped.unregisterDataSetObserver(dataSetObserver);
    }
}
