package com.WazaBe.HoloDemo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.app.Fragment;

public class MainFragment extends Fragment {
	private static MainFragment instance;

	public static MainFragment getInstance() {
		if (instance == null) {
			instance = new MainFragment();
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
		view.findViewById(R.id.showPreferences).setVisibility(View.GONE);
		Utils.onViewCreated(view);
	}
}
