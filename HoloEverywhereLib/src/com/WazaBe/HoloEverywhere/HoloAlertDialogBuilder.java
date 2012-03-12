package com.WazaBe.HoloEverywhere;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HoloAlertDialogBuilder extends AlertDialog.Builder {

	private final Context mContext;
	private TextView mTitle;
	private ImageView mIcon;
	private TextView mMessage;

	public HoloAlertDialogBuilder(Context context) {
		super(context);
		mContext = context;

		View customTitle = View.inflate(mContext, R.layout.alert_dialog_holo,
				null);
		mTitle = (TextView) customTitle.findViewById(R.id.alertTitle);
		mIcon = (ImageView) customTitle.findViewById(R.id.icon);
		//setCustomTitle(customTitle);
		// View customMessage = View.inflate(mContext,
		// R.layout.alert_dialog_message, null);
		mMessage = (TextView) customTitle.findViewById(R.id.message);
		setView(customTitle);
	}

	@Override
	public HoloAlertDialogBuilder setTitle(int textResId) {
		mTitle.setText(textResId);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setTitle(CharSequence text) {
		mTitle.setText(text);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setMessage(int textResId) {
		mMessage.setText(textResId);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setMessage(CharSequence text) {
		mMessage.setText(text);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setIcon(int drawableResId) {
		mIcon.setImageResource(drawableResId);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setIcon(Drawable icon) {
		mIcon.setImageDrawable(icon);
		return this;
	}

}