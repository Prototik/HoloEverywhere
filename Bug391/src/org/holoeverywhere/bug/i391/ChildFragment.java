
package org.holoeverywhere.bug.i391;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class ChildFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.child_fragment, container, false);
    }

    public static final String KEY_TITLE = ":title";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView secondaryText = (TextView) view.findViewById(R.id.secondaryText);
        String s = getArguments().getString(KEY_TITLE);
        title.setText(s);
        secondaryText.setText(String.format("Yeah, nested fragment in nested fragment:\n%s", s));
    }
}
