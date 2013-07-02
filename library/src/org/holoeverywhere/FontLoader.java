
package org.holoeverywhere;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

public class FontLoader {
    public static class Font implements Cloneable {
        private Context mContext;
        private String mFontFamily;
        private int mFontStyle;
        private boolean mLockModifing = false;
        private Typeface mTypeface;
        private boolean mTypefaceLoaded = false;

        public Font() {
        }

        public Font(Font font) {
            mContext = font.mContext;
            mFontStyle = font.mFontStyle;
            mTypeface = font.mTypeface;
            mTypefaceLoaded = font.mTypefaceLoaded;
            mFontFamily = font.mFontFamily;
        }

        protected final void assertContext() {
            if (mContext == null) {
                throw new IllegalStateException(
                        "Cannot load typeface without attaching font instance to FontLoader");
            }
        }

        protected final void assertModifing() {
            if (mLockModifing) {
                throw new IllegalStateException(
                        "Cannot modify typeface after attaching to FontCollector");
            }
        }

        @Override
        public Font clone() {
            return new Font(this);
        }

        public final Context getContext() {
            return mContext;
        }

        public String getFontFamily() {
            return mFontFamily;
        }

        public int getFontStyle() {
            return mFontStyle;
        }

        public Typeface getTypeface(String fontFamily, int fontStyle) {
            if (!mTypefaceLoaded) {
                mTypeface = loadTypeface();
                mTypefaceLoaded = true;
            }
            return mTypeface;
        }

        public Typeface loadTypeface() {
            return null;
        }

        public void lock() {
            mLockModifing = true;
        }

        protected final void resetTypeface() {
            mTypeface = null;
            mTypefaceLoaded = false;
        }

        public Font setFontFamily(String fontFamily) {
            assertModifing();
            mFontFamily = fontFamily;
            return this;
        }

        public Font setFontStyle(int fontStyle) {
            mFontStyle = fontStyle;
            return this;
        }
    }

    public static class FontCollector extends Font {
        private static final String DEFAULT_FONT_FAMILY = "FONT-FAMILY-DEFAULT";
        private boolean mAllowAnyFontFamily;
        private Font mDefaultFont;
        private final List<Font> mFonts;
        private Font mLastUsedFont;

        public FontCollector() {
            mFonts = new ArrayList<Font>();
        }

        public FontCollector(Font font) {
            super(font);
            if (font instanceof FontCollector) {
                FontCollector fontCollector = (FontCollector) font;
                mFonts = new ArrayList<Font>(fontCollector.mFonts);
                mAllowAnyFontFamily = fontCollector.mAllowAnyFontFamily;
                if (fontCollector.mDefaultFont != null) {
                    mDefaultFont = fontCollector.mDefaultFont.clone();
                }
            } else {
                mFonts = new ArrayList<Font>();
            }
        }

        public FontCollector allowAnyFontFamily() {
            mAllowAnyFontFamily = true;
            return this;
        }

        public FontCollector asDefaultFont() {
            mDefaultFont = mLastUsedFont;
            return this;
        }

        @Override
        public FontCollector clone() {
            return new FontCollector(this);
        }

        public Font getDefaultFont() {
            return mDefaultFont;
        }

        private Typeface getTypeface(Font font, String fontFamily, int fontStyle) {
            font.mContext = getContext();
            return font.getTypeface(fontFamily, fontStyle);
        }

        @Override
        public Typeface getTypeface(String fontFamily, int fontStyle) {
            if (fontFamily == null) {
                fontFamily = DEFAULT_FONT_FAMILY;
            }
            for (int i = 0; i < mFonts.size(); i++) {
                Font font = mFonts.get(i);
                if ((mAllowAnyFontFamily || fontFamily.equals(font.mFontFamily))
                        && font.mFontStyle == fontStyle) {
                    return getTypeface(font, fontFamily, fontStyle);
                }
            }
            if (mDefaultFont != null) {
                mDefaultFont.mContext = getContext();
                return getTypeface(mDefaultFont, fontFamily, fontStyle);
            }
            return null;
        }

        public FontCollector register(Font font) {
            if (font == null) {
                return this;
            }
            font.lock();
            mFonts.add(font);
            mLastUsedFont = font;
            return this;
        }

        public FontCollector setDefaultFont(Font defaultFont) {
            mDefaultFont = defaultFont;
            if (defaultFont != null) {
                setFontFamily(defaultFont.getFontFamily());
                setFontStyle(defaultFont.getFontStyle());
            }
            return this;
        }

        public FontCollector unregister(Font font) {
            mFonts.remove(font);
            return this;
        }

        public FontCollector unregister(String fontFamily, int fontStyle) {
            for (int i = 0; i < mFonts.size(); i++) {
                final Font font = mFonts.get(i);
                if (FontLoader.equals(fontFamily, font.mFontFamily) && font.mFontStyle == fontStyle) {
                    mFonts.remove(font);
                    return this;
                }
            }
            return this;
        }
    }

    public static interface FontStyleProvider {
        public String getFontFamily();

        public int getFontStyle();

        public void setFontStyle(String fontFamily, int fontStyle);

        public void setTypeface(Typeface typeface);
    }

    public static class RawFont extends Font {
        private int mRawResourceId;

        public RawFont(Font font) {
            super(font);
            if (font instanceof RawFont) {
                mRawResourceId = ((RawFont) font).mRawResourceId;
            }
        }

        public RawFont(int rawResourceId) {
            mRawResourceId = rawResourceId;
        }

        @Override
        public RawFont clone() {
            return new RawFont(this);
        }

        @Override
        public Typeface loadTypeface() {
            assertContext();
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

        public RawLazyFont(Font font) {
            super(font);
            if (font instanceof RawLazyFont) {
                mRawResourceName = ((RawLazyFont) font).mRawResourceName;
            }
        }

        public RawLazyFont(String rawResourceName) {
            super(0);
            mRawResourceName = rawResourceName;
        }

        @Override
        public RawLazyFont clone() {
            return new RawLazyFont(this);
        }

        @Override
        public Typeface loadTypeface() {
            assertContext();
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

    private static final class RobotoRawFont extends RawFont {
        public RobotoRawFont(int rawResourceId) {
            super(rawResourceId);
            setFontFamily("roboto");
        }
    }

    private static final class RobotoRawLazyFont extends RawLazyFont {
        public RobotoRawLazyFont(String rawResourceName) {
            super(rawResourceName);
            setFontFamily("roboto");
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
    private static List<String> sFontStyleKeys;
    private static final Map<String, Integer> sFontStyleMapping = new HashMap<String, Integer>();
    private static int sNextTextStyleOffset = 0;

    public static final int TEXT_STYLE_BLACK;
    public static final int TEXT_STYLE_BOLD;
    public static final int TEXT_STYLE_CONDENDSED;
    public static final int TEXT_STYLE_ITALIC;
    public static final int TEXT_STYLE_LIGHT;
    public static final int TEXT_STYLE_MEDIUM;
    public static final int TEXT_STYLE_NORMAL;
    public static final int TEXT_STYLE_THIN;
    static {
        TEXT_STYLE_NORMAL = 0;
        TEXT_STYLE_BOLD = registerTextStyle("bold");
        TEXT_STYLE_ITALIC = registerTextStyle("italic");
        TEXT_STYLE_BLACK = registerTextStyle("black");
        TEXT_STYLE_CONDENDSED = registerTextStyle("condensed");
        TEXT_STYLE_LIGHT = registerTextStyle("light");
        TEXT_STYLE_MEDIUM = registerTextStyle("medium");
        TEXT_STYLE_THIN = registerTextStyle("thin");

        ROBOTO_REGULAR = new RobotoRawFont(R.raw.roboto_regular)
                .setFontStyle(TEXT_STYLE_NORMAL);
        ROBOTO_BOLD = new RobotoRawFont(R.raw.roboto_bold)
                .setFontStyle(TEXT_STYLE_BOLD);
        ROBOTO_ITALIC = new RobotoRawFont(R.raw.roboto_italic)
                .setFontStyle(TEXT_STYLE_ITALIC);
        ROBOTO_BOLDITALIC = new RobotoRawFont(R.raw.roboto_bolditalic)
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_ITALIC);

        ROBOTO_BLACK = new RobotoRawLazyFont("roboto_black")
                .setFontStyle(TEXT_STYLE_BLACK);
        ROBOTO_BLACKITALIC = new RobotoRawLazyFont("roboto_blackitalic")
                .setFontStyle(TEXT_STYLE_BLACK | TEXT_STYLE_ITALIC);
        ROBOTO_BOLDCONDENSED = new RobotoRawLazyFont("roboto_boldcondensed")
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_CONDENDSED);
        ROBOTO_BOLDCONDENSEDITALIC = new RobotoRawLazyFont("roboto_boldcondenseditalic")
                .setFontStyle(TEXT_STYLE_BOLD | TEXT_STYLE_CONDENDSED | TEXT_STYLE_ITALIC);
        ROBOTO_CONDENSED = new RobotoRawLazyFont("roboto_condensed")
                .setFontStyle(TEXT_STYLE_CONDENDSED);
        ROBOTO_CONDENSEDITALIC = new RobotoRawLazyFont("roboto_condenseditalic")
                .setFontStyle(TEXT_STYLE_CONDENDSED | TEXT_STYLE_ITALIC);
        ROBOTO_LIGHT = new RobotoRawLazyFont("roboto_light")
                .setFontStyle(TEXT_STYLE_LIGHT);
        ROBOTO_LIGHTITALIC = new RobotoRawLazyFont("roboto_lightitalic")
                .setFontStyle(TEXT_STYLE_LIGHT | TEXT_STYLE_ITALIC);
        ROBOTO_MEDIUM = new RobotoRawLazyFont("roboto_medium")
                .setFontStyle(TEXT_STYLE_MEDIUM);
        ROBOTO_MEDIUMITALIC = new RobotoRawLazyFont("roboto_mediumitalic")
                .setFontStyle(TEXT_STYLE_MEDIUM | TEXT_STYLE_ITALIC);
        ROBOTO_THIN = new RobotoRawLazyFont("roboto_thin")
                .setFontStyle(TEXT_STYLE_THIN);
        ROBOTO_THINITALIC = new RobotoRawLazyFont("roboto_thinitalic")
                .setFontStyle(TEXT_STYLE_THIN | TEXT_STYLE_ITALIC);

        sDefaultFont = ROBOTO = new FontCollector().allowAnyFontFamily();
        ROBOTO.register(ROBOTO_REGULAR).asDefaultFont();
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
            final FontStyleProvider provider = (FontStyleProvider) view;
            final int fontStyle = provider.getFontStyle();
            final String fontFamily = provider.getFontFamily();
            if (view.getTag(R.id.fontLoaderFont) == font
                    && equals(view.getTag(R.id.fontLoaderFontStyle), fontStyle)
                    && equals(view.getTag(R.id.fontLoaderFontFamily), fontFamily)) {
                return;
            }
            provider.setTypeface(font.getTypeface(fontFamily, fontStyle));
            view.setTag(R.id.fontLoaderFont, font);
            view.setTag(R.id.fontLoaderFontStyle, fontStyle);
            view.setTag(R.id.fontLoaderFontFamily, fontFamily);
        }
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static Font getDefaultFont() {
        return sDefaultFont;
    }

    public static Object[] parseFontStyle(String string) {
        String fontFamily = null;
        int c = string.lastIndexOf('-');
        if (c > 0) {
            fontFamily = string.substring(0, c).toLowerCase(Locale.ENGLISH);
            string = string.substring(c + 1);
        }
        if (sFontStyleKeys == null) {
            sFontStyleKeys = new ArrayList<String>(sFontStyleMapping.keySet());
        }
        int textStyle = TEXT_STYLE_NORMAL;
        for (int i = 0; i < sFontStyleKeys.size(); i++) {
            final String key = sFontStyleKeys.get(i);
            if (string.contains(key)) {
                textStyle |= sFontStyleMapping.get(key);
            }
        }
        return new Object[] {
                textStyle,
                fontFamily == null && textStyle == TEXT_STYLE_NORMAL ? string : fontFamily
        };
    }

    public static int registerTextStyle(String modifier) {
        if (sNextTextStyleOffset >= 32) {
            throw new IllegalStateException("Too much text styles!");
        }
        final int flag = 1 << sNextTextStyleOffset++;
        sFontStyleMapping.put(modifier.toLowerCase(Locale.ENGLISH), flag);
        sFontStyleKeys = null;
        return flag;
    }

    public static void setDefaultFont(Font defaultFont) {
        sDefaultFont = defaultFont;
    }

    private FontLoader() {

    }
}
