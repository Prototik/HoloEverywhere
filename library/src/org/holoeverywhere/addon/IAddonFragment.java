
package org.holoeverywhere.addon;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;

public abstract class IAddonFragment extends IAddonBase {
    private Fragment fragment;

    public IAddonFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public Activity getActivity() {
        return fragment.getSupportActivity();
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void onViewCreated(View view) {

    }
}
