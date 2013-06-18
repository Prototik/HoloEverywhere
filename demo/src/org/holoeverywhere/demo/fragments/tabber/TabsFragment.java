
package org.holoeverywhere.demo.fragments.tabber;

import org.holoeverywhere.demo.fragments.OtherFragment;

public class TabsFragment extends OtherFragment {
    @Override
    protected void onHandleData() {
        addItem("Tab navigation", TabsTabsFragment.class);
        addItem("Tab + Swipe navigation", TabsTabsSwipeFragment.class);
    }
}
