
package org.holoeverywhere.demo.fragments.tabber;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TabSwipeFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class TabsTabsSwipeFragment extends TabSwipeFragment {
    public static class TabFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView textView = new TextView(getSupportActivity());
            textView.setTextAppearance(getSupportActivity(), R.style.Holo_TextAppearance_Medium);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(getArguments().getInt("color"));
            textView.setText(getArguments().getCharSequence("text"));
            return textView;
        }
    }

    private static Bundle make(int i) {
        Bundle bundle = new Bundle();
        int color;
        CharSequence text;
        switch (i) {
            case 1:
                color = R.color.holo_blue_dark;
                text = "I'm perfect! Maybe...";
                break;
            case 2:
                color = R.color.holo_green_dark;
                text = "Love and dru... friends. Yea.";
                break;
            case 3:
                color = R.color.holo_red_dark;
                text = "I'm angry!!! Argh!!";
                break;
            default:
                return null;
        }
        bundle.putCharSequence("text", text);
        bundle.putInt("color", color);
        return bundle;
    }

    @Override
    public void onHandleTabs() {
        addTab("Tab #1", TabFragment.class, make(1));
        addTab("Tab #2", TabFragment.class, make(2));
        addTab("Tab #3", TabFragment.class, make(3));
    }
}
