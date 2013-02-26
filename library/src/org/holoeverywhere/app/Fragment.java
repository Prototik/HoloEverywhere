
package org.holoeverywhere.app;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.addon.AddonSherlock;
import org.holoeverywhere.addon.AddonSherlock.AddonSherlockF;
import org.holoeverywhere.addon.IAddon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app._HoloFragment;
import android.view.View;

public class Fragment extends _HoloFragment {
    public static <T extends Fragment> T instantiate(Class<T> clazz) {
        return instantiate(clazz, null);
    }

    public static <T extends Fragment> T instantiate(Class<T> clazz, Bundle args) {
        try {
            T fragment = clazz.newInstance();
            if (args != null) {
                args.setClassLoader(clazz.getClassLoader());
                fragment.setArguments(args);
            }
            return fragment;
        } catch (Exception e) {
            throw new InstantiationException("Unable to instantiate fragment " + clazz
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        }
    }

    @Deprecated
    public static Fragment instantiate(Context context, String fname) {
        return instantiate(context, fname, null);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static Fragment instantiate(Context context, String fname, Bundle args) {
        try {
            return instantiate((Class<? extends Fragment>) Class.forName(fname, true,
                    context.getClassLoader()), args);
        } catch (Exception e) {
            throw new InstantiationException("Unable to instantiate fragment " + fname
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        }
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

    @Deprecated
    public Activity getSherlockActivity() {
        return (Activity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (IAddon<?, ?> addon : addons) {
            addon.fragment(this).onCreate(savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        for (IAddon<?, ?> addon : addons) {
            addon.fragment(this).onViewCreated(view);
        }
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

    public AddonSherlockF requireSherlock() {
        return requireAddon(AddonSherlock.class).fragment(this);
    }
}
