
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.GridFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseOtherFragment extends GridFragment {
    private static final class OtherAdapter extends DemoAdapter {
        public OtherAdapter(Context context) {
            super(context);
        }
    }

    public static interface OnOtherItemClickListener {
        public void onClick(OtherItem otherItem);
    }

    private static final class OtherItem extends DemoItem {
        public OnOtherItemClickListener listener;

        @Override
        public void onClick() {
            if (listener != null) {
                listener.onClick(this);
            }
        }
    }

    private OtherAdapter mAdapter;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mAdapter = new OtherAdapter(getSupportActivity());
        onHandleData();
        setGridAdapter(mAdapter);
        getGridView().setOnItemClickListener(mAdapter);
    }

    public void addItem(DemoItem item) {
        mAdapter.add(item);
    }

    public void addItem(CharSequence label, OnOtherItemClickListener listener) {
        OtherItem item = new OtherItem();
        item.label = label;
        item.listener = listener;
        addItem(item);
    }

    public abstract void onHandleData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.other, container, false);
    }
}
