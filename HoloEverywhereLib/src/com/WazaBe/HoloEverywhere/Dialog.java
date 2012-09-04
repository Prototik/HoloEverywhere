package com.WazaBe.HoloEverywhere;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class Dialog extends android.app.Dialog {
	private View contentView;

	public Dialog(Context context) {
		super(context);
	}

	public Dialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(FontLoader.loadFont(view), params);
	}

	public View getContentView() {
		return contentView;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(contentView = FontLoader.inflate(getContext(),
				layoutResID));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(contentView = FontLoader.loadFont(view));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(contentView = FontLoader.loadFont(view), params);
	}

	@Override
	public void setTitle(CharSequence text) {
		super.setTitle(text);
		if (contentView != null) {
			TextView title = (TextView) contentView
					.findViewById(R.id.alertTitle);
			if (title != null) {
				title.setText(text);
			}
		}
	}
}