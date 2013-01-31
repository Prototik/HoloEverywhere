
package org.holoeverywhere.preference;

import org.holoeverywhere.app.Application.Config.PreferenceImpl;
import org.holoeverywhere.preference.PreferenceManagerHelper.PreferenceManagerImpl;

import android.content.Context;

public class _PreferenceManagerImpl implements PreferenceManagerImpl {
    @Override
    public SharedPreferences getDefaultSharedPreferences(Context context, PreferenceImpl impl) {
        return PreferenceManager.getDefaultSharedPreferences(context, impl);
    }

    @Override
    public SharedPreferences wrap(Context context, PreferenceImpl impl, String name, int mode) {
        return PreferenceManager.wrap(context, impl, name, mode);
    }
}
