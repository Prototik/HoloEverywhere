package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckBoxHolo extends CheckBox {

	public CheckBoxHolo(Context context) {
		super(context);
	}

	public CheckBoxHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckBoxHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	@TargetApi(3)
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(!isInEditMode()) {
			FontLoader.loadFont(this, FontLoader.ROBOTO_REGULAR);
		}
	}
	
}
