
package org.holoeverywhere.widget;

import org.holoeverywhere.FontLoader.FontStyleProvider;
import org.holoeverywhere.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
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
        TypedArray a = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.fontFamily,
                android.R.attr.typeface,
                android.R.attr.textStyle
        }, defStyleAttr, 0);
        final TypedValue value = new TypedValue();
        a.getValue(0, value);
        if (value.string != null) {
            a.recycle();
            return parse(value.string.toString());
        } else {
            int i = TEXT_STYLE_NORMAL;
            a.getValue(1, value);
            if (value.string != null) {
                i |= parse(value.string.toString());
            }
            i |= a.getInt(2, TEXT_STYLE_NORMAL);
            a.recycle();
            return i;
        }
    }

    private boolean allCaps = false;
    private int mFontStyle;

    private CharSequence originalText;

    private BufferType originalType;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TextView, defStyle, 0);
        if (a.hasValue(R.styleable.TextView_android_textAllCaps)) {
            allCaps = a.getBoolean(R.styleable.TextView_android_textAllCaps,
                    false);
        } else {
            allCaps = a.getBoolean(R.styleable.TextView_textAllCaps, false);
        }
        CharSequence text = null;
        if (a.hasValue(R.styleable.TextView_android_text)) {
            text = a.getText(R.styleable.TextView_android_text);
        }
        a.recycle();
        if (text != null) {
            setText(text);
        }
        mFontStyle = TextView.parseFontStyle(context, attrs, defStyle);
    }

    @Override
    @SuppressLint("NewApi")
    public void dispatchDisplayHint(int hint) {
        onDisplayHint(hint);
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    public boolean isAllCaps() {
        return allCaps;
    }

    @Override
    @SuppressLint("NewApi")
    protected void onDisplayHint(int hint) {
        if (VERSION.SDK_INT >= 8) {
            super.onDisplayHint(hint);
        }
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        this.allCaps = allCaps;
        updateTextState();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        originalType = type;
        updateTextState();
    }

    private void updateTextState() {
        if (originalText == null) {
            super.setText(null, originalType);
            return;
        }
        super.setText(allCaps ? originalText.toString().toUpperCase()
                : originalText, originalType);
    }
}
