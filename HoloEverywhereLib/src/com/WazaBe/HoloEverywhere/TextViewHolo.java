package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewHolo extends TextView {

	public TextViewHolo(Context context) {
		this(context, null, 0);
	}

	public TextViewHolo(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		FontLoader.loadFont(this, "Roboto-Regular.ttf");
	}

}