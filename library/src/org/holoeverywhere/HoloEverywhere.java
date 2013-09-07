
package org.holoeverywhere;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class HoloEverywhere {
    /**
     * Main package of HoloEverywhere
     */
    public static final String PACKAGE;
    /**
     * When you call new activity it will be has parent activity theme
     */
    public static boolean ALWAYS_USE_PARENT_THEME;
    /**
     * Print some debug lines in Log
     */
    public static boolean DEBUG;
    /**
     * Disable any overscroll effects for views
     */
    public static boolean DISABLE_OVERSCROLL_EFFECT;
    /**
     * Since 1.5 to preferences can be assigned id instead of key. But for
     * saving values in SharedPreferences using key, and it has next format:
     * <p/>
     * <pre>
     * preference_0x7fABCDEF
     * </pre>
     * <p/>
     * If it options true - key for the preference will be name of id row:
     * <p/>
     * <pre>
     *  &lt;Preference holo:id="@+id/myCoolPreference" /&gt;
     *  Key for SharedPreferences: myCoolPreference
     * </pre>
     */
    public static boolean NAMED_PREFERENCES;
    /**
     * Preference implementation using by default
     */
    public static PreferenceImpl PREFERENCE_IMPL;

    static {
        PACKAGE = HoloEverywhere.class.getPackage().getName();

        DEBUG = false;
        ALWAYS_USE_PARENT_THEME = false;
        NAMED_PREFERENCES = true;
        DISABLE_OVERSCROLL_EFFECT = VERSION.SDK_INT < VERSION_CODES.HONEYCOMB;
        PREFERENCE_IMPL = PreferenceImpl.XML;
    }

    private HoloEverywhere() {
    }

    public static enum PreferenceImpl {
        JSON, XML
    }
}
