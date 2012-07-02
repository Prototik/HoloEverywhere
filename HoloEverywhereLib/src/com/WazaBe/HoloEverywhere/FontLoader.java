package com.WazaBe.HoloEverywhere;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class FontLoader {
	private static final String DEFAULT_FONT = "Roboto-Regular.ttf";
	private static final Map<String, Typeface> fontMap = new HashMap<String, Typeface>();

	public static View loadFont(View view) {
		return loadFont(view, DEFAULT_FONT);
	}

	@SuppressLint("NewApi")
	public static View loadFont(View view, String font) {
		/*
		 * Build.VERSION.SDK_INT is available since api 4
		 */
		try {
			if (Build.VERSION.SDK_INT >= 11) {
				return view;
			}
		} catch (Exception e) {
		}
		if (view == null) {
			return view;
		}
		font = font.intern();
		if (!FontLoader.fontMap.containsKey(font)) {
			FontLoader.fontMap.put(font, Typeface.createFromAsset(view
					.getContext().getAssets(), font));
		}
		Typeface typeface = FontLoader.fontMap.get(font);
		if (typeface == null) {
			Log.v("FontLoader", "Font " + font + " not found in assets");
			return view;
		}
		try {
			((TextView) view).setTypeface(typeface);
		} catch (ClassCastException e) {
		}
		try {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				loadFont(group.getChildAt(i), font);
			}
		} catch (ClassCastException e) {
		}
		return view;
	}

	private FontLoader() {
	}
}
