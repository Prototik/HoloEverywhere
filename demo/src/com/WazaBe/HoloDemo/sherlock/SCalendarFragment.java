package com.WazaBe.HoloDemo.sherlock;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.sherlock.SFragment;

public class SCalendarFragment extends SFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.calendar);
	}
}
