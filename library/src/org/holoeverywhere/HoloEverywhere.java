
package org.holoeverywhere;

import android.util.Log;

public class HoloEverywhere {
    /**
     * Main package of HoloEverywhere
     */
    public static final String PACKAGE;
    private static final String LOG_TAG = "HoloEverywhere";
    /**
     * When you call new activity it will be has parent activity theme
     */
    public static boolean ALWAYS_USE_PARENT_THEME;
    /**
     * Print some debug lines in Log
     */
    public static boolean DEBUG;
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
    /**
     * Save menu instance over calling Activity#supportInvalidateOptionsMenu()
     */
    public static boolean SAVE_MENU_INSTANCE_OVER_INVALIDATE;

    static {
        PACKAGE = HoloEverywhere.class.getPackage().getName();

        DEBUG = false;
        ALWAYS_USE_PARENT_THEME = false;
        NAMED_PREFERENCES = true;
        PREFERENCE_IMPL = PreferenceImpl.XML;
        SAVE_MENU_INSTANCE_OVER_INVALIDATE = false;
    }

    private HoloEverywhere() {
    }

    public static void warn(String message, Throwable throwable) {
        Log.w(LOG_TAG, message, throwable);
    }

    public static void warn(String message, Object... args) {
        warn(String.format(message, args), (Throwable) null);
    }

    public static void warn(Throwable throwable) {
        warn(null, throwable);
    }

    public static enum PreferenceImpl {
        JSON, XML
    }
}
