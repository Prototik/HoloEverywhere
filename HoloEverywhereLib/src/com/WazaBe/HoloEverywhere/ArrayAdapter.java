package com.WazaBe.HoloEverywhere;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ArrayAdapter<T> extends android.widget.ArrayAdapter<T> {
	public ArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public ArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public ArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
	}

	public ArrayAdapter(Context context, int resource, int textViewResourceId,
			T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public ArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
	}

	public ArrayAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public static ArrayAdapter<CharSequence> createFromResource(
			Context context, int textArrayResId, int textViewResId) {
		return new ArrayAdapter<CharSequence>(context, textViewResId, context
				.getResources().getTextArray(textArrayResId));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return FontLoader
				.loadFont(super.getView(position, convertView, parent));
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return FontLoader.loadFont(super.getDropDownView(position, convertView,
				parent));
	}
}