package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.sherlock.SFragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

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
