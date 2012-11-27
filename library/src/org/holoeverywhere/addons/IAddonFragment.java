
package org.holoeverywhere.addons;

import org.holoeverywhere.app.Fragment;

public abstract class IAddonFragment extends IAddonBase {
    private Fragment fragment;

    public IAddonFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
