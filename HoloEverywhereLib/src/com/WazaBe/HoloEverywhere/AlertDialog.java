package com.WazaBe.HoloEverywhere;

import java.util.Map.Entry;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AlertDialog extends android.app.AlertDialog {
	public static class Builder extends android.app.AlertDialog.Builder {
		private ButtonEntry buttonNegative, buttonNeutral, buttonPositive;
		private boolean cancelable = true;
		private final Context context;
		private OnCancelListener onCancelListener;
		private CharSequence title, message;
		private boolean useNative = false;
		private View view;
		private int viewSpacingLeft = 0, viewSpacingTop = 0,
				viewSpacingRight = 0, viewSpacingBottom = 0;
		private Drawable icon;

		public Builder(Context context) {
			this(context, useNative());
		}

		public Builder(Context context, boolean useNative) {
			super(context);
			setUseNative(useNative);
			this.context = context;
		}

		public Context context() {
			return context;
		}

		@Override
		public AlertDialog create() {
			AlertDialog dialog = new AlertDialog(context(), isCancelable(),
					getOnCancelListener(), isUseNative());
			dialog.setTitle(title != null ? title : "");
			if (message != null) {
				dialog.setMessage(message);
			}
			if (icon != null) {
				dialog.setIcon(icon);
			}
			if (getPositiveButton() != null) {
				dialog.setButton(DialogInterface.BUTTON_POSITIVE,
						getPositiveButton().getKey(), getPositiveButton()
								.getValue());
			}
			if (getNeutralButton() != null) {
				dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
						getNeutralButton().getKey(), getNeutralButton()
								.getValue());
			}
			if (getNegativeButton() != null) {
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						getNegativeButton().getKey(), getNegativeButton()
								.getValue());
			}
			if (getView() != null) {
				dialog.setView(getView(), getViewSpacingLeft(),
						getViewSpacingTop(), getViewSpacingRight(),
						getViewSpacingBottom());
			}
			return dialog;
		}

		public CharSequence getMessage() {
			return message;
		}

		public ButtonEntry getNegativeButton() {
			return buttonNegative;
		}

		public ButtonEntry getNeutralButton() {
			return buttonNeutral;
		}

		public OnCancelListener getOnCancelListener() {
			return onCancelListener;
		}

		public ButtonEntry getPositiveButton() {
			return buttonPositive;
		}

		public CharSequence getTitle() {
			return title;
		}

		public View getView() {
			return view;
		}

		public int getViewSpacingBottom() {
			return viewSpacingBottom;
		}

		public int getViewSpacingLeft() {
			return viewSpacingLeft;
		}

		public int getViewSpacingRight() {
			return viewSpacingRight;
		}

		public int getViewSpacingTop() {
			return viewSpacingTop;
		}

		public boolean isCancelable() {
			return cancelable;
		}

		public boolean isUseNative() {
			return useNative;
		}

		public Builder removeNegativeButton() {
			buttonNegative = null;
			return this;
		}

		public Builder removeNeutralButton() {
			buttonNeutral = null;
			return this;
		}

		public Builder removePositiveButton() {
			buttonPositive = null;
			return this;
		}

		@Override
		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		@Override
		public Builder setMessage(CharSequence message) {
			this.message = message;
			return this;
		}

		@Override
		public Builder setMessage(int messageId) {
			return setMessage(context().getText(messageId));
		}

		@Override
		public Builder setNegativeButton(CharSequence text,
				OnClickListener listener) {
			buttonNegative = new ButtonEntry(text, listener);
			return this;
		}

		@Override
		public Builder setNeutralButton(CharSequence text,
				OnClickListener listener) {
			buttonNeutral = new ButtonEntry(text, listener);
			return this;
		}

		@Override
		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			this.onCancelListener = onCancelListener;
			return this;
		}

		@Override
		public Builder setPositiveButton(CharSequence text,
				OnClickListener listener) {
			buttonPositive = new ButtonEntry(text, listener);
			return this;
		}

		@Override
		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}

		@Override
		public Builder setIcon(Drawable icon) {
			this.icon = icon;
			return this;
		}

		@Override
		public Builder setIcon(int iconId) {
			return setIcon(context().getResources().getDrawable(iconId));
		}

		@Override
		public Builder setTitle(int titleId) {
			return setTitle(context().getText(titleId));
		}

		public Builder setUseNative(boolean useNative) {
			this.useNative = useNative;
			return this;
		}

		public Builder setView(int resId) {
			return setView(resId, 0, 0, 0, 0);
		}

		public Builder setView(int resId, int viewSpacingLeft,
				int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
			return setView(FontLoader.inflate(context(), resId),
					viewSpacingLeft, viewSpacingTop, viewSpacingRight,
					viewSpacingBottom);
		}

		@Override
		public Builder setView(View view) {
			setView(view, 0, 0, 0, 0);
			return this;
		}

		public Builder setView(View view, int viewSpacingLeft,
				int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
			this.view = view;
			setViewSpacingLeft(viewSpacingLeft);
			setViewSpacingTop(viewSpacingTop);
			setViewSpacingRight(viewSpacingRight);
			setViewSpacingBottom(viewSpacingBottom);
			return this;
		}

		public void setViewSpacingBottom(int viewSpacingBottom) {
			this.viewSpacingBottom = viewSpacingBottom;
		}

		public void setViewSpacingLeft(int viewSpacingLeft) {
			this.viewSpacingLeft = viewSpacingLeft;
		}

		public void setViewSpacingRight(int viewSpacingRight) {
			this.viewSpacingRight = viewSpacingRight;
		}

		public void setViewSpacingTop(int viewSpacingTop) {
			this.viewSpacingTop = viewSpacingTop;
		}
	}

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
		private AlertDialog dialog;
		private OnClickListener listener;
		private int which;

		public InternalListener(final AlertDialog dialog,
				OnClickListener listener, int which) {
			this.dialog = dialog;
			this.listener = listener;
			this.which = which;
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onClick(dialog, which);
			}
			try {
				dialog.dismiss();
				dialog.cancel();
			} catch (Exception e) {
			}
		}
	}

	private static boolean useNative() {
		return VERSION.SDK_INT >= 14;
	}

	private ButtonEntry buttonNegative, buttonNeutral, buttonPositive;
	private DialogButtonBar buttonsLayout;
	private ImageView icon;
	private TextView title;
	private boolean useNative;
	private View view;

	private static int getDialogTheme(Context context) {
		TypedValue value = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.dialogStyle, value, true);
		return value.resourceId;
	}

	public AlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		this(context, cancelable, cancelListener, useNative());
	}

	public AlertDialog(Context context) {
		this(context, useNative());
	}

	public AlertDialog(Context context, boolean useNative) {
		super(context, getDialogTheme(context));
		init(useNative);
	}

	public AlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, boolean useNative) {
		super(context, getDialogTheme(context));
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		init(useNative);
	}

	public View getView() {
		return view;
	}

	private void init(boolean useNative) {
		this.useNative = useNative;

		if (!useNative) {
			buttonsLayout = new DialogButtonBar(getContext());
			View customTitle = FontLoader.inflate(getContext(),
					R.layout.alert_dialog_title);
			title = (TextView) customTitle.findViewById(R.id.alertTitle);
			icon = (ImageView) customTitle.findViewById(R.id.icon);
			setCustomTitle(customTitle);
		}
	}

	private View customTitleView, lastCustomTitleView;

	@Override
	public void setCustomTitle(View customTitleView) {
		super.setCustomTitle(this.customTitleView = customTitleView);
	}

	public View getCustomTitle() {
		return customTitleView;
	}

	private void pushButton(ButtonEntry button, int which) {
		if (button == null || button.getKey() == null) {
			return;
		}
		Button b = new Button(getContext());
		b.setText(button.getKey());
		b.setOnClickListener(new InternalListener(this, button.getValue(),
				which));
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1;
		buttonsLayout.addView(b, layoutParams);
	}

	@Override
	public void setButton(int whichButton, CharSequence text,
			OnClickListener listener) {
		if (useNative) {
			super.setButton(whichButton, text, listener);
		} else {
			ButtonEntry entry = new ButtonEntry(text, listener);
			switch (whichButton) {
			case BUTTON_NEGATIVE:
				buttonNegative = entry;
				break;
			case BUTTON_POSITIVE:
				buttonPositive = entry;
				break;
			case BUTTON_NEUTRAL:
				buttonNeutral = entry;
				break;
			}
			updateButtonsLayout();
		}
	}

	@Override
	public void setIcon(Drawable icon) {
		if (useNative) {
			super.setIcon(icon);
		} else {
			this.icon.setImageDrawable(icon);
		}
	}

	@Override
	public void setIcon(int resId) {
		setIcon(getContext().getResources().getDrawable(resId));
	}

	@Override
	public void setMessage(CharSequence message) {
		if (useNative) {
			super.setMessage(message);
		} else {
			View customMessage = FontLoader.inflate(getContext(),
					R.layout.alert_dialog_message);
			TextView messageView = (TextView) customMessage
					.findViewById(R.id.message);
			messageView.setText(message);
			setView(customMessage);
		}
	}

	public void setMessage(int res) {
		setMessage(getContext().getText(res));
	}

	@Override
	public void setTitle(CharSequence title) {
		if (useNative) {
			super.setTitle(title);
		} else {
			this.title.setText(title);
			if (title == null || title.length() == 0) {
				lastCustomTitleView = getCustomTitle();
				setCustomTitle(null);
			} else {
				setCustomTitle(lastCustomTitleView);
				lastCustomTitleView = null;
			}
		}
	}

	@Override
	public void setTitle(int res) {
		setTitle(getContext().getText(res));
	}

	public void setView(int resId) {
		setView(resId, 0, 0, 0, 0);
	}

	public boolean isUseNative() {
		return useNative;
	}

	public void setView(int resId, int viewSpacingLeft, int viewSpacingTop,
			int viewSpacingRight, int viewSpacingBottom) {
		setView(FontLoader.inflate(getContext(), resId), viewSpacingLeft,
				viewSpacingTop, viewSpacingRight, viewSpacingBottom);
	}

	@Override
	public void setView(View view) {
		setView(view, 0, 0, 0, 0);
	}

	@Override
	public void setView(View view, int viewSpacingLeft, int viewSpacingTop,
			int viewSpacingRight, int viewSpacingBottom) {
		this.view = view;
		if (view == null) {
			return;
		}
		FontLoader.loadFont(view);
		if (useNative) {
			super.setView(view, viewSpacingLeft, viewSpacingTop,
					viewSpacingRight, viewSpacingBottom);
		} else {
			LinearLayout layout = new LinearLayout(getContext());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(view);
			try {
				((LinearLayout) buttonsLayout.getParent())
						.removeView(buttonsLayout);
			} catch (Exception e) {
			}
			LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, viewSpacingBottom, 0, 0);
			layout.addView(buttonsLayout, params);
			super.setView(layout, viewSpacingLeft, viewSpacingTop,
					viewSpacingRight, 0);
		}
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(getContext());
	}

	private void updateButtonsLayout() {
		buttonsLayout.removeAllViews();
		pushButton(buttonNegative, DialogInterface.BUTTON_NEGATIVE);
		pushButton(buttonNeutral, DialogInterface.BUTTON_NEUTRAL);
		pushButton(buttonPositive, DialogInterface.BUTTON_POSITIVE);
		buttonsLayout.rebuild();
		FontLoader.loadFont(buttonsLayout);
	}
}
