
package org.holoeverywhere.addon;

import java.util.Map;
import java.util.WeakHashMap;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

public abstract class IAddon<A extends IAddonActivity, F extends IAddonFragment> {
    private final Map<Object, Object> statesMap = new WeakHashMap<Object, Object>();

    public A activity(Activity activity) {
        A addon = get(activity);
        if (addon == null) {
            addon = createAddon(activity);
            put(activity, addon);
        }
        return addon;
    }

    public void addon(Activity activity) {
        activity.attachAddon(this);
    }

    public void addon(Fragment fragment) {
        fragment.attachAddon(this);
    }

    public A createAddon(Activity activity) {
        return null;
    }

    public F createAddon(Fragment fragment) {
        return null;
    }

    public F fragment(Fragment fragment) {
        F addon = get(fragment);
        if (addon == null) {
            addon = createAddon(fragment);
            put(fragment, addon);
        }
        return addon;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        return (T) statesMap.get(key);
    }

    public void put(Object key, Object value) {
        statesMap.put(key, value);
    }
}
