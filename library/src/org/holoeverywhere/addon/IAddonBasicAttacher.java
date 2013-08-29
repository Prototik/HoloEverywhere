
package org.holoeverywhere.addon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.holoeverywhere.addon.IAddon.Addon;

public final class IAddonBasicAttacher<V extends IAddonBase<Z>, Z> implements IAddonAttacher<V> {
    private final class AddonComparator implements Comparator<V> {
        @Override
        public int compare(V lhs, V rhs) {
            final int i1 = getWeight(lhs.getParent());
            final int i2 = getWeight(rhs.getParent());
            return i1 > i2 ? 1 : i1 < i2 ? -1 : 0;
        }

        private int getWeight(IAddon addon) {
            if (addon.getClass().isAnnotationPresent(Addon.class)) {
                return addon.getClass().getAnnotation(Addon.class).weight();
            }
            return -1;
        }
    }

    private final Map<Class<? extends IAddon>, V> mAddons = new HashMap<Class<? extends IAddon>, V>();
    private List<V> mAddonsList;
    private final Set<V> mAddonsSet = new TreeSet<V>(new AddonComparator());
    private boolean mLockAttaching = false;
    private Z mObject;

    public IAddonBasicAttacher(Z object) {
        mObject = object;
    }

    @Override
    public <T extends V> T addon(Class<? extends IAddon> clazz) {
        return addon(clazz, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends V> T addon(Class<? extends IAddon> clazz, boolean checkConflicts) {
        T addon = (T) mAddons.get(clazz);
        if (addon == null) {
            if (mLockAttaching) {
                throw AttachException.afterInit(mObject, clazz);
            }
            addon = IAddon.obtain(clazz, mObject);
            if (addon == null) {
                return null;
            }
            mAddons.put(clazz, addon);
            mAddonsSet.add(addon);
            mAddonsList = null;
            if (checkConflicts) {
                checkConflicts();
            }
        }
        return addon;
    }

    @Override
    public void addon(Collection<Class<? extends IAddon>> classes) {
        if (classes == null || classes.size() == 0) {
            return;
        }
        for (Class<? extends IAddon> clazz : classes) {
            addon(clazz, false);
        }
        checkConflicts();
    }

    @Override
    public <T extends V> T addon(String classname) {
        return addon(IAddon.makeAddonClass(classname));
    }

    private void checkConflicts() {
        Set<String> attachedAddons = new HashSet<String>();
        Map<String, String> conflictAddons = new HashMap<String, String>();
        for (V addon : mAddonsSet) {
            Class<? extends IAddon> clazz = addon.getParent().getClass();
            final String clazzName = clazz.getName();
            attachedAddons.add(clazzName);
            if (!clazz.isAnnotationPresent(Addon.class)) {
                continue;
            }
            Addon addonMeta = clazz.getAnnotation(Addon.class);
            for (String a : addonMeta.conflictStrings()) {
                conflictAddons.put(a, clazzName);
            }
            for (Class<? extends IAddon> a : addonMeta.conflict()) {
                conflictAddons.put(a.getName(), clazzName);
            }
        }
        StringBuilder builder = null;
        for (String addon : conflictAddons.keySet()) {
            if (attachedAddons.contains(addon)) {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append('\n');
                }
                builder.append(String.format(
                        "Found addon conflict: %s is cannot be attached together with %s",
                        addon, conflictAddons.get(addon)));
            }
        }
        if (builder != null) {
            throw AttachException.conflict(builder.toString());
        }
    }

    public void inhert(Collection<Class<? extends IAddon>> sourceClasses) {
        if (sourceClasses == null || sourceClasses.size() == 0) {
            return;
        }
        List<Class<? extends IAddon>> classes = new ArrayList<Class<? extends IAddon>>();
        for (Class<? extends IAddon> clazz : sourceClasses) {
            if (!clazz.isAnnotationPresent(Addon.class)) {
                continue;
            }
            Addon addon = clazz.getAnnotation(Addon.class);
            if (addon.inhert()) {
                classes.add(clazz);
            }
        }
        addon(classes);
    }

    public void inhert(IAddonAttacher<?> attacher) {
        inhert(attacher == null ? null : attacher.obtainAddonsList());
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
    public Collection<Class<? extends IAddon>> obtainAddonsList() {
        return new ArrayList<Class<? extends IAddon>>(mAddons.keySet());
    }

    @Override
    public boolean performAddonAction(AddonCallback<V> callback) {
        if (mAddonsSet.size() == 0) {
            return callback.post();
        }
        if (mAddonsList == null) {
            mAddonsList = new ArrayList<V>(mAddonsSet);
        }
        final int addonCount = mAddonsList.size();
        for (int i = 0; i < addonCount; i++) {
            if (callback.action(mAddonsList.get(i))) {
                return true;
            }
        }
        return callback.post();
    }

    public void reset() {
        mAddons.clear();
        mAddonsSet.clear();
        mAddonsList = null;
        mLockAttaching = false;
    }
}
