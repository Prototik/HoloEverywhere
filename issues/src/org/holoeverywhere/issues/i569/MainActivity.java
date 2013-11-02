package org.holoeverywhere.issues.i569;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeActivity;
import org.holoeverywhere.widget.TextView;


public class MainActivity extends TabSwipeActivity {
    @Override
    public void onHandleTabs() {
        addTab("Tab #1", MyFragment.class, bundle("Random text"));
        addTab("Tab #2", MyFragment.class, bundle("Hotdog"));
        addTab("Tab #3", MyFragment.class, bundle("Coolcat"));
    }

    private Bundle bundle(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("text", s);
        return bundle;
    }

    public static final class MyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView tv = new TextView(inflater.getContext());
            tv.setText(getArguments().getString("text"));
            return tv;
        }
    }
}
