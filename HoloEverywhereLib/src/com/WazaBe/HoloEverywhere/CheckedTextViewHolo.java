package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class CheckedTextViewHolo extends CheckedTextView {

    public CheckedTextViewHolo(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(font);
    }
}
