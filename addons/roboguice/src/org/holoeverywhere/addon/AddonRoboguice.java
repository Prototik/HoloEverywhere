
package org.holoeverywhere.addon;

import java.util.HashMap;

import org.holoeverywhere.addon.AddonRoboguice.AddonRoboguiceA;
import org.holoeverywhere.addon.AddonRoboguice.AddonRoboguiceF;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.RoboInjector;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Key;

public class AddonRoboguice extends IAddon<AddonRoboguiceA, AddonRoboguiceF> {
    public static class AddonRoboguiceA extends IAddonActivity {
        private EventManager mEventManager;

        private HashMap<Key<?>, Object> mScopedObjects = new HashMap<Key<?>, Object>();

        public AddonRoboguiceA(Activity activity) {
            super(activity);
        }

        public EventManager getEventManager() {
            return mEventManager;
        }

        public HashMap<Key<?>, Object> getScopedObjectMap() {
            return mScopedObjects;
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
                    RoboGuice.destroyInjector(getActivity());
                } catch (Exception e) {
                    Log.w(TAG, "OnDestroyInjector error", e);
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
            final RoboInjector injector = RoboGuice.getInjector(getActivity());
            mEventManager = injector.getInstance(EventManager.class);
            injector.injectMembersWithoutViews(getActivity());
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
        public AddonRoboguiceF(Fragment fragment) {
            super(fragment);
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
