package com.WazaBe.HoloEverywhere;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DialogButtonBar extends LinearLayout implements
		OnLongClickListener {
	public static class LayoutParams extends LinearLayout.LayoutParams {
		public CharSequence hint = "";

		public LayoutParams(int width, int height) {
			super(width, height, 1);
		}

		public LayoutParams() {
			this(MATCH_PARENT, WRAP_CONTENT);
		}

		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.DialogButtonBar_LayoutParams);
			if (a.hasValue(R.styleable.DialogButtonBar_LayoutParams_android_hint)) {
				hint = a.getString(R.styleable.DialogButtonBar_LayoutParams_android_hint);
			}
			a.recycle();
		}

		public LayoutParams(ViewGroup.LayoutParams p) {
			super(p);
			try {
				LayoutParams params = ((LayoutParams) p);
				hint = params.hint;
			} catch (ClassCastException e) {

			}
		}
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams();
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		try {
			return (LayoutParams) p;
		} catch (ClassCastException e) {
			return new LayoutParams(p);
		}
	}

	private int dialogButton = 0, dialogButtonBorder = 0;
	private boolean rebuild = true;

	public DialogButtonBar(Context context) {
		super(context);
		init();
	}

	public DialogButtonBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DialogButtonBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		rebuild = true;
		super.addView(child, index, params);
	}

	@Override
	protected boolean addViewInLayout(View child, int index,
			android.view.ViewGroup.LayoutParams params,
			boolean preventRequestLayout) {
		rebuild = true;
		return super
				.addViewInLayout(child, index, params, preventRequestLayout);
	}

	private void init() {
		setOrientation(0);
		Theme theme = getContext().getTheme();
		TypedValue value = new TypedValue();
		theme.resolveAttribute(R.attr.dialogButton, value, true);
		dialogButton = value.resourceId;
		theme.resolveAttribute(R.attr.dialogButtonBorder, value, true);
		dialogButtonBorder = value.resourceId;
		value = null;
		if (dialogButton <= 0 || dialogButtonBorder <= 0) {
			throw new RuntimeException(
					"You must define dialogButton and dialogButtonBorder in theme for using DialogButtonBar");
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (rebuild) {
			rebuild();
		}
		super.onLayout(changed, l, t, r, b);
	}

	public void rebuild() {
		try {
			final int count = getChildCount(), pos = count - 1;
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				view.setBackgroundResource(i == pos ? dialogButton
						: dialogButtonBorder);
				view.setOnLongClickListener(this);
			}
			rebuild = false;
		} catch (NullPointerException e) {
			rebuild();
		}
	}

	@Override
	public void removeView(View view) {
		rebuild = true;
		super.removeView(view);
	}

	@Override
	public void removeViewAt(int index) {
		rebuild = true;
		super.removeViewAt(index);
	}

	@Override
	public void removeViewInLayout(View view) {
		rebuild = true;
		super.removeViewInLayout(view);
	}

	@Override
	public void setOrientation(int orientation) {
		super.setOrientation(HORIZONTAL);
	}

	@Override
	public void removeViews(int start, int count) {
		rebuild = true;
		super.removeViews(start, count);
	}

	@Override
	public void removeViewsInLayout(int start, int count) {
		rebuild = true;
		super.removeViewsInLayout(start, count);
	}

	@TargetApi(3)
	@Override
	public boolean onLongClick(View v) {
		LayoutParams p = generateLayoutParams(v.getLayoutParams());
		String hint = p.hint.toString().trim();
		if (hint.length() > 0) {
			final int[] screenPos = new int[2];
			v.getLocationOnScreen(screenPos);
			final Rect displayFrame = new Rect();
			v.getWindowVisibleDisplayFrame(displayFrame);
			final Context context = getContext();
			final int height = v.getHeight();
			final int midy = screenPos[1] + height / 2;
			final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			HoloToast toast = HoloToast.makeText(context, hint,
					HoloToast.LENGTH_SHORT);
			if (midy < displayFrame.height()) {
				toast.setGravity(Gravity.TOP | Gravity.RIGHT, screenWidth
						- screenPos[0] - v.getWidth() / 2, screenPos[1]
						- height);
			} else {
				toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
						height);
			}
			toast.show();
			return true;
		} else {
			return false;
		}
	}
}
