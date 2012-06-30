package com.WazaBe.HoloEverywhere;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

public final class FontLoader {
	private static final Map<String, Typeface> fontMap = new HashMap<String, Typeface>();

	public static void loadFont(TextView view, String font) {
		if (Build.VERSION.SDK_INT >= 14 || view == null) {
			return;
		}
		font = font.intern();
		if (!FontLoader.fontMap.containsKey(font)) {
			FontLoader.fontMap.put(font, Typeface.createFromAsset(view
					.getContext().getAssets(), font));
		}
		Typeface typeface = FontLoader.fontMap.get(font);
		if (typeface == null) {
			Log.v("FontLoader", "Font " + font + " not found in assets");
			return;
		}
		view.setTypeface(typeface);
	}

	private FontLoader() {
	}
}
