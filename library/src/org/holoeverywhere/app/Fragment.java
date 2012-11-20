
package org.holoeverywhere.app;

import android.os.Build.VERSION;
import android.support.v4.app._HoloFragment;

public class Fragment extends _HoloFragment {
    @Deprecated
    public Activity getSherlockActivity() {
        return (Activity) getActivity();
    }

    @Override
    public boolean isABSSupport() {
        return VERSION.SDK_INT >= 7;
    }
}
