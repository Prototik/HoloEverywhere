
package org.holoeverywhere.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class ListAdapterWrapper implements WrapperListAdapter {
    private final ListAdapterCallback mCallback;
    private final ListAdapter mWrapped;
    private DataSetObserver mLastDataSetObserver;
    private android.widget.AdapterView<ListAdapter> mAdapterView;

    public ListAdapterWrapper(ListAdapter wrapped) {
        this(wrapped, null);
    }

    public ListAdapterWrapper(ListAdapter wrapped, ListAdapterCallback callback) {
        mWrapped = wrapped;
        mCallback = callback;
    }

    public AdapterView<ListAdapter> getAdapterView() {
        return mAdapterView;
    }

    public void setAdapterView(android.widget.AdapterView<ListAdapter> adapterView) {
        mAdapterView = adapterView;
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

    @Override
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
        if (mCallback != null) {
            return mCallback.onPrepareView(view, position);
        }
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mWrapped.registerDataSetObserver(mLastDataSetObserver = new WrapperDataSetObserver(
                dataSetObserver));
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mWrapped.unregisterDataSetObserver(mLastDataSetObserver);
        mLastDataSetObserver = null;
    }

    public static interface ListAdapterCallback {
        public void onChanged();

        public void onInvalidated();

        public View onPrepareView(View view, int position);
    }

    private final class WrapperDataSetObserver extends DataSetObserver {
        private DataSetObserver mDataSetObserver;

        public WrapperDataSetObserver(DataSetObserver dataSetObserver) {
            mDataSetObserver = dataSetObserver;
        }

        @Override
        public void onChanged() {
            mDataSetObserver.onChanged();
            if (mCallback != null) {
                mCallback.onChanged();
            }
        }

        @Override
        public void onInvalidated() {
            mDataSetObserver.onInvalidated();
            if (mCallback != null) {
                mCallback.onInvalidated();
            }
        }
    }
}
