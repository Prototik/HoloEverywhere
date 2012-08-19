package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonHolo extends Button {

	public ButtonHolo(Context context) {
		super(context);
	}

	public ButtonHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtonHolo(Context context, AttributeSet attrs, int defStyle) {
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