
package org.holoeverywhere.bug.i324;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Activity.Addons;
import org.holoeverywhere.widget.TextView;

import com.google.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.ContentViewListener;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;

@ContentView(R.layout.main)
@Addons(Activity.ADDON_ROBOGUICE)
public class MainActivity extends Activity {
    @InjectView(R.id.customTextView)
    public TextView mTextView;

    @Inject
    public ContentViewListener ignored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView.setText("Please wait 5 seconds");
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("I'm sexy and i know it.");
            }
        }, 5000);
    }
}
