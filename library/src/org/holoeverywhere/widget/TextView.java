
package org.holoeverywhere.widget;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.FontLoader.FontStyleProvider;
import org.holoeverywhere.R;
import org.holoeverywhere.text.AllCapsTransformationMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

public class TextView extends android.widget.TextView implements FontStyleProvider {
    public static <T extends android.widget.TextView & FontStyleProvider> void construct(
            T textView, Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
        final int textAppearance = a.getResourceId(R.styleable.TextView_android_textAppearance, 0);
        a.recycle();
        TextView.setTextAppearance(textView, context, textAppearance);

        a = context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0);
        TextView.setTextAppearance(textView, a);
        a.recycle();
    }

    /**
     * Looks ugly? Yea, i know.
     */
    @SuppressLint("InlinedApi")
    private static Object[] parseFontStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextAppearance, defStyleAttr, 0);
        final Object[] result = parseFontStyle(a);
        a.recycle();
        return result;
    }

    private static Object[] parseFontStyle(TypedArray a) {
        boolean force = true;
        int fontStyle = FontLoader.TEXT_STYLE_NORMAL;
        String fontFamily = null;
        TypedValue value = new TypedValue();
        a.getValue(R.styleable.TextAppearance_android_fontFamily, value);
        if (value.string == null) {
            a.getValue(R.styleable.TextAppearance_android_typeface, value);
        }
        if (value.string == null) {
            force = false;
        } else {
            Object[] z = FontLoader.parseFontStyle(value.string.toString());
            fontStyle = (Integer) z[0];
            fontFamily = (String) z[1];
        }
        fontStyle |= a.getInt(R.styleable.TextAppearance_android_textStyle,
                FontLoader.TEXT_STYLE_NORMAL);
        return new Object[] {
                force, fontStyle, fontFamily
        };
    }

    public static void setAllCaps(android.widget.TextView textView, boolean allCaps) {
        if (allCaps) {
            textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
        } else {
            textView.setTransformationMethod(null);
        }
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setFontStyle(
            T textView, String fontFamily, int fontStyle) {
        FontLoader.applyDefaultFont(textView);
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setTextAppearance(
            T textView, Context context, int resid) {
        if (resid == 0) {
            return;
        }
        TypedArray appearance = context.obtainStyledAttributes(resid,
                R.styleable.TextAppearance);
        setTextAppearance(textView, appearance);
        appearance.recycle();
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setTextAppearance(
            T textView, TypedArray appearance) {
        int color = appearance.getColor(
                R.styleable.TextAppearance_android_textColorHighlight, 0);
        if (color != 0) {
            textView.setHighlightColor(color);
        }
        ColorStateList colors = appearance
                .getColorStateList(R.styleable.TextAppearance_android_textColor);
        if (colors != null) {
            textView.setTextColor(colors);
        }
        int ts = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
        if (ts != 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
        }
        colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColorHint);
        if (colors != null) {
            textView.setHintTextColor(colors);
        }
        colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
        if (colors != null) {
            textView.setLinkTextColor(colors);
        }
        if (appearance.getBoolean(R.styleable.TextAppearance_android_textAllCaps, false)) {
            textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
        }
        Object[] font = parseFontStyle(appearance);
        textView.setFontStyle((String) font[2], (Integer) font[1]
                | ((Boolean) font[0] ? 0 : textView.getFontStyle()));
    }

    private String mFontFamily;

    private int mFontStyle;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
    }

    @Override
    public String getFontFamily() {
        return mFontFamily;
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        TextView.setAllCaps(this, allCaps);
    }

    @Override
    public void setFontStyle(String fontFamily, int fontStyle) {
        mFontFamily = fontFamily;
        mFontStyle = fontStyle;
        TextView.setFontStyle(this, fontFamily, fontStyle);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
