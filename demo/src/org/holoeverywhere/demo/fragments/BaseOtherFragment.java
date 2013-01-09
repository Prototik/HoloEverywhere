
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;

import android.content.Context;
import android.view.View;

public abstract class BaseOtherFragment extends ListFragment {
    private final class FragmentListener implements OnOtherItemClickListener {
        private Class<? extends Fragment> mClass;

        public FragmentListener(Class<? extends Fragment> clazz) {
            mClass = clazz;
        }

        @Override
        public void onClick(OtherItem otherItem) {
            Fragment fragment = Fragment.instantiate(mClass);
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).show(getSupportActivity());
            } else {
                ((DemoActivity) getSupportActivity()).replaceFragment(fragment,
                        "fragment-" + mClass.getName());
            }
        }
    }

    public static interface OnOtherItemClickListener {
        public void onClick(OtherItem otherItem);
    }

    private static final class OtherAdapter extends DemoAdapter {
        public OtherAdapter(Context context) {
            super(context);
        }
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

    public void addItem(CharSequence label, Class<? extends Fragment> clazz) {
        addItem(label, new FragmentListener(clazz));
    }

    public void addItem(CharSequence label, OnOtherItemClickListener listener) {
        OtherItem item = new OtherItem();
        item.label = label;
        item.listener = listener;
        addItem(item);
    }

    public void addItem(DemoItem item) {
        mAdapter.add(item);
    }

    public abstract void onHandleData();

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mAdapter = new OtherAdapter(getSupportActivity());
        onHandleData();
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(mAdapter);
    }
}
