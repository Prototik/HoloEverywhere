package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonHolo extends RadioButton {

	public RadioButtonHolo(Context context) {
		this(context, null, 0);
	}

	public RadioButtonHolo(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RadioButtonHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
	}

}
