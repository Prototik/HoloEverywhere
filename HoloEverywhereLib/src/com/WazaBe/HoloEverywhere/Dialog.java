package com.WazaBe.HoloEverywhere;

import com.WazaBe.HoloEverywhere.FontLoader.HoloFont;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class Dialog extends android.app.Dialog {

	private final Context mContext;
	private TextView mTitle;

	public Dialog(Context context) {
		this(context, 0);
	}

	public Dialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		View customView = View.inflate(mContext, R.layout.alert_dialog_holo,
				null);
		mTitle = (TextView) customView.findViewById(R.id.alertTitle);
		FontLoader.loadFont(customView, HoloFont.ROBOTO_REGULAR);
		setContentView(customView);
	}

	@Override
	public void setTitle(CharSequence text) {
		mTitle.setText(text);
	}

	@Override
	public void setTitle(int textResId) {
		mTitle.setText(textResId);
	}

}