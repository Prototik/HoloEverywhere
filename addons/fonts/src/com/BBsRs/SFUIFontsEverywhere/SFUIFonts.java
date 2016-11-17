package com.BBsRs.SFUIFontsEverywhere;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.CheckedTextView;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.RadioButton;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.TextView;

public class SFUIFonts {
	public static final SFUIFonts ULTRALIGHT = new SFUIFonts(SFUIFontsPath.ULTRALIGHT);
	public static final SFUIFonts LIGHT = new SFUIFonts(SFUIFontsPath.LIGHT);
	public static final SFUIFonts MEDIUM = new SFUIFonts(SFUIFontsPath.MEDIUM);
	public static final String FAMILY_NAME = "SFUIDisplay";
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
	
	public void apply(Context context, CheckedTextView textView) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		textView.setTypeface(typeface);
	}
	
	public void apply(Context context, Button button) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		button.setTypeface(typeface);
	}
	
	public void apply(Context context, SearchAutoComplete mQueryTextView) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		mQueryTextView.setTypeface(typeface);
	}
	
	public void apply(Context context, EditText mEditText) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		mEditText.setTypeface(typeface);
	}
	
	public void apply(Context context, RadioButton mRadioButton) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		mRadioButton.setTypeface(typeface);
	}
	
	public void apply(Context context, CheckBox mCheckBox) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		mCheckBox.setTypeface(typeface);
	}
	
	public void apply(Context context, MenuItem mMenuItem) {
		if (typeface == null) {
			synchronized (this) {
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(), assetName);
				}
			}
		}
		SpannableString s = new SpannableString(mMenuItem.getTitle());
		s.setSpan(new CustomTypefaceSpan(FAMILY_NAME, typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mMenuItem.setTitle(s);
	}
}
