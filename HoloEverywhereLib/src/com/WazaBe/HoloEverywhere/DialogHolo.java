package com.WazaBe.HoloEverywhere;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogHolo extends Dialog {

	private final Context mContext;
	private TextView mTitle;

	public DialogHolo(Context context) {
		super(context);
		mContext = context;

		View customView = View.inflate(mContext, R.layout.alert_dialog_holo,
				null);
		mTitle = (TextView) customView.findViewById(R.id.alertTitle);
		setContentView(customView);

	}

	public DialogHolo(Context context, int theme) {
		super(context, theme);
		mContext = context;
		View customView = View.inflate(mContext, R.layout.alert_dialog_holo,
				null);
		mTitle = (TextView) customView.findViewById(R.id.alertTitle);
		setContentView(customView);

	}

	@Override
	public void setTitle(int textResId) {
		mTitle.setText(textResId);
	}

	@Override
	public void setTitle(CharSequence text) {
		mTitle.setText(text);
	}

}