
package org.holoeverywhere.bug.i286;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(this)
                .setItems(new String[] {
                        "Line #1", "Line #2", "Line #3", "Line #4", "Line #5"
                }, null)
                .setNegativeButton(android.R.string.cancel, null).show();
    }
}
