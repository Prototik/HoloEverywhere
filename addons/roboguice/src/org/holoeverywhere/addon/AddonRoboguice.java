
package org.holoeverywhere.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.inject.ViewListener;
import roboguice.inject._HoloViewInjector;
import roboguice.util.RoboContext;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.util.Modules;

public class AddonRoboguice extends IAddon {
    public static class AddonRoboguiceA extends IAddonActivity implements Provider<Activity>,
            Iterable<Module> {
        private static List<Module> sUserModules;
        private boolean mActivityPresentRobo;
        private EventManager mEventManager;
        private HoloInjector mInjector;
        private Context mRoboguiceContext;

        public EventManager getEventManager() {
            return mEventManager;
        }

        @Override
        public Iterator<Module> iterator() {
            List<Module> allModules = new ArrayList<Module>(1 + sUserModules.size());
            allModules.add(new HoloModule(get()));
            allModules.addAll(sUserModules);
            return allModules.iterator();
        }

        public HoloInjector obtainInjector() {
            if (mInjector != null) {
                return mInjector;
            }
            mActivityPresentRobo = get() instanceof RoboContext;
            mRoboguiceContext = mActivityPresentRobo ? get() : new RoboguiceContextWrapper(get());
            mInjector = new HoloInjector(mRoboguiceContext, Guice.createInjector(Modules.override(
                    RoboGuice.newDefaultRoboModule(get().getApplication())).with(this)));
            mEventManager = mInjector.getInstance(EventManager.class);
            return mInjector;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mEventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onAttach(Activity activity) {
            super.onAttach(activity);
            if (sUserModules == null) {
                sUserModules = new ArrayList<Module>();
                int id = activity.getResources().getIdentifier("roboguice_modules", "array",
                        activity.getPackageName());
                if (id <= 0) {
                    return;
                }
                for (String moduleName : activity.getResources().getStringArray(id)) {
                    try {
                        addModule((Class<? extends Module>) Class.forName(moduleName), activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onConfigurationChanged(Configuration oldConfig, Configuration newConfig) {
            mEventManager.fire(new OnConfigurationChangedEvent(oldConfig, newConfig));
        }

        @Override
        public void onContentChanged() {
            mInjector.injectViewMembers(get());
            mEventManager.fire(new OnContentChangedEvent());
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mEventManager.fire(new OnCreateEvent(savedInstanceState));
        }

        @Override
        public void onDestroy() {
            try {
                mEventManager.fire(new OnDestroyEvent());
            } catch (Exception e) {
                Log.w(TAG, "OnDestroy error", e);
            } finally {
                try {
                    RoboGuice.destroyInjector(mRoboguiceContext);
                } catch (Exception e) {
                    Log.w(TAG, "OnDestroyInjector error", e);
                } finally {
                    mRoboguiceContext = null;
                    mInjector = null;
                    mEventManager = null;
                }
            }
        }

        @Override
        public void onNewIntent(Intent intent) {
            mEventManager.fire(new OnNewIntentEvent());
        }

        @Override
        public void onPause() {
            mEventManager.fire(new OnPauseEvent());
        }

        @Override
        public void onPreCreate(Bundle savedInstanceState) {
            mInjector = obtainInjector();
            mInjector.injectMembers(get());
        }

        @Override
        public void onRestart() {
            mEventManager.fire(new OnRestartEvent());
        }

        @Override
        public void onResume() {
            mEventManager.fire(new OnResumeEvent());
        }

        @Override
        public void onStart() {
            mEventManager.fire(new OnStartEvent());
        }

        @Override
        public void onStop() {
            try {
                mEventManager.fire(new OnStopEvent());
            } catch (Exception e) {
                Log.w(TAG, "OnStop error", e);
            }
        }
    }

    public static class AddonRoboguiceF extends IAddonFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            getInjector(get()).injectMembers(get());
        }

        @Override
        public void onViewCreated(View view) {
            getInjector(get()).injectViewMembers(get());
        }
    }

    public static class HoloInjector extends ContextScopedRoboInjector {
        private final Context mContext;

        public HoloInjector(Context context, Injector delegateInjector) {
            super(context, delegateInjector, VIEW_LISTENER);
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public void injectViewMembers(android.app.Activity activity) {
            synchronized (ContextScope.class) {
                scope.enter(context);
                try {
                    _HoloViewInjector.inject(activity);
                } finally {
                    scope.exit(context);
                }
            }
        }
    }

    private static final class HoloModule extends AbstractModule {
        private final Activity mActivity;

        public HoloModule(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void configure() {
            bind(android.app.Activity.class).toInstance(mActivity);
            bind(android.view.LayoutInflater.class).toInstance(LayoutInflater.from(mActivity));
        }
    }

    private static final class RoboguiceContextWrapper extends ContextWrapper implements
            RoboContext {
        private final Map<Key<?>, Object> mScopedObjects = new HashMap<Key<?>, Object>();

        public RoboguiceContextWrapper(Context base) {
            super(base);
        }

        @Override
        public Map<Key<?>, Object> getScopedObjectMap() {
            return mScopedObjects;
        }
    }

    private static final String TAG = "Roboguice";
    private static final ViewListener VIEW_LISTENER = new ViewListener();

    public static void addModule(Class<? extends Module> clazz) {
        addModule(clazz, null);
    }

    private static void addModule(Class<? extends Module> clazz, Activity activity) {
        Module module;
        try {
            if (activity == null) {
                throw new NullPointerException();
            }
            module = clazz.getConstructor(Context.class).newInstance(activity);
        } catch (Exception e) {
            try {
                module = clazz.newInstance();
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        if (module != null) {
            addModule(module);
        }
    }

    public static void addModule(Module module) {
        if (module == null) {
            return;
        }
        if (AddonRoboguiceA.sUserModules == null) {
            AddonRoboguiceA.sUserModules = new ArrayList<Module>();
        }
        AddonRoboguiceA.sUserModules.add(module);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Context & RoboContext> T getContext(Activity activity) {
        if (activity instanceof RoboContext) {
            return (T) activity;
        } else {
            return (T) getInjector(activity).mContext;
        }
    }

    public static <T extends Context & RoboContext> T getContext(Fragment fragment) {
        return getContext(fragment.getSupportActivity());
    }

    public static HoloInjector getInjector(Activity activity) {
        AddonRoboguiceA addon = activity.addon(AddonRoboguice.class);
        return addon.obtainInjector();
    }

    public static HoloInjector getInjector(Fragment fragment) {
        return getInjector(fragment.getSupportActivity());
    }

    public AddonRoboguice() {
        register(Activity.class, AddonRoboguiceA.class);
        register(Fragment.class, AddonRoboguiceF.class);
    }
}
