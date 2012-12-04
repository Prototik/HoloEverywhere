
package org.holoeverywhere;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class FontLoader {
    public static class HoloFont {
        public static final HoloFont ROBOTO = new HoloFont(-1);
        public static final HoloFont ROBOTO_BOLD = new HoloFont(
                R.raw.roboto_bold);
        public static final HoloFont ROBOTO_BOLD_ITALIC = new HoloFont(
                R.raw.roboto_bolditalic);
        public static final HoloFont ROBOTO_ITALIC = new HoloFont(
                R.raw.roboto_italic);
        public static final HoloFont ROBOTO_REGULAR = new HoloFont(
                R.raw.roboto_regular);

        protected final int font;
        protected final boolean ignore;

        private HoloFont(int font) {
            this(font, VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB);
        }

        private HoloFont(int font, boolean ignore) {
            this.font = font;
            this.ignore = ignore;
        }
    }

    private static final SparseArray<Typeface> fontArray = new SparseArray<Typeface>();
    private static final String TAG = "FontLoader";

    public static View apply(View view) {
        return FontLoader.applyDefaultStyles(view);
    }

    public static View apply(View view, HoloFont font) {
        if (font.ignore) {
            return view;
        }
        return FontLoader.apply(view, font.font);
    }

    @SuppressLint("NewApi")
    public static View apply(View view, int font) {
        if (view == null || view.getContext() == null
                || view.getContext().isRestricted()) {
            Log.e(FontLoader.TAG, "View or context is invalid");
            return view;
        }
        if (font < 0) {
            return FontLoader.applyDefaultStyles(view);
        }
        Typeface typeface = FontLoader.loadTypeface(view.getContext(), font);
        if (typeface == null) {
            Log.v(FontLoader.TAG, "Font " + font + " not found in resources");
            return view;
        } else {
            return FontLoader.apply(view, typeface);
        }
    }

    public static View apply(View view, Typeface typeface) {
        if (view == null || view.getContext() == null
                || view.getContext().isRestricted()) {
            return view;
        }
        if (typeface == null) {
            Log.v(FontLoader.TAG, "Font is null");
            return view;
        }
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeface);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                FontLoader.apply(group.getChildAt(i), typeface);
            }
        }
        return view;
    }

    public static View applyDefaultStyles(View view) {
        if (view == null || view.getContext() == null
                || view.getContext().isRestricted()) {
            return view;
        }
        if (view instanceof TextView) {
            TextView text = (TextView) view;
            Typeface typeface = text.getTypeface();
            if (typeface == null) {
                text.setTypeface(FontLoader.loadTypeface(view.getContext(),
                        HoloFont.ROBOTO_REGULAR.font));
                return view;
            }
            HoloFont font;
            boolean isBold = typeface.isBold(), isItalic = typeface.isItalic();
            if (isBold && isItalic) {
                font = HoloFont.ROBOTO_BOLD_ITALIC;
            } else if (isBold && !isItalic) {
                font = HoloFont.ROBOTO_BOLD;
            } else if (!isBold && isItalic) {
                font = HoloFont.ROBOTO_ITALIC;
            } else {
                font = HoloFont.ROBOTO_REGULAR;
            }
            if (!font.ignore) {
                typeface = FontLoader
                        .loadTypeface(view.getContext(), font.font);
                if (typeface != null) {
                    text.setTypeface(typeface);
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                FontLoader.applyDefaultStyles(group.getChildAt(i));
            }
        }
        return view;
    }

    /**
     * @deprecated Use {@link LayoutInflater#inflate(Context, int)} instead
     */
    @Deprecated
    public static View inflate(Context context, int res) {
        return LayoutInflater.inflate(context, res);
    }

    /**
     * @deprecated Use {@link LayoutInflater#inflate(Context, int, ViewGroup)}
     *             instead
     */
    @Deprecated
    public static View inflate(Context context, int res, ViewGroup parent) {
        return LayoutInflater.inflate(context, res, parent);
    }

    public static Typeface loadTypeface(Context context, int font) {
        if (FontLoader.fontArray.get(font) == null) {
            try {
                File file = new File(context.getApplicationInfo().dataDir
                        + "/fonts");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(file, Integer.toHexString(font));
                if (file.exists()) {
                    file.delete();
                }
                Resources res = context.getResources();
                InputStream is = res.openRawResource(font);
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
                os.flush();
                os.close();
                is.close();
                FontLoader.fontArray.put(font, Typeface.createFromFile(file));
            } catch (Exception e) {
                Log.e(FontLoader.TAG, "Error of loading font", e);
            }
        }
        return FontLoader.fontArray.get(font);
    }

    private FontLoader() {
    }
}
