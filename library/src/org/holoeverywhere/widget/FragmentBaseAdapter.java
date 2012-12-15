
package org.holoeverywhere.widget;

import org.holoeverywhere.R;
import org.holoeverywhere.widget.AdapterView.OnItemClickListener;
import org.holoeverywhere.widget.AdapterView.OnItemLongClickListener;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Pager.PagerAdapter;
import org.holoeverywhere.widget.Pager.PagerDataSetObserver;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public abstract class FragmentBaseAdapter extends BaseAdapter implements PagerAdapter {
    public static final class FragmentInfo {
        public final Fragment fragment;
        public final int position;

        protected FragmentInfo(Fragment fragment, int position) {
            this.fragment = fragment;
            this.position = position;
        }
    }

    private static final String makeTagName(int container, int position) {
        return "fragmentbaseadapter:" + container + ":" + position;
    }

    private final FragmentManager fm;
    private PagerDataSetObserver pagerDataSetObserver;
    private int selection = -1;

    public FragmentBaseAdapter(FragmentManager fm) {
        this.fm = fm;
    }

    public boolean alwaysRefreshFragment() {
        return false;
    }

    private void attachFragment(Fragment fragment, String tag, FragmentTransaction ft) {
        if (fragment == null || tag == null) {
            return;
        }
        if (!fragment.isAdded()) {
            ft.add(fragment, tag);
        }
        if (fragment.isDetached()) {
            ft.attach(fragment);
        }
    }

    private FragmentTransaction beginTransaction() {
        return fm.beginTransaction();
    }

    public void bind(AdapterView<ListAdapter> adapterView) {
        if (adapterView == null) {
            return;
        }
        adapterView.setAdapter(this);
        if (this instanceof OnItemClickListener) {
            adapterView.setOnItemClickListener((OnItemClickListener) this);
        }
        if (this instanceof OnItemLongClickListener) {
            adapterView.setOnItemLongClickListener((OnItemLongClickListener) this);
        }
        if (this instanceof OnItemSelectedListener) {
            adapterView.setOnItemSelectedListener((OnItemSelectedListener) this);
        }
    }

    public boolean fullDetachFragment() {
        return false;
    }

    public Iterable<View> cached() {
        return pagerDataSetObserver == null ? null : pagerDataSetObserver.cached();
    }

    private void checkState(Fragment fragment, int position) {
        boolean visible = selection == position;
        fragment.setUserVisibleHint(visible);
        fragment.setMenuVisibility(visible);
    }

    private void checkState(FragmentInfo info) {
        if (info != null && info.fragment != null) {
            checkState(info.fragment, info.position);
        }
    }

    private void commitTransaction(FragmentTransaction ft) {
        if (ft == null || ft.isEmpty()) {
            return;
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    private void detachFragment(Fragment fragment, FragmentTransaction ft) {
        if (fragment == null) {
            return;
        }
        if (!fragment.isDetached()) {
            ft.detach(fragment);
        }
        if (fullDetachFragment()) {
            if (fragment.isAdded()) {
                ft.remove(fragment);
            }
        }
    }

    @Override
    public int getCachePageCount() {
        return 1;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        return getView(position, view, parent);
    }

    public FragmentInfo getFragmentInfo(View view) {
        return view == null ? null : (FragmentInfo) view.getTag(PAGER_TAG_ID);
    }

    @Override
    public abstract Fragment getItem(int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        final String tag = makeTagName(container.getId(), position);
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null || alwaysRefreshFragment()) {
            fragment = getItem(position);
            checkState(fragment, position);
        }
        final FragmentTransaction ft = beginTransaction();
        if (view == null) {
            if (fragment.isDetached() || !fragment.isAdded()) {
                attachFragment(fragment, tag, ft);
            }
            commitTransaction(ft);
            view = fragment.getView();
        } else {
            FragmentInfo info = getFragmentInfo(view);
            if (info.fragment != fragment || info.position != position) {
                if (info.fragment != null) {
                    detachFragment(info.fragment, ft);
                }
                attachFragment(fragment, tag, ft);
                commitTransaction(ft);
                view = fragment.getView();
            }
        }
        view.setTag(PAGER_TAG_ID, makeInfo(fragment, position));
        return view;
    }

    private static final int PAGER_TAG_ID = R.id.pagerTag;

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    private FragmentInfo makeInfo(Fragment fragment, int position) {
        return new FragmentInfo(fragment, position);
    }

    @Override
    public void onRecycleView(View child) {
        FragmentInfo info = getFragmentInfo(child);
        if (info != null && info.fragment != null) {
            FragmentTransaction ft = beginTransaction();
            detachFragment(info.fragment, ft);
            commitTransaction(ft);
        }
    }

    @Override
    public void onSelectionChanged(int selection) {
        if (this.selection != selection) {
            this.selection = selection;
            for (View view : cached()) {
                checkState(getFragmentInfo(view));
            }
        }
    }

    public void post(Runnable runnable) {
        if (pagerDataSetObserver != null) {
            pagerDataSetObserver.post(runnable);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        pagerDataSetObserver = observer instanceof PagerDataSetObserver ? (PagerDataSetObserver) observer
                : null;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        pagerDataSetObserver = null;
    }

    public Iterable<View> visible() {
        return pagerDataSetObserver == null ? null : pagerDataSetObserver.visible();
    }
}
