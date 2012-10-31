package org.holoeverywhere.sherlock;

import org.holoeverywhere.app.BaseFragment;

import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;


public interface SBaseFragment extends BaseFragment,
		OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener,
		OnOptionsItemSelectedListener {
	public SBase getSBase();

	@Override
	public boolean isABSSupport();
}
