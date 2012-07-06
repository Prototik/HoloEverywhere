package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextHolo extends EditText {

	public EditTextHolo(Context context) {
		this(context, null, 0);
	}

	public EditTextHolo(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EditTextHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
	}

}