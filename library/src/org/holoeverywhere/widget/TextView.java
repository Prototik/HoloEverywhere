
package org.holoeverywhere.widget;

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
    public static final int TEXT_STYLE_BLACK = 1 << 3;
    public static final int TEXT_STYLE_BOLD = 1 << 0;
    public static final int TEXT_STYLE_CONDENDSED = 1 << 4;
    public static final int TEXT_STYLE_ITALIC = 1 << 1;
    public static final int TEXT_STYLE_LIGHT = 1 << 2;
    public static final int TEXT_STYLE_MEDIUM = 1 << 5;
    public static final int TEXT_STYLE_NORMAL = 0;
    public static final int TEXT_STYLE_THIN = 1 << 6;

    private static int parse(String string) {
        int c = string.lastIndexOf('-');
        if (c > 0) {
            string = string.substring(c + 1);
        }
        int i = TEXT_STYLE_NORMAL;
        if (string.contains("bold")) {
            i |= TEXT_STYLE_BOLD;
        }
        if (string.contains("italic")) {
            i |= TEXT_STYLE_ITALIC;
        }
        if (string.contains("light")) {
            i |= TEXT_STYLE_LIGHT;
        }
        if (string.contains("black")) {
            i |= TEXT_STYLE_BLACK;
        }
        if (string.contains("condensed")) {
            i |= TEXT_STYLE_CONDENDSED;
        }
        if (string.contains("medium")) {
            i |= TEXT_STYLE_MEDIUM;
        }
        if (string.contains("thin")) {
            i |= TEXT_STYLE_THIN;
        }
        return i;
    }

    /**
     * Looks ugly? Yea, i know.
     */
    @SuppressLint("InlinedApi")
    public static int parseFontStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextAppearance, defStyleAttr, 0);
        final int result = parseFontStyle(a);
        a.recycle();
        return result;
    }

    public static int parseFontStyle(TypedArray a) {
        final TypedValue value = new TypedValue();
        a.getValue(R.styleable.TextAppearance_android_fontFamily, value);
        if (value.string != null) {
            a.recycle();
            return parse(value.string.toString());
        } else {
            int i = TEXT_STYLE_NORMAL;
            a.getValue(R.styleable.TextAppearance_android_typeface, value);
            if (value.string != null) {
                i |= parse(value.string.toString());
            }
            i |= a.getInt(R.styleable.TextAppearance_android_textStyle, TEXT_STYLE_NORMAL);
            a.recycle();
            return i;
        }
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
        int color;
        ColorStateList colors;
        int ts;
        color = appearance.getColor(
                R.styleable.TextAppearance_android_textColorHighlight, 0);
        if (color != 0) {
            textView.setHighlightColor(color);
        }
        colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColor);
        if (colors != null) {
            textView.setTextColor(colors);
        }
        ts = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
        if (ts != 0) {
            // textView.setRawTextSize(ts);
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
        textView.setFontStyle(parseFontStyle(appearance));
    }

    private int mFontStyle;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
        TextView.setTextAppearance(this, context,
                a.getResourceId(R.styleable.TextView_android_textAppearance, 0));
        TextView.setTextAppearance(this,
                context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0));
        a.recycle();
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    @Override
    public void setFontStyle(int mFontStyle) {
        this.mFontStyle = mFontStyle;
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
