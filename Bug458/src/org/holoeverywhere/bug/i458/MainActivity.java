
package org.holoeverywhere.bug.i458;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Spinner;

import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        spinner.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
    }
}
