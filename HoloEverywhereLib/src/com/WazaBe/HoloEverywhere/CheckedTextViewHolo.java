package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class CheckedTextViewHolo extends CheckedTextView {

	public CheckedTextViewHolo(Context context) {
		this(context, null, 0);
	}

	public CheckedTextViewHolo(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckedTextViewHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
	}

}
