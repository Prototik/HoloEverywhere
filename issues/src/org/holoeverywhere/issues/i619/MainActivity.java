package org.holoeverywhere.issues.i619;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i619_main);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.v("i619", "Fragment: " + fragment);
    }
}
