
package org.holoeverywhere.demo.fragments.lists;

import org.holoeverywhere.app.ListFragment;

public abstract class ListsBaseFragment extends ListFragment {
    protected CharSequence getTitle() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        CharSequence title = getTitle();
        if (title != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }
}
