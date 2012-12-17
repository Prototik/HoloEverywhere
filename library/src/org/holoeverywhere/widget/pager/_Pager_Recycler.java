
package org.holoeverywhere.widget.pager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.holoeverywhere.widget.Pager;

import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;

public final class _Pager_Recycler {
    private final class RecyclerIterable implements Iterable<View> {
        private int[] pages = new int[2];

        private int position;

        private ViewType type;

        public RecyclerIterable(ViewType type) {
            this.type = type;
        }

        @Override
        public Iterator<View> iterator() {
            if (type == ViewType.Visible) {
                pager.getPagerScroller().computePages(pages);
                position = pages[0];
            } else if (type == ViewType.Cached) {
                pager.fakeLayout();
                position = 0;
            }
            return new Iterator<View>() {
                @Override
                public boolean hasNext() {
                    switch (type) {
                        case Visible:
                            final Adapter adapter = pager.getAdapter();
                            if (adapter == null) {
                                return false;
                            }
                            return position <= pages[1] && position > 0
                                    && position < adapter.getCount();
                        case Cached:
                            return position < mScrapHeap.size();
                    }
                    return false;
                }

                @Override
                public View next() {
                    switch (type) {
                        case Visible:
                        case Cached:
                            return get(position++);
                    }
                    return null;
                }

                @Override
                public void remove() {
                    throw new RuntimeException("Remove don't support");
                }
            };
        }
    }

    public static enum ViewType {
        Cached, Visible
    }

    private final Set<Integer> mDeleteHeap = new HashSet<Integer>();

    private final SparseArray<View> mScrapHeap = new SparseArray<View>();

    private Pager pager;

    public _Pager_Recycler(Pager pager) {
        this.pager = pager;
    }

    public synchronized Iterable<View> cached() {
        return new RecyclerIterable(ViewType.Cached);
    }

    public synchronized void clear() {
        for (int i = 0; i < mScrapHeap.size(); i++) {
            final int key = mScrapHeap.keyAt(i);
            if (!mDeleteHeap.contains(key)) {
                final View view = mScrapHeap.valueAt(i--);
                mScrapHeap.remove(key);
                if (view != null) {
                    pager.removeDetachedView(view, true);
                    pager.removeViewInLayout(view);
                }
            }
        }
        mDeleteHeap.clear();
    }

    public synchronized void fullClear() {
        mDeleteHeap.clear();
        for (int i = 0; i < mScrapHeap.size(); i++) {
            final View view = mScrapHeap.valueAt(i);
            if (view != null) {
                pager.removeDetachedView(view, true);
                pager.removeViewInLayout(view);
            }
        }
        mScrapHeap.clear();
    }

    public synchronized View get(int position) {
        return mScrapHeap.get(position);
    }

    public synchronized void markAsSave(int position) {
        if (!mDeleteHeap.contains(position)) {
            mDeleteHeap.add(position);
        }
    }

    public synchronized void prepareForClear() {
        mDeleteHeap.clear();
    }

    public synchronized void put(int position, View v) {
        mScrapHeap.put(position, v);
    }

    public synchronized Iterable<View> visible() {
        return new RecyclerIterable(ViewType.Visible);
    }
}
