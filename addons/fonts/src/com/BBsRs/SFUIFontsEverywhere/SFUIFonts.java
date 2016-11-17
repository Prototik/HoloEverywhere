package com.BBsRs.SFUIFontsEverywhere;

import com.BBsRs.SFUIFontsEverywhere.SFUIFontsPath;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class SFUIFonts {
	public static final SFUIFonts ULTRALIGHT = new SFUIFonts(SFUIFontsPath.ULTRALIGHT);
	public static final SFUIFonts LIGHT = new SFUIFonts(SFUIFontsPath.LIGHT);
	public static final SFUIFonts MEDIUM = new SFUIFonts(SFUIFontsPath.MEDIUM);
	private final String assetName;
	private volatile Typeface typeface;

	public SFUIFonts(String assetName) {
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
