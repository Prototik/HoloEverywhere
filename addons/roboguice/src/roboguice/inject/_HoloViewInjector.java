
package roboguice.inject;

import roboguice.inject.ViewListener.ViewMembersInjector;
import android.app.Activity;

public class _HoloViewInjector {
    public static ViewListener getViewListener(ContextScopedRoboInjector injector) {
        return injector.viewListener;
    }

    public static void inject(Activity activity) {
        ViewMembersInjector.injectViews(activity);
    }
}
