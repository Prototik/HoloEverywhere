
package org.holoeverywhere.addon;

import java.util.HashMap;
import java.util.Map;

import org.holoeverywhere.addon.AddonRoboguice.AddonRoboguiceA;
import org.holoeverywhere.addon.AddonRoboguice.AddonRoboguiceF;
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
import roboguice.config.DefaultRoboModule;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.inject.RoboInjector;
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
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.util.Modules;

public class AddonRoboguice extends IAddon<AddonRoboguiceA, AddonRoboguiceF> {
    public static class AddonRoboguiceA extends IAddonActivity {
        private EventManager mEventManager;
        private RoboInjector mInjector;
        private Context mRoboguiceContext;
        private boolean mActivityPresentRobo;

        public AddonRoboguiceA(Activity activity) {
            super(activity);
        }

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
                mInjector.injectViewMembers(getActivity());
            } else if (mInjector instanceof ContextScopedRoboInjector) {
                _HoloViewInjector.inject((ContextScopedRoboInjector) mInjector, getActivity());
            } else {
                _HoloViewInjector.inject(getActivity());
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

        private Injector mMainInjector;

        @Override
        public void onPreCreate(Bundle savedInstanceState) {
            mActivityPresentRobo = getActivity() instanceof RoboContext;
            mRoboguiceContext = mActivityPresentRobo ? getActivity() :
                    new RoboguiceContextWrapper(getActivity());
            mInjector = RoboGuice.getInjector(mRoboguiceContext);
            mEventManager = mInjector.getInstance(EventManager.class);
            mMainInjector = Guice.createInjector(Modules.override(RoboGuice
                    .newDefaultRoboModule(getActivity().getApplication())).with(
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(android.app.Activity.class).toProvider(new Provider<Activity>() {
                                @Override
                                public Activity get() {
                                    return getActivity();
                                }
                            });
                        }
                    }));
            mMainInjector = new ContextScopedRoboInjector(mRoboguiceContext, mMainInjector,
                    new ViewListener());
            mMainInjector.injectMembers(getActivity());
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
        private static final RoboInjector getInjector(Activity activity) {
            return activity.requireAddon(AddonRoboguice.class).activity(activity).mInjector;
        }

        public AddonRoboguiceF(Fragment fragment) {
            super(fragment);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            getInjector(getActivity()).injectMembersWithoutViews(getFragment());
        }

        @Override
        public void onViewCreated(View view) {
            getInjector(getActivity()).injectViewMembers(getFragment());
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

    @Override
    public AddonRoboguiceA createAddon(Activity activity) {
        return new AddonRoboguiceA(activity);
    }

    @Override
    public AddonRoboguiceF createAddon(Fragment fragment) {
        return new AddonRoboguiceF(fragment);
    }
}
