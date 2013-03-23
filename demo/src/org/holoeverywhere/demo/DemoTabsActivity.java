
package org.holoeverywhere.demo;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeActivity;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class DemoTabsActivity extends TabSwipeActivity {
    public static class TabFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView textView = new TextView(getSupportActivity());
            textView.setText("Position: " + getArguments().getInt("position"));
            return textView;
        }
    }

    private static Bundle make(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", i);
        return bundle;
    }

    @Override
    protected void onHandleTabs() {
        addTab("Tab #1", TabFragment.class, make(1));
        addTab("Tab #2", TabFragment.class, make(2));
        addTab("Tab #3", TabFragment.class, make(3));
    }
}
