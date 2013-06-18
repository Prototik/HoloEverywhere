
package org.holoeverywhere.demo.fragments.tabber;

import android.os.Bundle;
import android.view.View;

public class TabsTabsFragment extends TabsTabsSwipeFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeEnabled(false);
    }
}
