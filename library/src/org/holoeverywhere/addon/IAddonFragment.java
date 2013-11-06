
package org.holoeverywhere.addon;

import android.os.Bundle;
import android.view.View;

import org.holoeverywhere.app.Fragment;

/**
 * Basic addon class which can handle creating of fragment and it's view
 */
public abstract class IAddonFragment extends IAddonBase<Fragment> {
    public void onCreate(Bundle savedInstanceState) {

    }

    public void onDestroyView() {

    }

    public void onPreCreate(Bundle savedInstanceState) {

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
