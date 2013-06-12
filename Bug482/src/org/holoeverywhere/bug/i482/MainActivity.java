
package org.holoeverywhere.bug.i482;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeActivity;
import org.holoeverywhere.widget.Button;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends TabSwipeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((Button) findViewById(R.id.main_button)).setText("Text via code");
    }

    public static final class DummyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.child, container, false);
        }
    }

    @Override
    protected void onHandleTabs() {
        addTab("First", DummyFragment.class);
        addTab("Second", DummyFragment.class);
        addTab("Third", DummyFragment.class);
    }
}
