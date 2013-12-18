package org.holoeverywhere.issues.i701;

import android.os.Bundle;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Holo_Issues_I701_Theme);
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Title?")
                .setView(getLayoutInflater().inflate(R.layout.i701_dialog))
                .show();
    }
}
