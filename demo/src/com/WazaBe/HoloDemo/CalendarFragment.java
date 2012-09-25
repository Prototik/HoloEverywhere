package com.WazaBe.HoloDemo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.sherlock.SFragment;

public class CalendarFragment extends SFragment {
	private static final class CalendarFragmentHolder {
		private static final CalendarFragment instance = new CalendarFragment();
	}

	public static CalendarFragment getInstance() {
		return CalendarFragmentHolder.instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.calendar);
	}
}
