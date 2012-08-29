package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.WazaBe.HoloEverywhere.FontLoader.HoloFont;

@Deprecated
/*
 * You should use *Activity instead *AnyView*Holo
 */
public class ButtonHolo extends Button {
	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public ButtonHolo(Context context) {
		super(context);
	}

	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public ButtonHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public ButtonHolo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	@TargetApi(3)
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (!isInEditMode()) {
			FontLoader.loadFont(this, HoloFont.ROBOTO_REGULAR);
		}
	}

}