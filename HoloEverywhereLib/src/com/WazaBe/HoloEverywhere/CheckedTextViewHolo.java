package com.WazaBe.HoloEverywhere;

import com.WazaBe.HoloEverywhere.FontLoader.HoloFont;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class CheckedTextViewHolo extends CheckedTextView {

	public CheckedTextViewHolo(Context context) {
		super(context);
	}

	public CheckedTextViewHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckedTextViewHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	@TargetApi(3)
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(!isInEditMode()) {
			FontLoader.loadFont(this, HoloFont.ROBOTO_REGULAR);
		}
	}
	
}
