
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
        /**
         * Roboto Bold
         */
        public static final HoloFont ROBOTO_BOLD = new HoloFont(
                R.raw.roboto_bold);
        /**
         * Roboto Bold Italic
         */
        public static final HoloFont ROBOTO_BOLD_ITALIC = new HoloFont(
                R.raw.roboto_bolditalic);
        /**
         * Roboto Italic
         */
        public static final HoloFont ROBOTO_ITALIC = new HoloFont(
                R.raw.roboto_italic);
        /**
         * Roboto Regular
         */
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

        /**
         * Font raw resource id
         */
        protected int mFontId;
        /**
         * If this flag setted this font doesn't modify any view
         */
        protected boolean mIgnore;
        /**
         * Loaded typeface
         */
        protected Typeface mTypeface;

        private HoloFont(int font) {
            this(font, VERSION.SDK_INT >= 11);
        }

        private HoloFont(int font, boolean ignore) {
            mFontId = font;
            mIgnore = ignore;
            mTypeface = null;
        }

        private HoloFont(Typeface typeface) {
            mFontId = -1;
            mIgnore = false;
            mTypeface = typeface;
        }

        public <T extends View> T apply(T view) {
            if (mIgnore || mTypeface == null && mFontId <= 0 || view == null) {
                return view;
            }
            if (mTypeface == null) {
                mTypeface = loadTypeface(view.getContext(), mFontId);
            }
            return FontLoader.apply(view, mTypeface);
        }
    }

    private static final SparseArray<Typeface> sFontCache = new SparseArray<Typeface>();
    private static final String TAG = FontLoader.class.getSimpleName();

    public static <T extends View> T apply(T view) {
        return FontLoader.applyDefaultStyles(view);
    }

    public static <T extends View> T apply(T view, HoloFont font) {
        return font.apply(view);
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
        return HoloFont.ROBOTO_REGULAR.apply(view);
    }

    public static Typeface loadTypeface(Context context, int font) {
        Typeface typeface = FontLoader.sFontCache.get(font);
        if (typeface == null) {
            try {
                File file = new File(context.getCacheDir(), "fonts");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(file, "font_0x" + Integer.toHexString(font));
                FontLoader.sFontCache.put(font,
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
