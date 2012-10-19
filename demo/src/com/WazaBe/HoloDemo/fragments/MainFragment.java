package com.WazaBe.HoloDemo.fragments;

import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.sherlock.SFragment;
import com.WazaBe.HoloEverywhere.widget.Toast;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public class MainFragment extends SFragment {
	private static MainFragment instance;

	public static MainFragment getInstance() {
		if (MainFragment.instance == null) {
			return new MainFragment();
		}
		return MainFragment.instance;
	}

	private int[] contextItemIds = new int[] { R.id.item3, R.id.item4,
			R.id.item5 };
	private int contextItemSelected = 0;

	public MainFragment() {
		MainFragment.instance = this;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String text;
		switch (item.getItemId()) {
		case R.id.item1:
			text = "Toast 1";
			break;
		case R.id.item2:
			text = "Toast 2";
			break;
		case R.id.item3:
			text = "Toggle to first item";
			contextItemSelected = 0;
			break;
		case R.id.item4:
			text = "Toggle to second item";
			contextItemSelected = 1;
			break;
		case R.id.item5:
			text = "Toggle to third item";
			contextItemSelected = 2;
			break;
		default:
			return super.onContextItemSelected(item);
		}
		Toast.makeText(getSActivity(), text, Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.menu, menu);
		menu.findItem(contextItemIds[contextItemSelected]).setChecked(true);
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

	public void showContextMenu(View v) {
		registerForContextMenu(v);
		openContextMenu(v);
		unregisterForContextMenu(v);
	}
}
