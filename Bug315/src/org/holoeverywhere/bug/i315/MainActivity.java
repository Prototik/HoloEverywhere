
package org.holoeverywhere.bug.i315;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends ListActivity {
    private static final String[] ITEMS = {
            "Jack", "Christophe", "Sergey", "Jeremy", "Alex"
    };

    private View makeFooterView(int i) {
        return makeView("Footer: " + i);
    }

    private View makeHeaderView(int i) {
        return makeView("Header: " + i);
    }

    private View makeView(String text) {
        View view = getLayoutInflater().inflate(R.layout.row, null);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(text);
        EditText editText = (EditText) view.findViewById(android.R.id.text2);
        editText.setHint(text);
        editText.setText("Text: " + text);
        return view;
    }

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        getListView().setForceHeaderListAdapter(true);
        getListView().setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.simple_list_item_1, android.R.id.text2, ITEMS));

        getListView().addHeaderView(makeHeaderView(1));
        getListView().addHeaderView(makeHeaderView(2));
        getListView().addHeaderView(makeHeaderView(3));

        getListView().addFooterView(makeFooterView(1));
        getListView().addFooterView(makeFooterView(2));
        getListView().addFooterView(makeFooterView(3));
    }
}
