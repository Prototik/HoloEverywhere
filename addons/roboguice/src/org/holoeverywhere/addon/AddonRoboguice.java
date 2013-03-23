
package org.holoeverywhere.addon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
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
import com.google.inject.util.Modules.OverriddenModuleBuilder;

public class AddonRoboguice extends IAddon {
    public static class AddonRoboguiceA extends IAddonActivity implements Provider<Activity> {
        private EventManager mEventManager;
        private HoloInjector mInjector;

        public EventManager getEventManager() {
            return mEventManager;
        }

        public HoloInjector obtainInjector() {
            if (mInjector != null) {
                return mInjector;
            }
            WeakReference<HoloInjector> reference = sInjectorsMap.get(get());
            mInjector = reference == null ? null : reference.get();
            if (mInjector == null) {
                mInjector = createInjector(get());
                sInjectorsMap.put(get(), reference);
            }
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
                mInjector = null;
                mEventManager = null;
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
        public void onViewCreated(View view, Bundle savedInstanceState) {
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

    private static final WeakHashMap<Context, WeakReference<HoloInjector>> sInjectorsMap;

    private static List<Module> sUserModules;

    private static final WeakHashMap<Context, WeakReference<RoboguiceContextWrapper>> sWrappersMap;
    private static final String TAG = "Roboguice";

    private static final ViewListener VIEW_LISTENER = new ViewListener();

    static {
        sWrappersMap = new WeakHashMap<Context, WeakReference<RoboguiceContextWrapper>>();
        sInjectorsMap = new WeakHashMap<Context, WeakReference<HoloInjector>>();
    }

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
        if (sUserModules == null) {
            sUserModules = new ArrayList<Module>();
        }
        sUserModules.add(module);
    }

    private static HoloInjector createInjector(final Context context) {
        OverriddenModuleBuilder builder = Modules.override(RoboGuice
                .newDefaultRoboModule((Application) context.getApplicationContext()));
        Module module;
        if (context instanceof Activity) {
            module = builder.with(new Iterable<Module>() {
                @Override
                public Iterator<Module> iterator() {
                    List<Module> allModules = new ArrayList<Module>(1 + sUserModules
                            .size());
                    allModules.add(new HoloModule((Activity) context));
                    allModules.addAll(sUserModules);
                    return allModules.iterator();
                }
            });
        } else {
            module = builder.with(sUserModules);
        }
        return new HoloInjector(context, Guice.createInjector(module));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Context & RoboContext> T getContext(Activity activity) {
        if (activity instanceof RoboContext) {
            return (T) activity;
        } else {
            return (T) getInjector(activity).mContext;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Context & RoboContext> T getContext(Context context) {
        if (context instanceof Activity) {
            return getContext((Activity) context);
        }
        if (context instanceof RoboContext) {
            return (T) context;
        }
        WeakReference<RoboguiceContextWrapper> reference = sWrappersMap.get(context);
        RoboguiceContextWrapper wrapper = reference == null ? null : reference.get();
        if (wrapper == null) {
            wrapper = new RoboguiceContextWrapper(context);
            sWrappersMap.put(context, new WeakReference<RoboguiceContextWrapper>(wrapper));
        }
        return (T) wrapper;
    }

    public static <T extends Context & RoboContext> T getContext(Fragment fragment) {
        return getContext(fragment.getSupportActivity());
    }

    public static HoloInjector getInjector(Activity activity) {
        AddonRoboguiceA addon = activity.addon(AddonRoboguice.class);
        return addon.obtainInjector();
    }

    public static HoloInjector getInjector(Context context) {
        if (context instanceof Activity) {
            return getInjector((Activity) context);
        }
        WeakReference<HoloInjector> reference = sInjectorsMap.get(context);
        HoloInjector injector = reference == null ? null : reference.get();
        if (injector == null) {
            injector = createInjector(context);
            sInjectorsMap.put(context, new WeakReference<HoloInjector>(injector));
        }
        return injector;
    }

    public static HoloInjector getInjector(Fragment fragment) {
        return getInjector(fragment.getSupportActivity());
    }

    public AddonRoboguice() {
        register(Activity.class, AddonRoboguiceA.class);
        register(Fragment.class, AddonRoboguiceF.class);
    }
}
