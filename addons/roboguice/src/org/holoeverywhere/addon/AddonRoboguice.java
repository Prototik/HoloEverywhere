
package org.holoeverywhere.addon;

import java.util.HashMap;
import java.util.Map;

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
import roboguice.inject.ContentViewListener;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.inject.RoboInjector;
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
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.util.Modules;

public class AddonRoboguice extends IAddon {
    public static class AddonRoboguiceA extends IAddonActivity implements Provider<Activity> {
        private boolean mActivityPresentRobo;
        private EventManager mEventManager;
        private ContextScopedRoboInjector mInjector;
        private Injector mMainInjector;
        private Context mRoboguiceContext;

        public EventManager getEventManager() {
            return mEventManager;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mEventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
        }

        @Override
        public void onConfigurationChanged(Configuration oldConfig, Configuration newConfig) {
            mEventManager.fire(new OnConfigurationChangedEvent(oldConfig, newConfig));
        }

        @Override
        public void onContentChanged() {
            if (mActivityPresentRobo) {
                mInjector.injectViewMembers(get());
            } else {
                _HoloViewInjector.inject(mInjector, get());
            }
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
            mActivityPresentRobo = get() instanceof RoboContext;
            mRoboguiceContext = mActivityPresentRobo ? get() :
                    new RoboguiceContextWrapper(get());
            mInjector = (ContextScopedRoboInjector) RoboGuice.getInjector(mRoboguiceContext);
            mEventManager = mInjector.getInstance(EventManager.class);
            mMainInjector = Guice.createInjector(Modules.override(RoboGuice
                    .newDefaultRoboModule(get().getApplication())).with(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(android.app.Activity.class).toProvider(AddonRoboguiceA.this);
                    bind(Activity.class).toProvider(AddonRoboguiceA.this);
                }
            }));
            mMainInjector = new ContextScopedRoboInjector(mRoboguiceContext, mMainInjector,
                    _HoloViewInjector.getViewListener(mInjector));
            mMainInjector.injectMembers(get());
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
        private static final Injector getInjector(Activity activity, boolean main) {
            AddonRoboguiceA addon = activity.addon(AddonRoboguice.class);
            return main ? addon.mMainInjector : addon.mInjector;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            getInjector(get().getSupportActivity(), true).injectMembers(get());
        }

        @Override
        public void onViewCreated(View view) {
            ((RoboInjector) getInjector(get().getSupportActivity(), false))
                    .injectViewMembers(get());
        }
    }

    private static final class RoboguiceContextWrapper extends ContextWrapper implements
            RoboContext {
        /**
         * Bug in Roboguice
         */
        @Inject
        ContentViewListener ignored;

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

    public AddonRoboguice() {
        register(Activity.class, AddonRoboguiceA.class);
        register(Fragment.class, AddonRoboguiceF.class);
    }
}
