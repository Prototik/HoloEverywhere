
package roboguice.inject;

import org.holoeverywhere.app.Activity;

import roboguice.inject.ViewListener.ViewMembersInjector;

public class _HoloViewInjector {
    public static void inject(ContextScopedRoboInjector injector, Activity activity) {
        synchronized (ContextScope.class) {
            injector.scope.enter(injector.context);
            try {
                inject(activity);
            } finally {
                injector.scope.exit(injector.context);
            }
        }
    }

    public static void inject(Activity activity) {
        ViewMembersInjector.injectViews(activity);
    }
}
