
package org.holoeverywhere.bug.i309;

import org.holoeverywhere.app.Activity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.show_message).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Crouton.makeText(this, "Sample text", Style.ALERT).show();
    }
}
