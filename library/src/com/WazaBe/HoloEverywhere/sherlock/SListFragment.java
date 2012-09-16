package com.WazaBe.HoloEverywhere.sherlock;

import android.support.v4.app._ActionBarSherlockTrojanHorse.OnCreateOptionsMenuListener;
import android.support.v4.app._ActionBarSherlockTrojanHorse.OnOptionsItemSelectedListener;
import android.support.v4.app._ActionBarSherlockTrojanHorse.OnPrepareOptionsMenuListener;

import com.WazaBe.HoloEverywhere.app.Activity;
import com.WazaBe.HoloEverywhere.app.ListFragment;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SListFragment extends ListFragment implements
		OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener,
		OnOptionsItemSelectedListener {
	private SActivity mActivity;

	public SActivity getSherlockActivity() {
		return mActivity;
	}

	@Override
	public boolean isABSSupport() {
		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof SActivity)) {
			throw new IllegalStateException(getClass().getSimpleName()
					+ " must be attached to a SActivity.");
		}
		mActivity = (SActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public final void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater) {
		onCreateOptionsMenu(new MenuWrapper(menu),
				mActivity.getSupportMenuInflater());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	}

	@Override
	public void onDetach() {
		mActivity = null;
		super.onDetach();
	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		return onOptionsItemSelected(new MenuItemWrapper(item));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public final void onPrepareOptionsMenu(android.view.Menu menu) {
		onPrepareOptionsMenu(new MenuWrapper(menu));
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
	}
}
