
package org.holoeverywhere.slider.menu;

import org.holoeverywhere.slider.R;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SliderMenuItemView extends TextView {
    public SliderMenuItemView(Context context) {
        this(context, null);
    }

    public SliderMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sliderMenuItemStyle);
    }

    public SliderMenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderMenu_ItemView,
                defStyle, R.style.Holo_SliderMenu_ItemView);
        setSelectionHandlerColor(a.getColor(
                R.styleable.SliderMenu_ItemView_sliderMenuItemSelectionHandler,
                getResources().getColor(R.color.holo_blue_dark)));
        a.recycle();
    }

    private int mSelectionHandlerColor = Color.WHITE;

    public void setSelectionHandlerColor(int selectionHandlerColor) {
        mSelectionHandlerColor = selectionHandlerColor;
        final Drawable background = getBackground();
        if (background != null) {
            background.setColorFilter(mSelectionHandlerColor, Mode.MULTIPLY);
        }
    }

    public int getSelectionHandlerColor() {
        return mSelectionHandlerColor;
    }
}
