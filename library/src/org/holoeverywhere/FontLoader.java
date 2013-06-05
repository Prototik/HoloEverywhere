
package org.holoeverywhere;

import static org.holoeverywhere.widget.TextView.TEXT_STYLE_BLACK;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_BOLD;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_CONDENDSED;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_ITALIC;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_LIGHT;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_MEDIUM;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_NORMAL;
import static org.holoeverywhere.widget.TextView.TEXT_STYLE_THIN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.holoeverywhere.util.SparseArray;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

public class FontLoader {
    public static class Font {
        private Context mContext;
        private int mFontStyle;
        private Typeface mTypeface;
        private boolean mTypefaceLoaded = false;

        public Font() {
        }

        protected final void assertParent() {
            if (mContext == null) {
                throw new IllegalStateException(
                        "Cannot load typeface without attaching font instance to FontLoader");
            }
        }

        public final Context getContext() {
            return mContext;
        }

        public int getFontStyle() {
            return mFontStyle;
        }

        public Typeface getTypeface(int fontStyle) {
            if (!mTypefaceLoaded) {
                mTypeface = loadTypeface();
                mTypefaceLoaded = true;
            }
            return mTypeface;
        }

        public Typeface loadTypeface() {
            return null;
        }

        protected final void resetTypeface() {
            mTypeface = null;
            mTypefaceLoaded = false;
        }

        public Font setFontStyle(int fontStyle) {
            mFontStyle = fontStyle;
            return this;
        }
    }

    public static class FontCollector extends Font {
        private final SparseArray<Font> mFonts = new SparseArray<Font>();

        @Override
        public Typeface getTypeface(int fontStyle) {
            Font font = mFonts.get(fontStyle);
            if (font != null) {
                font.mContext = getContext();
                return font.getTypeface(fontStyle);
            }
            return null;
        }

        public void register(Font font) {
            final int fontStyle = font.getFontStyle();
            if (mFonts.get(fontStyle) != null) {
                throw new IllegalStateException("Could not register font " + font
                        + " with the fontStyle " + fontStyle + ": already exists");
            }
            if (font instanceof FontCollector) {
                final SparseArray<Font> fonts = ((FontCollector) font).mFonts;
                for (int i = 0; i < fonts.size(); i++) {
                    register(fonts.valueAt(i));
                }
                return;
            }
            mFonts.put(fontStyle, font);
        }

        public void unregister(Font font) {
            unregister(font.getFontStyle());
        }

        public void unregister(int fontStyle) {
            mFonts.delete(fontStyle);
        }
    }

    public static interface FontStyleProvider {
        public int getFontStyle();

        public void setTypeface(Typeface typeface);
    }

    public static class RawFont extends Font {
        private int mRawResourceId;

        public RawFont(int rawResourceId) {
            mRawResourceId = rawResourceId;
        }

        @Override
        public Typeface loadTypeface() {
            assertParent();
            return loadTypeface(true);
        }

        protected Typeface loadTypeface(boolean allowFileReusage) {
            return loadTypeface(new File(getContext().getCacheDir(), "font_0x"
                    + Integer.toHexString(mRawResourceId)), allowFileReusage);
        }

        private Typeface loadTypeface(File file, boolean allowFileReusage) {
            if (file.exists() && allowFileReusage) {
                try {
                    Typeface typeface = Typeface.createFromFile(file);
                    if (typeface == null) {
                        throw new NullPointerException();
                    }
                    return typeface;
                } catch (Exception e) {
                    return loadTypeface(false);
                }
            } else {
                try {
                    InputStream is = getContext().getResources().openRawResource(mRawResourceId);
                    OutputStream os = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int c;
                    while ((c = is.read(buffer)) > 0) {
                        os.write(buffer, 0, c);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    return loadTypeface(file, allowFileReusage);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        public void setRawResourceId(int rawResourceId) {
            mRawResourceId = rawResourceId;
            resetTypeface();
        }
    }

    public static class RawLazyFont extends RawFont {
        private String mRawResourceName;

        public RawLazyFont(String rawResourceName) {
            super(0);
            mRawResourceName = rawResourceName;
        }

        @Override
        public Typeface loadTypeface() {
            assertParent();
            final int id = getContext().getResources().getIdentifier(mRawResourceName,
                    "raw", getContext().getPackageName());
            if (id == 0) {
                throw new IllegalStateException("Could not find font in raw resources: "
                        + mRawResourceName);
            }
            setRawResourceId(id);
            return loadTypeface(true);
        }
    }

    public static final FontCollector ROBOTO;
    public static final Font ROBOTO_BLACK;
    public static final Font ROBOTO_BLACKITALIC;
    public static final Font ROBOTO_BOLD;
    public static final Font ROBOTO_BOLDCONDENSED;
    public static final Font ROBOTO_BOLDCONDENSEDITALIC;
    public static final Font ROBOTO_BOLDITALIC;
    public static final Font ROBOTO_CONDENSED;
    public static final Font ROBOTO_CONDENSEDITALIC;
    public static final Font ROBOTO_ITALIC;
    public static final Font ROBOTO_LIGHT;
    public static final Font ROBOTO_LIGHTITALIC;
    public static final Font ROBOTO_MEDIUM;
    public static final Font ROBOTO_MEDIUMITALIC;
    public static final Font ROBOTO_REGULAR;
    public static final Font ROBOTO_THIN;
    public static final Font ROBOTO_THINITALIC;
    private static Font sDefaultFont;

    static {
        ROBOTO_REGULAR = new RawFont(R.raw.roboto_regular)
                .setFontStyle(TEXT_STYLE_NORMAL);
        ROBOTO_BOLD = new RawFont(R.raw.roboto_bold)
                .setFontStyle(TEXT_STYLE_BOLD);
        ROBOTO_ITALIC = new RawFont(R.raw.roboto_italic)
                .setFontStyle(TEXT_STYLE_ITALIC);
        ROBOTO_BOLDITALIC = new RawFont(R.raw.roboto_bolditalic)
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_ITALIC);

        ROBOTO_BLACK = new RawLazyFont("roboto_black")
                .setFontStyle(TEXT_STYLE_BLACK);
        ROBOTO_BLACKITALIC = new RawLazyFont("roboto_blackitalic")
                .setFontStyle(TEXT_STYLE_BLACK | TEXT_STYLE_ITALIC);
        ROBOTO_BOLDCONDENSED = new RawLazyFont("roboto_boldcondensed")
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_CONDENDSED);
        ROBOTO_BOLDCONDENSEDITALIC = new RawLazyFont("roboto_boldcondenseditalic")
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_CONDENDSED | TEXT_STYLE_ITALIC);
        ROBOTO_CONDENSED = new RawLazyFont("roboto_condensed")
                .setFontStyle(TEXT_STYLE_CONDENDSED);
        ROBOTO_CONDENSEDITALIC = new RawLazyFont("roboto_condenseditalic")
                .setFontStyle(TEXT_STYLE_CONDENDSED | TEXT_STYLE_ITALIC);
        ROBOTO_LIGHT = new RawLazyFont("roboto_light")
                .setFontStyle(TEXT_STYLE_LIGHT);
        ROBOTO_LIGHTITALIC = new RawLazyFont("roboto_lightitalic")
                .setFontStyle(TEXT_STYLE_LIGHT | TEXT_STYLE_ITALIC);
        ROBOTO_MEDIUM = new RawLazyFont("roboto_medium")
                .setFontStyle(TEXT_STYLE_MEDIUM);
        ROBOTO_MEDIUMITALIC = new RawLazyFont("roboto_mediumitalic")
                .setFontStyle(TEXT_STYLE_MEDIUM | TEXT_STYLE_ITALIC);
        ROBOTO_THIN = new RawLazyFont("roboto_thin")
                .setFontStyle(TEXT_STYLE_THIN);
        ROBOTO_THINITALIC = new RawLazyFont("roboto_thinitalic")
                .setFontStyle(TEXT_STYLE_THIN | TEXT_STYLE_ITALIC);

        sDefaultFont = ROBOTO = new FontCollector();
        ROBOTO.register(ROBOTO_REGULAR);
        ROBOTO.register(ROBOTO_BOLD);
        ROBOTO.register(ROBOTO_ITALIC);
        ROBOTO.register(ROBOTO_BOLDITALIC);
        ROBOTO.register(ROBOTO_BLACK);
        ROBOTO.register(ROBOTO_BLACKITALIC);
        ROBOTO.register(ROBOTO_BOLDCONDENSED);
        ROBOTO.register(ROBOTO_BOLDCONDENSEDITALIC);
        ROBOTO.register(ROBOTO_CONDENSED);
        ROBOTO.register(ROBOTO_CONDENSEDITALIC);
        ROBOTO.register(ROBOTO_LIGHT);
        ROBOTO.register(ROBOTO_LIGHTITALIC);
        ROBOTO.register(ROBOTO_MEDIUM);
        ROBOTO.register(ROBOTO_MEDIUMITALIC);
        ROBOTO.register(ROBOTO_THIN);
        ROBOTO.register(ROBOTO_THINITALIC);
    }

    public static <T extends View> T apply(T view, Font font) {
        if (view == null || font == null) {
            return view;
        }
        font.mContext = view.getContext();
        applyInternal(view, font);
        return view;
    }

    public static <T extends View> T applyDefaultFont(T view) {
        return apply(view, sDefaultFont);
    }

    private static void applyInternal(View view, Font font) {
        if (view instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) view;
            final int childCount = vg.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applyInternal(vg.getChildAt(i), font);
            }
        }
        if (view instanceof FontStyleProvider) {
            if (view.getTag(R.id.fontLoaderTag) == font) {
                return;
            }
            final FontStyleProvider provider = (FontStyleProvider) view;
            final Typeface typeface = font.getTypeface(provider.getFontStyle());
            if (typeface != null) {
                provider.setTypeface(typeface);
                view.setTag(R.id.fontLoaderTag, font);
            }
        }

    }

    public static Font getDefaultFont() {
        return sDefaultFont;
    }

    public static void setDefaultFont(Font defaultFont) {
        sDefaultFont = defaultFont;
    }

    private FontLoader() {

    }
}
