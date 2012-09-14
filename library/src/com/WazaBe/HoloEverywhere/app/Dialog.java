package com.WazaBe.HoloEverywhere.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.WazaBe.HoloEverywhere.FontLoader;

public class Dialog extends android.app.Dialog {
	public Dialog(Context context) {
		super(context);
	}

	public Dialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public Dialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.apply(view), params);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(FontLoader.inflate(getContext(), layoutResID));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(FontLoader.apply(view));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(FontLoader.apply(view), params);
	}
}
