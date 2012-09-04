package com.WazaBe.HoloEverywhere.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.WazaBe.HoloEverywhere.R;

public class ListActivity extends Activity {
	protected ListAdapter mAdapter;
	protected ListView mList;

	private Handler mHandler = new Handler();
	private boolean mFinishedStart = false;

	private Runnable mRequestFocus = new Runnable() {
		public void run() {
			mList.focusableViewAvailable(mList);
		}
	};

	protected void onListItemClick(ListView l, View v, int position, long id) {
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		ensureList();
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(mRequestFocus);
		super.onDestroy();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		View emptyView = findViewById(android.R.id.empty);
		mList = (ListView) findViewById(android.R.id.list);
		if (mList == null) {
			throw new RuntimeException(
					"Your content must have a ListView whose id attribute is "
							+ "'android.R.id.list'");
		}
		if (emptyView != null) {
			mList.setEmptyView(emptyView);
		}
		mList.setOnItemClickListener(mOnClickListener);
		if (mFinishedStart) {
			setListAdapter(mAdapter);
		}
		mHandler.post(mRequestFocus);
		mFinishedStart = true;
	}

	public void setListAdapter(ListAdapter adapter) {
		synchronized (this) {
			ensureList();
			mAdapter = adapter;
			mList.setAdapter(adapter);
		}
	}

	public void setSelection(int position) {
		mList.setSelection(position);
	}

	public int getSelectedItemPosition() {
		return mList.getSelectedItemPosition();
	}

	public long getSelectedItemId() {
		return mList.getSelectedItemId();
	}

	public ListView getListView() {
		ensureList();
		return mList;
	}

	public ListAdapter getListAdapter() {
		return mAdapter;
	}

	private void ensureList() {
		if (mList != null) {
			return;
		}
		setContentView(R.layout.list_content);
	}

	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onListItemClick((ListView) parent, v, position, id);
		}
	};
}