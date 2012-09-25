package com.WazaBe.HoloEverywhere.sherlock;

import android.os.Build.VERSION;
import android.support.v4.app._ActionBarSherlockTrojanHorse.OnCreateOptionsMenuListener;
import android.support.v4.app._ActionBarSherlockTrojanHorse.OnOptionsItemSelectedListener;
import android.support.v4.app._ActionBarSherlockTrojanHorse.OnPrepareOptionsMenuListener;

import com.WazaBe.HoloEverywhere.app.Activity;
import com.WazaBe.HoloEverywhere.app.DialogFragment;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.MenuWrapper;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SDialogFragment extends DialogFragment implements
		OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener,
		OnOptionsItemSelectedListener {
	private SBase mBase;

	public SActivity getSherlockActivity() {
		return (SActivity) mBase;
	}

	@Override
	public boolean isABSSupport() {
		return VERSION.SDK_INT >= 7;
	}

	@Override
	public void onAttach(Activity activity) {
		if (isABSSupport()) {
			if (!(activity instanceof SBase)) {
				throw new IllegalStateException(getClass().getSimpleName()
						+ " must be attached to a SActivity.");
			}
			mBase = (SBase) activity;
		}
		super.onAttach(activity);
	}

	@Override
	public final void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater) {
		if (isABSSupport()) {
			onCreateOptionsMenu(new MenuWrapper(menu),
					mBase.getSupportMenuInflater());
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	}

	@Override
	public void onDetach() {
		mBase = null;
		super.onDetach();
	}

	@Override
	public final boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (isABSSupport()) {
			return onOptionsItemSelected(new MenuItemWrapper(item));
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public final void onPrepareOptionsMenu(android.view.Menu menu) {
		if (isABSSupport()) {
			onPrepareOptionsMenu(new MenuWrapper(menu));
		} else {
			super.onPrepareOptionsMenu(menu);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
	}
}
