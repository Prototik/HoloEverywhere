package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextHolo extends EditText {

	public EditTextHolo(Context context) {
		super(context);
	}

	public EditTextHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditTextHolo(Context context, AttributeSet attrs, int defStyle) {
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