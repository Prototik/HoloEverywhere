
package org.holoeverywhere.addon;

import android.content.Context;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.ThemeManager.ThemeSetter;
import org.holoeverywhere.app.ContextThemeWrapperPlus;
import org.holoeverywhere.util.WeaklyMap;

import java.util.Map;

/**
 * Helper for handling custom themes for required parts of library, like view themes in preferences addon
 */
public class IAddonThemes implements ThemeSetter {
    private final ThemeResolver mDefaultThemeResolver = new ThemeResolver() {
        @Override
        public int resolveThemeForContext(Context context, int invalidTheme) {
            int theme = ThemeManager.getThemeType(context);
            if (theme == ThemeManager.INVALID) {
                theme = invalidTheme & ThemeManager.getThemeMask();
                if (theme == 0) {
                    theme = ThemeManager.DARK;
                }
            }
            theme |= mThemeFlag;
            return ThemeManager.getThemeResource(theme, false);
        }
    };
    private final int mThemeFlag;
    private Map<Context, AddonThemeWrapper> mContexts;
    private int mDarkTheme = -1;
    private int mLightTheme = -1;
    private int mMixedTheme = -1;

    public IAddonThemes() {
        this(true);
    }

    public IAddonThemes(boolean createFlag) {
        mThemeFlag = createFlag ? ThemeManager.makeNewFlag() : -1;
        ThemeManager.registerThemeSetter(this);
    }

    public int getTheme(int themeType) {
        if (themeType == ThemeManager.DARK) {
            return mDarkTheme;
        } else if (themeType == ThemeManager.LIGHT) {
            return mLightTheme;
        } else if (themeType == ThemeManager.MIXED) {
            return mMixedTheme;
        } else {
            return 0;
        }
    }

    public Context context(Context context) {
        return context(context, ThemeManager.DARK);
    }

    public Context context(Context context, int invalidTheme) {
        return context(context, invalidTheme, mDefaultThemeResolver);
    }

    public Context context(Context context, int invalidTheme, ThemeResolver themeResolver) {
        if (context instanceof AddonThemeWrapper) {
            return context;
        }
        AddonThemeWrapper wrapper = null;
        if (mContexts != null) {
            wrapper = mContexts.get(context);
        }
        if (wrapper == null) {
            final int theme = themeResolver.resolveThemeForContext(context, invalidTheme);
            if (theme == 0) {
                return null;
            }
            wrapper = new AddonThemeWrapper(context, theme);
            if (mContexts == null) {
                mContexts = new WeaklyMap<Context, AddonThemeWrapper>();
            }
            mContexts.put(context, wrapper);
        }
        return wrapper;
    }

    public int getDarkTheme() {
        return mDarkTheme;
    }

    public void setDarkTheme(int darkTheme) {
        mDarkTheme = darkTheme;
        setupThemes();
    }

    public int getLightTheme() {
        return mLightTheme;
    }

    public void setLightTheme(int lightTheme) {
        mLightTheme = lightTheme;
        setupThemes();
    }

    public int getMixedTheme() {
        return mMixedTheme;
    }

    public void setMixedTheme(int mixedTheme) {
        mMixedTheme = mixedTheme;
        setupThemes();
    }

    public int getThemeFlag() {
        return mThemeFlag;
    }

    public void map(int darkTheme, int lightTheme, int mixedTheme) {
        mDarkTheme = darkTheme;
        mLightTheme = lightTheme;
        mMixedTheme = mixedTheme;
        setupThemes();
    }

    @Override
    public void setupThemes() {
        ThemeManager.map(mThemeFlag | ThemeManager.DARK, mDarkTheme);
        ThemeManager.map(mThemeFlag | ThemeManager.LIGHT, mLightTheme);
        ThemeManager.map(mThemeFlag | ThemeManager.MIXED, mMixedTheme);
    }

    public Context unwrap(Context context) {
        if (context == null) {
            return null;
        }
        while (context instanceof AddonThemeWrapper) {
            context = ((AddonThemeWrapper) context).getBaseContext();
        }
        return context;
    }

    public interface ThemeResolver {
        public int resolveThemeForContext(Context context, int invalidTheme);
    }

    private static final class AddonThemeWrapper extends ContextThemeWrapperPlus {
        public AddonThemeWrapper(Context base, int themeres) {
            super(base, themeres);
        }
    }
}
