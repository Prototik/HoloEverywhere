
package org.holoeverywhere;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class FontLoader {
    public static final class HoloFont {
        public static final HoloFont ROBOTO = new HoloFont(-1);
        public static final HoloFont ROBOTO_BOLD = new HoloFont(
                R.raw.roboto_bold);
        public static final HoloFont ROBOTO_BOLD_ITALIC = new HoloFont(
                R.raw.roboto_bolditalic);
        public static final HoloFont ROBOTO_ITALIC = new HoloFont(
                R.raw.roboto_italic);
        public static final HoloFont ROBOTO_REGULAR = new HoloFont(
                R.raw.roboto_regular);

        public static HoloFont makeFont(int rawResourceId) {
            return new HoloFont(rawResourceId);
        }

        public static HoloFont makeFont(int rawResourceId, boolean ignore) {
            return new HoloFont(rawResourceId, ignore);
        }

        public static HoloFont makeFont(Typeface typeface) {
            return new HoloFont(typeface);
        }

        protected final int font;
        protected final boolean ignore;
        protected final Typeface typeface;

        private HoloFont(int font) {
            this(font, VERSION.SDK_INT >= 11);
        }

        private HoloFont(int font, boolean ignore) {
            this.font = font;
            this.ignore = ignore;
            typeface = null;
        }

        private HoloFont(Typeface typeface) {
            this.typeface = typeface;
            font = -1;
            ignore = false;
        }

        public <T extends View> T apply(T view) {
            if (typeface != null) {
                return FontLoader.apply(view, typeface);
            } else if (font > 0) {
                return FontLoader.apply(view, font);
            }
            return null;
        }

        public boolean isValid() {
            return typeface != null || font > 0;
        }
    }

    private static final SparseArray<Typeface> FONT_CACHE = new SparseArray<Typeface>();
    private static final String TAG = "FontLoader";

    public static <T extends View> T apply(T view) {
        return FontLoader.applyDefaultStyles(view);
    }

    public static <T extends View> T apply(T view, HoloFont font) {
        if (font.ignore) {
            return view;
        }
        if (font.isValid()) {
            return font.apply(view);
        } else {
            throw new IllegalArgumentException("HoloFont is invalid");
        }
    }

    @SuppressLint("NewApi")
    public static <T extends View> T apply(T view, int font) {
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

    public static <T extends View> T apply(T view, Typeface typeface) {
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

    public static <T extends View> T applyDefaultStyles(T view) {
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
                typeface = FontLoader.loadTypeface(view.getContext(), font.font);
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

    public static Typeface loadTypeface(Context context, int font) {
        Typeface typeface = FontLoader.FONT_CACHE.get(font);
        if (typeface == null) {
            try {
                File file = new File(context.getApplicationInfo().dataDir + "/fonts");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(file, "font_0x" + Integer.toHexString(font));
                FontLoader.FONT_CACHE.put(font,
                        typeface = readTypeface(file, context.getResources(), font, true));
            } catch (Exception e) {
                Log.e(FontLoader.TAG, "Error of loading font", e);
            }
        }
        return typeface;
    }

    private static Typeface readTypeface(File file, Resources res, int font,
            boolean allowReadExistsFile) throws Exception {
        try {
            if (!allowReadExistsFile || !file.exists()) {
                InputStream is = new BufferedInputStream(res.openRawResource(font));
                OutputStream os = new ByteArrayOutputStream(Math.max(is.available(), 1024));
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
                is.close();
                os.flush();
                buffer = ((ByteArrayOutputStream) os).toByteArray();
                os.close();
                os = new FileOutputStream(file);
                os.write(buffer);
                os.flush();
                os.close();
            }
            return Typeface.createFromFile(file);
        } catch (Exception e) {
            if (allowReadExistsFile) {
                return readTypeface(file, res, font, false);
            }
            throw e;
        }
    }

    private FontLoader() {
    }
}
