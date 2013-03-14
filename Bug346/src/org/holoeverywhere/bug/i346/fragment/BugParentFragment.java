
package org.holoeverywhere.bug.i346.fragment;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.bug.i346.R;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class BugParentFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.parent, container, false);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        TextView label = (TextView) view.findViewById(R.id.label);
        label.setText("Position: " + getArguments().getInt("position"));
    }
}
