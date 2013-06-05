
package org.holoeverywhere.app;

import org.holoeverywhere.SystemServiceManager;
import org.holoeverywhere.SystemServiceManager.SuperSystemService;

import android.content.Context;
import android.view.ContextThemeWrapper;

public class ContextThemeWrapperPlus extends ContextThemeWrapper implements SuperSystemService {
    private int mTheme;

    public ContextThemeWrapperPlus(Context base, int themeres) {
        super(base, themeres);
        mTheme = themeres;
    }

    @Override
    public Object getSystemService(String name) {
        return SystemServiceManager.getSystemService(this, name);
    }

    public int getThemeResource() {
        return mTheme;
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(mTheme = resid);
    }

    @Override
    public Object superGetSystemService(String name) {
        return super.getSystemService(name);
    }
}
