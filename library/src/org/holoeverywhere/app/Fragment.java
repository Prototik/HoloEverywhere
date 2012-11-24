
package org.holoeverywhere.app;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.addon.AddonSherlock;
import org.holoeverywhere.addon.AddonSherlock.SherlockF;
import org.holoeverywhere.addons.IAddon;

import android.support.v4.app._HoloFragment;

public class Fragment extends _HoloFragment {
    @Deprecated
    public Activity getSherlockActivity() {
        return (Activity) getActivity();
    }

    private final List<IAddon<?, ?>> addons = new ArrayList<IAddon<?, ?>>();

    public void attachAddon(IAddon<?, ?> addon) {
        if (!addons.contains(addon)) {
            addons.add(addon);
        }
    }

    public void detachAddon(IAddon<?, ?> addon) {
        addons.remove(addon);
    }

    @SuppressWarnings("unchecked")
    public <T extends IAddon<?, ?>> T findAddon(Class<T> clazz) {
        for (IAddon<?, ?> addon : addons) {
            if (addon.getClass().isAssignableFrom(clazz)) {
                return (T) addon;
            }
        }
        return null;
    }

    public <T extends IAddon<?, ?>> T requireAddon(Class<T> clazz) {
        T t = findAddon(clazz);
        if (t == null) {
            try {
                t = clazz.newInstance();
                t.addon(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public SherlockF requireSherlock() {
        return requireAddon(AddonSherlock.class).fragment(this);
    }
}
