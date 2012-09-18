package com.WazaBe.HoloDemo.sherlock;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloDemo.Utils;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.sherlock.SFragment;

public class SMainFragment extends SFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main);
		view.findViewById(R.id.showPreferences).setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Utils.onViewCreated(view);
	}
}
