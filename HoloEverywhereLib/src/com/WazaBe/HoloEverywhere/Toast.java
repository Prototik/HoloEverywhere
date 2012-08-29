package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Toast extends android.widget.Toast {

	private static final String TAG = "Toast";

	public static Toast makeText(Context context, CharSequence s, int duration) {
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		TextView view = new TextView(context);
		view.setText(s);
		view.setTextColor(0xFFDADADA);
		toast.setView(view);
		return toast;
	}

	public static Toast makeText(Context context, int resId, int duration) {
		return Toast.makeText(context, context.getResources().getString(resId),
				duration);
	}

	private View view;

	public Toast(Context context) {
		super(context);
	}

	@Override
	public void setText(CharSequence s) {
		if (view == null) {
			return;
		}
		try {
			((TextView) view).setText(s);
		} catch (ClassCastException e) {
			Log.e(TAG, "This Toast was not created with Toast.makeText", e);
		}
	}

	@Override
	public void setView(View view) {
		(this.view = view).setBackgroundResource(R.drawable.toast_frame);
		super.setView(view);
	}

}
