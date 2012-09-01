package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.WazaBe.HoloEverywhere.FontLoader.HoloFont;

@Deprecated
/*
 * You should use *Activity instead *AnyView*Holo
 */
public class CheckBoxHolo extends CheckBox {
	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public CheckBoxHolo(Context context) {
		super(context);
	}

	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public CheckBoxHolo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Deprecated
	/*
	 * You should use *Activity instead *AnyView*Holo
	 */
	public CheckBoxHolo(Context context, AttributeSet attrs, int defStyle) {
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
