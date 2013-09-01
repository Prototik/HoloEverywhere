
package org.holoeverywhere.preference;

import android.content.Context;

import org.holoeverywhere.HoloEverywhere.PreferenceImpl;
import org.holoeverywhere.preference.PreferenceManagerHelper.PreferenceManagerImpl;

public class _PreferenceManagerImpl implements PreferenceManagerImpl {
    static {
        PreferenceInit.init();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(Context context, PreferenceImpl impl) {
        return PreferenceManager.getDefaultSharedPreferences(context, impl);
    }

    @Override
    public int obtainThemeTag() {
        return PreferenceInit.THEME_FLAG;
    }

    @Override
    public SharedPreferences wrap(Context context, PreferenceImpl impl, String name, int mode) {
        return PreferenceManager.wrap(context, impl, name, mode);
    }
}
