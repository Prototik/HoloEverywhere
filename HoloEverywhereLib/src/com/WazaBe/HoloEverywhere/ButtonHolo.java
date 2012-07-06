package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonHolo extends Button {

	public ButtonHolo(Context context) {
		this(context, null, 0);
	}

	public ButtonHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
		FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
	}

	public ButtonHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
	}

}