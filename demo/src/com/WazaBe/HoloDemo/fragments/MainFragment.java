package com.WazaBe.HoloDemo.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.sherlock.SFragment;

public class MainFragment extends SFragment {
	private static MainFragment instance;

	public static MainFragment getInstance() {
		if (instance == null) {
			return new MainFragment();
		}
		return instance;
	}

	public MainFragment() {
		instance = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (isABSSupport()) {
			view.findViewById(R.id.showPreferences).setVisibility(View.GONE);
			view.findViewById(R.id.showAbout).setVisibility(View.GONE);
		}
	}
}
