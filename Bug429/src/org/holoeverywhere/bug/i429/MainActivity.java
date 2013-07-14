package org.holoeverywhere.bug.i429;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;

import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.ContextMenu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnCreateContextMenuListener(this);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				registerForContextMenu(v);
				openContextMenu(v);
				unregisterForContextMenu(v);
			}

		});
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(1, v.getId(), 0, "Option 0");
		menu.add(1, v.getId(), 1, "Option 1");
		menu.add(1, v.getId(), 2, "Option 2");
	}
}