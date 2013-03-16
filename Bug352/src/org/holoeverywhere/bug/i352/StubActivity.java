
package org.holoeverywhere.bug.i352;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StubActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        Button button = new Button(this);
        button.setText("Open");
        button.setOnClickListener(this);
        setContentView(button);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
