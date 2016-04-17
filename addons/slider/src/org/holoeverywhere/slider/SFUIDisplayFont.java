package org.holoeverywhere.slider;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class SFUIDisplayFont {
	public static final SFUIDisplayFont ULTRALIGHT = new SFUIDisplayFont(SFUIDisplayFontPath.ULTRALIGHT);
	public static final SFUIDisplayFont LIGHT = new SFUIDisplayFont(SFUIDisplayFontPath.LIGHT);
	public static final SFUIDisplayFont MEDIUM = new SFUIDisplayFont(SFUIDisplayFontPath.MEDIUM);
	private final String assetName;
	private volatile Typeface typeface;

	public SFUIDisplayFont(String assetName) {
		this.assetName = assetName;
	}

	public void apply(Context context, TextView textView) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		textView.setTypeface(typeface);
	}
}
