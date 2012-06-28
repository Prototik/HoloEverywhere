package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HoloToast extends Toast {

	public HoloToast(Context context) {
		super(context);
	}

	private View view;

	@Override
	public void setView(View view) {
		(this.view = view).setBackgroundResource(R.drawable.toast_frame);
		super.setView(view);
	}

	@Override
	public void setText(CharSequence s) {
		if (view == null) {
			return;
		}
		try {
			((TextView) view).setText(s);
		} catch (ClassCastException e) {
			Log.e("HoloToast",
					"This HoloToast was not created with HoloToast.makeText", e);
		}
	}

	public static HoloToast makeText(Context context, CharSequence s,
			int duration) {
		HoloToast toast = new HoloToast(context);
		toast.setDuration(duration);
		TextView view = new TextView(context);
		view.setText(s);
		view.setTextColor(0xFFDADADA);
		toast.setView(view);
		return toast;
	}

	public static HoloToast makeText(Context context, int resId, int duration) {
		return makeText(context, context.getResources().getString(resId),
				duration);
	}

}
