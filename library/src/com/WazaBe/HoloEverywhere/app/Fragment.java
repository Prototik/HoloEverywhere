package com.WazaBe.HoloEverywhere.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app._HoloFragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

import com.WazaBe.HoloEverywhere.FontLoader;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.preference.PreferenceManager;
import com.WazaBe.HoloEverywhere.preference.SharedPreferences;
import com.actionbarsherlock.internal.view.menu.ContextMenu;
import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;

public class Fragment extends _HoloFragment implements BaseFragment {
	private Base mBase;

	@Override
	public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
			View view) {
		mBase.createContextMenu(contextMenuBuilder, view);
	}

	@Override
	public SharedPreferences getDefaultSharedPreferences() {
		return mBase.getDefaultSharedPreferences();
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(getActivity());
	}

	@Override
	public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
		return LayoutInflater.from(super.getLayoutInflater(savedInstanceState));
	}

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return PreferenceManager.wrap(getActivity(), name, mode);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Activity & Base> T getSupportActivity() {
		return (T) mBase;
	}

	@Override
	public FragmentManager getSupportFragmentManager() {
		if (mBase != null) {
			return mBase.getSupportFragmentManager();
		} else {
			return getFragmentManager();
		}
	}

	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(getActivity().getSystemService(
				name));
	}

	@Override
	public boolean isABSSupport() {
		return false;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public final void onAttach(android.app.Activity activity) {
		if (!(activity instanceof Activity)) {
			throw new RuntimeException(
					"HoloEverywhere.Fragment must be attached to HoloEverywhere.Activity");
		}
		mBase = (Activity) activity;
		onAttach((Activity) activity);
	}

	@Override
	public final View onCreateView(android.view.LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return prepareDecorView(onCreateView(
				getLayoutInflater(savedInstanceState), container,
				savedInstanceState));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
	}

	@Override
	public final void onInflate(android.app.Activity activity,
			AttributeSet attrs, Bundle savedInstanceState) {
		onInflate((Activity) activity, attrs, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		FontLoader.apply(view);
		super.onViewCreated(view, savedInstanceState);
	}

	protected View prepareDecorView(View v) {
		v = FontLoader.apply(v);
		if (!mBase.getConfig().isDisableContextMenu()) {
			v = new ContextMenuDecorView(v, this);
		}
		return v;
	}
}
