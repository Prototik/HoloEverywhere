
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
    public static final int TEXT_STYLE_BLACK = 1 << 3;
    public static final int TEXT_STYLE_BOLD = 1 << 0;
    public static final int TEXT_STYLE_CONDENDSED = 1 << 4;
    public static final int TEXT_STYLE_ITALIC = 1 << 1;
    public static final int TEXT_STYLE_LIGHT = 1 << 2;
    public static final int TEXT_STYLE_MEDIUM = 1 << 5;
    public static final int TEXT_STYLE_NORMAL = 0;
    public static final int TEXT_STYLE_THIN = 1 << 6;

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
    private static int[] parseFontStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextAppearance, defStyleAttr, 0);
        final int[] result = parseFontStyle(a);
        a.recycle();
        return result;
    }

    private static int parseFontStyle(String string) {
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

    private static int[] parseFontStyle(TypedArray a) {
        boolean force = true;
        int fontStyle = TEXT_STYLE_NORMAL;
        TypedValue value = new TypedValue();
        a.getValue(R.styleable.TextAppearance_android_fontFamily, value);
        if (value.string == null) {
            a.getValue(R.styleable.TextAppearance_android_typeface, value);
        }
        if (value.string == null) {
            force = false;
        } else {
            fontStyle = parseFontStyle(value.string.toString());
        }
        fontStyle |= a.getInt(R.styleable.TextAppearance_android_textStyle, TEXT_STYLE_NORMAL);
        return new int[] {
                fontStyle, force ? 1 : 0
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
            T textView, int fontStyle) {
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
        int[] fontStyle = parseFontStyle(appearance);
        textView.setFontStyle(fontStyle[0] | (fontStyle[1] == 0 ? textView.getFontStyle() : 0));
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
        TextView.construct(this, context, attrs, defStyle);
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
    public void setFontStyle(int fontStyle) {
        mFontStyle = fontStyle;
        TextView.setFontStyle(this, fontStyle);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
