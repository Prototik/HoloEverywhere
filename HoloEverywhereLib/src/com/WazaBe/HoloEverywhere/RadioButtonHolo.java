package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonHolo extends RadioButton {

    public RadioButtonHolo(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(font);
    }
}
