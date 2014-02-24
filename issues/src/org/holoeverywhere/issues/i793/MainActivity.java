package org.holoeverywhere.issues.i793;

import android.os.Bundle;

import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.issues.R;
import org.holoeverywhere.widget.ArrayAdapter;

import java.util.Arrays;

public class MainActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i793_main);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, android.R.id.text1, Arrays.asList(new String[]{
                "First", "Second", "Something else", "Too many rows!", "Nooop", "Where is my gamepad?",
                "I want to play", "Definetly", "Wow, xpad driver works!", "Shit, under wine - not very good",
                "But we have a Steam!", "Shit, very good", "Gradle-gradle-graaaaadle", "I know nothing",
                "Where is my phones?", "Below gamepad?", "I need for sleep..."
        })));
    }
}
