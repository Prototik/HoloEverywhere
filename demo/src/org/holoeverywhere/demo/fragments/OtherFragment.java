
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.GridFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.fragments.lists.ListsFragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class OtherFragment extends BaseOtherFragment {
    @Override
    public void onHandleData() {
        addItem("Lists", ListsFragment.class);
    }
}
