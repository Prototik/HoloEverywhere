
package org.holoeverywhere.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IAddonBasicAttacher<V extends IAddonBase<Z>, Z> implements IAddonAttacher<V> {
    private final Map<Class<? extends IAddon>, V> mAddons = new HashMap<Class<? extends IAddon>, V>();
    private final List<V> mAddonsList = new ArrayList<V>();
    private boolean mLockAttaching = false;

    private Z mObject;

    public IAddonBasicAttacher(Z object) {
        mObject = object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends V> T addon(Class<? extends IAddon> clazz) {
        T addon = (T) mAddons.get(clazz);
        if (addon == null) {
            if (mLockAttaching) {
                throw new AttachException(mObject, clazz);
            }
            mAddons.put(clazz, addon = IAddon.obtain(clazz, mObject));
            mAddonsList.add(addon);
        }
        return addon;
    }

    @Override
    public <T extends V> T addon(String classname) {
        return addon(IAddon.makeAddonClass(classname));
    }

    @Override
    public boolean isAddonAttached(Class<? extends IAddon> clazz) {
        return mAddons.containsKey(clazz);
    }

    @Override
    public void lockAttaching() {
        mLockAttaching = true;
    }

    @Override
    public boolean performAddonAction(AddonCallback<V> callback) {
        if (mAddons.size() == 0) {
            return false;
        }
        final int size = mAddonsList.size();
        callback.pre();
        boolean result = false;
        for (int i = 0; i < size; i++) {
            result = callback.performAction(mAddonsList.get(i));
            if (callback.mStopped) {
                return result;
            }
        }
        callback.post();
        return true;
    }
}
