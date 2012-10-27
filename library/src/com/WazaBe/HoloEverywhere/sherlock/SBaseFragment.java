package com.WazaBe.HoloEverywhere.sherlock;

import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;

import com.WazaBe.HoloEverywhere.app.BaseFragment;

public interface SBaseFragment extends BaseFragment,
		OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener,
		OnOptionsItemSelectedListener {
	public SBase getSBase();

	@Override
	public boolean isABSSupport();
}
