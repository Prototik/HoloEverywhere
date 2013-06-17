
package org.holoeverywhere.bug.i495;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final int choiceMode = ((ListView) findViewById(android.R.id.list)).getChoiceMode();
        CharSequence text;
        switch (choiceMode) {
            case ListView.CHOICE_MODE_NONE:
                text = "DAMN, CHOICE MODE IS NONE!";
                break;
            case ListView.CHOICE_MODE_SINGLE:
                text = "Wtf, single?";
                break;
            case ListView.CHOICE_MODE_MULTIPLE:
                text = "No-no-no, David Blane, no-no-no!";
                break;
            case ListView.CHOICE_MODE_MULTIPLE_MODAL:
                text = "Everything is ok!";
                break;
            default:
                // I'm idiot
                finish();
                return;
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
