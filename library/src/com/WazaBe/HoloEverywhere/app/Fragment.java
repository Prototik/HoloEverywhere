package com.WazaBe.HoloEverywhere.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.WazaBe.HoloEverywhere.util.BaseSharedPreferences;

;

public class Fragment extends android.support.v4.app.Fragment {
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		FontLoader.apply(view);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public final void onAttach(android.app.Activity activity) {
		onAttach((Activity) activity);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public FragmentManager getSupportFragmentManager() {
		if (getSupportActivity() != null) {
			return getSupportActivity().getSupportFragmentManager();
		} else {
			return getFragmentManager();
		}
	}

	@Override
	public final void onInflate(android.app.Activity activity,
			AttributeSet attrs, Bundle savedInstanceState) {
		onInflate((Activity) activity, attrs, savedInstanceState);
	}

	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
	}

	public Activity getSupportActivity() {
		return (Activity) getActivity();
	}

	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(getActivity());
	}

	@Override
	public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
		return LayoutInflater.from(super.getLayoutInflater(savedInstanceState));
	}

	public SharedPreferences getSupportSharedPreferences(String name, int mode) {
		return new BaseSharedPreferences(getActivity().getSharedPreferences(
				name, mode));
	}

	@Override
	public final View onCreateView(android.view.LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return onCreateView(getLayoutInflater(savedInstanceState), container,
				savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public boolean isABSSupport() {
		return false;
	}

	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(getActivity().getSystemService(
				name));
	}
}
