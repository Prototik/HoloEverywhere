
package org.holoeverywhere.bug.i361;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ListActivity {
    private class MyAdapter extends ArrayAdapter<String> {
        MyAdapter() {
            super(MainActivity.this, 0, 0);
            for (int i = 1; i <= 50; i++) {
                add("Item #" + i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MyCustomWidget(MainActivity.this);
            }
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position));
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new MyAdapter());
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
