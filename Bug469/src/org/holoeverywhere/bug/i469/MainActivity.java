
package org.holoeverywhere.bug.i469;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeActivity;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends TabSwipeActivity {
    public static class DummyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView view = new TextView(getSupportActivity());
            view.setGravity(Gravity.CENTER);
            view.setText(getArguments().getString("text"));
            return view;
        }
    }

    @Override
    protected void onHandleTabs() {
        Bundle bundle = new Bundle();
        bundle.putString("text", "Text on first page");
        addTab("First", DummyFragment.class, bundle);

        bundle = new Bundle();
        bundle.putString("text", "Text on second page");
        addTab("Second", DummyFragment.class, bundle);
    }
}
