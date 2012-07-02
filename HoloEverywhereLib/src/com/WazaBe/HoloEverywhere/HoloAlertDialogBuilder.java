package com.WazaBe.HoloEverywhere;

import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HoloAlertDialogBuilder extends AlertDialog.Builder {
	private static class ButtonEntry implements
			Entry<CharSequence, OnClickListener> {
		private OnClickListener listener;
		private CharSequence text;

		public ButtonEntry(CharSequence text, OnClickListener listener) {
			this.text = text;
			this.listener = listener;
		}

		@Override
		public CharSequence getKey() {
			return text;
		}

		@Override
		public OnClickListener getValue() {
			return listener;
		}

		@Override
		public OnClickListener setValue(OnClickListener object) {
			return listener = object;
		}

	}

	private static class InternalListener implements
			android.view.View.OnClickListener {
		private OnClickListener listener;
		private int which;
		private HoloAlertDialogBuilder builder;

		public InternalListener(final HoloAlertDialogBuilder builder,
				OnClickListener listener, int which) {
			this.builder = builder;
			this.listener = listener;
			this.which = which;
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onClick(builder.lastDialog, which);
			}
			if (builder.lastDialog != null) {
				builder.lastDialog.dismiss();
				builder.lastDialog.cancel();
			}
		}

	}

	private ButtonEntry buttonNegative, buttonPositive, buttonNeutral;
	private final LinearLayout buttonsLayout;
	private AlertDialog lastDialog;
	private final Context mContext;
	private ImageView mIcon;
	private TextView mMessage, mTitle;
	private final boolean useNativeButtons;

	@SuppressLint("NewApi")
	public HoloAlertDialogBuilder(Context context) {
		this(context, VERSION.SDK_INT >= 14);
	}

	public HoloAlertDialogBuilder(Context context, boolean useNativeButtons) {
		super(context);
		mContext = context;
		buttonsLayout = new LinearLayout(mContext);
		buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

		this.useNativeButtons = useNativeButtons;
		// Using the full layout give me a strange top divider in Donut and
		// Eclair in when using setView. Using second idea breaks custom View.
		Boolean useFullLayout = true;

		if (useFullLayout) {
			View customTitle = View.inflate(mContext,
					R.layout.alert_dialog_title, null);
			mTitle = (TextView) customTitle.findViewById(R.id.alertTitle);
			FontLoader.loadFont(customTitle);
			mIcon = (ImageView) customTitle.findViewById(R.id.icon);
			setCustomTitle(customTitle);

			View customMessage = View.inflate(mContext,
					R.layout.alert_dialog_message, null);
			mMessage = (TextView) customMessage.findViewById(R.id.message);
			setView(customMessage);
		} else {
			View customView = View.inflate(mContext,
					R.layout.alert_dialog_holo, null);
			mTitle = (TextView) customView.findViewById(R.id.alertTitle);
			FontLoader.loadFont(customView);
			mIcon = (ImageView) customView.findViewById(R.id.icon);
			mMessage = (TextView) customView.findViewById(R.id.message);
			setView(customView);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public AlertDialog create() {
		lastDialog = super.create();
		if (content != null) {
			lastDialog.setView(content, 0, 0, 0, 0);
		}
		return lastDialog;
	}

	private void pushButton(ButtonEntry button, int which, int background) {
		if (button == null || button.getKey() == null) {
			return;
		}
		Button b = new Button(buttonsLayout.getContext());
		b.setBackgroundResource(background);
		b.setText(button.getKey());
		b.setOnClickListener(new InternalListener(this, button.getValue(),
				which));
		LayoutParams layoutParams = new LayoutParams(0,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		buttonsLayout.addView(b, layoutParams);
	}

	@Override
	public HoloAlertDialogBuilder setIcon(Drawable icon) {
		mIcon.setImageDrawable(icon);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setIcon(int drawableResId) {
		mIcon.setImageResource(drawableResId);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setMessage(CharSequence text) {
		mMessage.setText(text);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setMessage(int textResId) {
		mMessage.setText(textResId);
		return this;
	}

	@Override
	public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
		if (useNativeButtons) {
			return super.setNegativeButton(text, listener);
		} else {
			buttonNegative = new ButtonEntry(text, listener);
			return updateButtonsLayout();
		}
	}

	@Override
	public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
		if (useNativeButtons) {
			return super.setNeutralButton(text, listener);
		} else {
			buttonNeutral = new ButtonEntry(text, listener);
			return updateButtonsLayout();
		}
	}

	@Override
	public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
		if (useNativeButtons) {
			return super.setPositiveButton(text, listener);
		} else {
			buttonPositive = new ButtonEntry(text, listener);
			return updateButtonsLayout();
		}
	}

	@Override
	public HoloAlertDialogBuilder setTitle(CharSequence text) {
		mTitle.setText(text);
		return this;
	}

	@Override
	public HoloAlertDialogBuilder setTitle(int textResId) {
		mTitle.setText(textResId);
		return this;
	}

	private View content;

	@Override
	public Builder setView(View view) {
		FontLoader.loadFont(view);
		if (useNativeButtons) {
			return super.setView(view);
		} else {
			LinearLayout layout = new LinearLayout(buttonsLayout.getContext());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(view);
			try {
				((LinearLayout) buttonsLayout.getParent())
						.removeView(buttonsLayout);
			} catch (Exception e) {
			}
			layout.addView(buttonsLayout);
			content = layout;
			return this;
		}
	}

	private Builder updateButtonsLayout() {
		buttonsLayout.removeAllViews();
		TypedValue value = new TypedValue();
		mContext.getTheme().resolveAttribute(android.R.attr.windowBackground,
				value, true);
		boolean isDark = value.resourceId == R.drawable.background_holo_dark;
		int button = isDark ? R.drawable.ad_dark_button
				: R.drawable.ad_light_button, button_border = isDark ? R.drawable.ad_dark_button_border
				: R.drawable.ad_light_button_border;
		pushButton(
				buttonNegative,
				DialogInterface.BUTTON_NEGATIVE,
				(buttonNeutral == null || buttonNeutral.getKey() == null)
						&& (buttonPositive == null || buttonPositive.getKey() == null) ? button
						: button_border);
		pushButton(
				buttonNeutral,
				DialogInterface.BUTTON_NEUTRAL,
				buttonPositive == null || buttonPositive.getKey() == null ? button
						: button_border);
		pushButton(buttonPositive, DialogInterface.BUTTON_POSITIVE, button);
		FontLoader.loadFont(buttonsLayout);
		return this;
	}

}