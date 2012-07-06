package com.WazaBe.HoloEverywhere;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

public final class FontLoader {
	
	public static final String ROBOTO_REGULAR = "Roboto-Regular.ttf";
	
	private static final Map<String, Typeface> fontMap = new HashMap<String, Typeface>();	

	public static void loadFont(TextView view, String font) {
		if (Build.VERSION.SDK_INT >= 14 || view == null || view.getContext() == null) {
			return;
		}
		Typeface typeface = loadTypeface(view.getContext(), font);
		if (typeface != null) {
			view.setTypeface(typeface);
		}
	}
	
	private static Typeface loadTypeface(Context ctx, String font) {
		if (!FontLoader.fontMap.containsKey(font)) {
			try {
				Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), font);
				FontLoader.fontMap.put(font, typeface);
			} catch (Exception e) {
				Log.w("FontLoader", "Error loading font " + font + " from assets. Error: " + e.getMessage());
			}
		}
		return FontLoader.fontMap.get(font);
	}

	private FontLoader() {
	}
}
