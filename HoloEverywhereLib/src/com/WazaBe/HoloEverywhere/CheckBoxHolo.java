package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckBoxHolo extends CheckBox {

	public CheckBoxHolo(Context context) {
		this(context, null, 0);
	}

	public CheckBoxHolo(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckBoxHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, "Roboto-Regular.ttf");
	}

}
