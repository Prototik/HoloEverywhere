package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.widget.TextView;

@Deprecated
/*
 * Use com.WazaBe.HoloEverywhere.Toast
 */
public class HoloToast extends Toast {
	@Deprecated
	/*
	 * Use com.WazaBe.HoloEverywhere.Toast
	 */
	public HoloToast(Context context) {
		super(context);
	}

	@Deprecated
	/*
	 * Use com.WazaBe.HoloEverywhere.Toast.makeText(Context, CharSequence, int)
	 */
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

	@Deprecated
	/*
	 * Use com.WazaBe.HoloEverywhere.Toast.makeText(Context, int, int)
	 */
	public static HoloToast makeText(Context context, int resId, int duration) {
		return HoloToast.makeText(context,
				context.getResources().getString(resId), duration);
	}

}
