package com.WazaBe.HoloEverywhere.app;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.internal.AlertController;

public class AlertDialog extends Dialog implements DialogInterface {
	public static class Builder {
		private final AlertController.AlertParams P;

		public Builder(Context context) {
			this(context, resolveDialogTheme(context, 0));
		}

		public Builder(Context context, int theme) {
			P = new AlertController.AlertParams(new ContextThemeWrapper(
					context, resolveDialogTheme(context, theme)));
			P.mTheme = theme;
		}

		public AlertDialog create() {
			final AlertDialog dialog = new AlertDialog(P.mContext, P.mTheme);
			P.apply(dialog.mAlert);
			dialog.setCancelable(P.mCancelable);
			if (P.mCancelable) {
				dialog.setCanceledOnTouchOutside(true);
			}
			dialog.setOnCancelListener(P.mOnCancelListener);
			if (P.mOnKeyListener != null) {
				dialog.setOnKeyListener(P.mOnKeyListener);
			}
			return dialog;
		}

		public Context getContext() {
			return P.mContext;
		}

		public Builder setAdapter(final ListAdapter adapter,
				final OnClickListener listener) {
			P.mAdapter = adapter;
			P.mOnClickListener = listener;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			P.mCancelable = cancelable;
			return this;
		}

		public Builder setCursor(final Cursor cursor,
				final OnClickListener listener, String labelColumn) {
			P.mCursor = cursor;
			P.mLabelColumn = labelColumn;
			P.mOnClickListener = listener;
			return this;
		}

		public Builder setCustomTitle(View customTitleView) {
			P.mCustomTitleView = customTitleView;
			return this;
		}

		public Builder setIcon(Drawable icon) {
			P.mIcon = icon;
			return this;
		}

		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		public Builder setIconAttribute(int attrId) {
			TypedValue out = new TypedValue();
			P.mContext.getTheme().resolveAttribute(attrId, out, true);
			P.mIconId = out.resourceId;
			return this;
		}

		public Builder setInverseBackgroundForced(boolean useInverseBackground) {
			P.mForceInverseBackground = useInverseBackground;
			return this;
		}

		public Builder setItems(CharSequence[] items,
				final OnClickListener listener) {
			P.mItems = items;
			P.mOnClickListener = listener;
			return this;
		}

		public Builder setItems(int itemsId, final OnClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnClickListener = listener;
			return this;
		}

		public Builder setMessage(CharSequence message) {
			P.mMessage = message;
			return this;
		}

		public Builder setMessage(int messageId) {
			P.mMessage = P.mContext.getText(messageId);
			return this;
		}

		public Builder setMultiChoiceItems(CharSequence[] items,
				boolean[] checkedItems,
				final OnMultiChoiceClickListener listener) {
			P.mItems = items;
			P.mOnCheckboxClickListener = listener;
			P.mCheckedItems = checkedItems;
			P.mIsMultiChoice = true;
			return this;
		}

		public Builder setMultiChoiceItems(Cursor cursor,
				String isCheckedColumn, String labelColumn,
				final OnMultiChoiceClickListener listener) {
			P.mCursor = cursor;
			P.mOnCheckboxClickListener = listener;
			P.mIsCheckedColumn = isCheckedColumn;
			P.mLabelColumn = labelColumn;
			P.mIsMultiChoice = true;
			return this;
		}

		public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
				final OnMultiChoiceClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnCheckboxClickListener = listener;
			P.mCheckedItems = checkedItems;
			P.mIsMultiChoice = true;
			return this;
		}

		public Builder setNegativeButton(CharSequence text,
				final OnClickListener listener) {
			P.mNegativeButtonText = text;
			P.mNegativeButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(int textId,
				final OnClickListener listener) {
			P.mNegativeButtonText = P.mContext.getText(textId);
			P.mNegativeButtonListener = listener;
			return this;
		}

		public Builder setNeutralButton(CharSequence text,
				final OnClickListener listener) {
			P.mNeutralButtonText = text;
			P.mNeutralButtonListener = listener;
			return this;
		}

		public Builder setNeutralButton(int textId,
				final OnClickListener listener) {
			P.mNeutralButtonText = P.mContext.getText(textId);
			P.mNeutralButtonListener = listener;
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			P.mOnCancelListener = onCancelListener;
			return this;
		}

		public Builder setOnItemSelectedListener(
				final AdapterView.OnItemSelectedListener listener) {
			P.mOnItemSelectedListener = listener;
			return this;
		}

		public Builder setOnKeyListener(OnKeyListener onKeyListener) {
			P.mOnKeyListener = onKeyListener;
			return this;
		}

		public Builder setPositiveButton(CharSequence text,
				final OnClickListener listener) {
			P.mPositiveButtonText = text;
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setPositiveButton(int textId,
				final OnClickListener listener) {
			P.mPositiveButtonText = P.mContext.getText(textId);
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setRecycleOnMeasureEnabled(boolean enabled) {
			P.mRecycleOnMeasure = enabled;
			return this;
		}

		public Builder setSingleChoiceItems(CharSequence[] items,
				int checkedItem, final OnClickListener listener) {
			P.mItems = items;
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mIsSingleChoice = true;
			return this;
		}

		public Builder setSingleChoiceItems(Cursor cursor, int checkedItem,
				String labelColumn, final OnClickListener listener) {
			P.mCursor = cursor;
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mLabelColumn = labelColumn;
			P.mIsSingleChoice = true;
			return this;
		}

		public Builder setSingleChoiceItems(int itemsId, int checkedItem,
				final OnClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mIsSingleChoice = true;
			return this;
		}

		public Builder setSingleChoiceItems(ListAdapter adapter,
				int checkedItem, final OnClickListener listener) {
			P.mAdapter = adapter;
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mIsSingleChoice = true;
			return this;
		}

		public Builder setTitle(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setTitle(int titleId) {
			P.mTitle = P.mContext.getText(titleId);
			return this;
		}

		public Builder setView(View view) {
			P.mView = view;
			P.mViewSpacingSpecified = false;
			return this;
		}

		public Builder setView(View view, int viewSpacingLeft,
				int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
			P.mView = view;
			P.mViewSpacingSpecified = true;
			P.mViewSpacingLeft = viewSpacingLeft;
			P.mViewSpacingTop = viewSpacingTop;
			P.mViewSpacingRight = viewSpacingRight;
			P.mViewSpacingBottom = viewSpacingBottom;
			return this;
		}

		public AlertDialog show() {
			AlertDialog dialog = create();
			dialog.show();
			return dialog;
		}
	}

	public static final int THEME_HOLO_DARK = 1;
	public static final int THEME_HOLO_LIGHT = 2;

	static int resolveDialogTheme(Context context, int resid) {
		if (resid == THEME_HOLO_DARK) {
			return R.style.Holo_Theme_Dialog_Alert;
		} else if (resid == THEME_HOLO_LIGHT) {
			return R.style.Holo_Theme_Dialog_Alert_Light;
		} else if (resid >= 0x01000000) {
			return resid;
		} else {
			TypedValue outValue = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.alertDialogTheme,
					outValue, true);
			return outValue.resourceId;
		}
	}

	private final AlertController mAlert;

	protected AlertDialog(Context context) {
		this(context, false, null, resolveDialogTheme(context, 0));
	}

	protected AlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		this(context, cancelable, cancelListener,
				resolveDialogTheme(context, 0));
	}

	protected AlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, int theme) {
		super(context, resolveDialogTheme(context, theme));
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		mAlert = new AlertController(context, this, getWindow());
	}

	protected AlertDialog(Context context, int theme) {
		this(context, false, null, theme);
	}

	public Button getButton(int whichButton) {
		return mAlert.getButton(whichButton);
	}

	public ListView getListView() {
		return mAlert.getListView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAlert.installContent();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mAlert.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mAlert.onKeyUp(keyCode, event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Deprecated
	public void setButton(CharSequence text, Message msg) {
		setButton(BUTTON_POSITIVE, text, msg);
	}

	@Deprecated
	public void setButton(CharSequence text, final OnClickListener listener) {
		setButton(BUTTON_POSITIVE, text, listener);
	}

	public void setButton(int whichButton, CharSequence text, Message msg) {
		mAlert.setButton(whichButton, text, null, msg);
	}

	public void setButton(int whichButton, CharSequence text,
			OnClickListener listener) {
		mAlert.setButton(whichButton, text, listener, null);
	}

	@Deprecated
	public void setButton2(CharSequence text, Message msg) {
		setButton(BUTTON_NEGATIVE, text, msg);
	}

	@Deprecated
	public void setButton2(CharSequence text, final OnClickListener listener) {
		setButton(BUTTON_NEGATIVE, text, listener);
	}

	@Deprecated
	public void setButton3(CharSequence text, Message msg) {
		setButton(BUTTON_NEUTRAL, text, msg);
	}

	@Deprecated
	public void setButton3(CharSequence text, final OnClickListener listener) {
		setButton(BUTTON_NEUTRAL, text, listener);
	}

	public void setCustomTitle(View customTitleView) {
		mAlert.setCustomTitle(customTitleView);
	}

	public void setIcon(Drawable icon) {
		mAlert.setIcon(icon);
	}

	public void setIcon(int resId) {
		mAlert.setIcon(resId);
	}

	public void setIconAttribute(int attrId) {
		TypedValue out = new TypedValue();
		getContext().getTheme().resolveAttribute(attrId, out, true);
		mAlert.setIcon(out.resourceId);
	}

	public void setInverseBackgroundForced(boolean forceInverseBackground) {
		mAlert.setInverseBackgroundForced(forceInverseBackground);
	}

	public void setMessage(CharSequence message) {
		mAlert.setMessage(message);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mAlert.setTitle(title);
	}

	public void setView(View view) {
		mAlert.setView(view);
	}

	public void setView(View view, int viewSpacingLeft, int viewSpacingTop,
			int viewSpacingRight, int viewSpacingBottom) {
		mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight,
				viewSpacingBottom);
	}
}