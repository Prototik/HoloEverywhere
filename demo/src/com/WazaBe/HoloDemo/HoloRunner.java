package com.WazaBe.HoloDemo;

import com.WazaBe.HoloDemo.sherlock.SHoloDemoActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;

public class HoloRunner extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
		Intent intent = new Intent(this,
				VERSION.SDK_INT >= 7 ? SHoloDemoActivity.class
						: HoloDemoActivity.class);
		startActivity(intent);
	}
}
