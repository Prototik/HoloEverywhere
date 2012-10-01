package com.WazaBe.HoloEverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEventSource;

import com.WazaBe.HoloEverywhere.sherlock.SBase;
import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;
import com.actionbarsherlock.view.ActionMode;

public class View extends android.view.View implements Drawable.Callback,
		KeyEvent.Callback, AccessibilityEventSource {

	public static final int[] PRESSED_STATE_SET, SUPPORT_EMPTY_STATE_SET,
			SUPPORT_WINDOW_FOCUSED_STATE_SET, SUPPORT_SELECTED_STATE_SET,
			SUPPORT_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_FOCUSED_STATE_SET,
			SUPPORT_FOCUSED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_FOCUSED_SELECTED_STATE_SET,
			SUPPORT_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_ENABLED_STATE_SET,
			SUPPORT_ENABLED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_ENABLED_SELECTED_STATE_SET,
			SUPPORT_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_ENABLED_FOCUSED_STATE_SET,
			SUPPORT_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_ENABLED_FOCUSED_SELECTED_STATE_SET,
			SUPPORT_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_STATE_SET, SUPPORT_PRESSED_SELECTED_STATE_SET,
			SUPPORT_PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_FOCUSED_SELECTED_STATE_SET,
			SUPPORT_PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_SELECTED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET,
			SUPPORT_PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

	static final int VIEW_STATE_ACCELERATED = 1 << 6;
	static final int VIEW_STATE_ACTIVATED = 1 << 5;
	static final int VIEW_STATE_DRAG_CAN_ACCEPT = 1 << 8;
	static final int VIEW_STATE_DRAG_HOVERED = 1 << 9;
	static final int VIEW_STATE_ENABLED = 1 << 3;
	static final int VIEW_STATE_FOCUSED = 1 << 2;
	static final int VIEW_STATE_HOVERED = 1 << 7;
	static final int VIEW_STATE_PRESSED = 1 << 4;
	static final int VIEW_STATE_SELECTED = 1 << 1;
	private static final int[][] VIEW_STATE_SETS;

	static final int VIEW_STATE_WINDOW_FOCUSED = 1;

	private static final int[] ViewDrawableStates = {
			android.R.attr.state_pressed, android.R.attr.state_focused,
			android.R.attr.state_selected, android.R.attr.state_window_focused,
			android.R.attr.state_enabled, android.R.attr.state_activated,
			android.R.attr.state_accelerated, android.R.attr.state_hovered,
			android.R.attr.state_drag_can_accept,
			android.R.attr.state_drag_hovered };
	static final int[] Z_VIEW_STATE_IDS = new int[] {
			android.R.attr.state_window_focused, VIEW_STATE_WINDOW_FOCUSED,
			android.R.attr.state_selected, VIEW_STATE_SELECTED,
			android.R.attr.state_focused, VIEW_STATE_FOCUSED,
			android.R.attr.state_enabled, VIEW_STATE_ENABLED,
			android.R.attr.state_pressed, VIEW_STATE_PRESSED,
			android.R.attr.state_activated, VIEW_STATE_ACTIVATED,
			android.R.attr.state_accelerated, VIEW_STATE_ACCELERATED,
			android.R.attr.state_hovered, VIEW_STATE_HOVERED,
			android.R.attr.state_drag_can_accept, VIEW_STATE_DRAG_CAN_ACCEPT,
			android.R.attr.state_drag_hovered, VIEW_STATE_DRAG_HOVERED };

	static {
		if (Z_VIEW_STATE_IDS.length / 2 != ViewDrawableStates.length) {
			throw new IllegalStateException(
					"VIEW_STATE_IDs array length does not match ViewDrawableStates style array");
		}
		int[] orderedIds = new int[Z_VIEW_STATE_IDS.length];
		for (int i = 0; i < ViewDrawableStates.length; i++) {
			int viewState = ViewDrawableStates[i];
			for (int j = 0; j < Z_VIEW_STATE_IDS.length; j += 2) {
				if (Z_VIEW_STATE_IDS[j] == viewState) {
					orderedIds[i * 2] = viewState;
					orderedIds[i * 2 + 1] = Z_VIEW_STATE_IDS[j + 1];
				}
			}
		}
		final int NUM_BITS = Z_VIEW_STATE_IDS.length / 2;
		VIEW_STATE_SETS = new int[1 << NUM_BITS][];
		for (int i = 0; i < VIEW_STATE_SETS.length; i++) {
			int numBits = Integer.bitCount(i);
			int[] set = new int[numBits];
			int pos = 0;
			for (int j = 0; j < orderedIds.length; j += 2) {
				if ((i & orderedIds[j + 1]) != 0) {
					set[pos++] = orderedIds[j];
				}
			}
			VIEW_STATE_SETS[i] = set;
		}

		SUPPORT_EMPTY_STATE_SET = VIEW_STATE_SETS[0];
		SUPPORT_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED];
		SUPPORT_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED];
		SUPPORT_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED];
		SUPPORT_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_FOCUSED];
		SUPPORT_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_FOCUSED];
		SUPPORT_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_FOCUSED];
		SUPPORT_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_FOCUSED];
		SUPPORT_ENABLED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_FOCUSED
				| VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_FOCUSED | VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_FOCUSED | VIEW_STATE_ENABLED];
		SUPPORT_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_FOCUSED | VIEW_STATE_ENABLED];

		SUPPORT_PRESSED_STATE_SET = PRESSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_FOCUSED
				| VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_FOCUSED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_FOCUSED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_FOCUSED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_ENABLED
				| VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED | VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_FOCUSED
				| VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_FOCUSED | VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED
				| VIEW_STATE_FOCUSED | VIEW_STATE_ENABLED | VIEW_STATE_PRESSED];
		SUPPORT_PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED
				| VIEW_STATE_SELECTED
				| VIEW_STATE_FOCUSED
				| VIEW_STATE_ENABLED
				| VIEW_STATE_PRESSED];
	}

	public static int supportResolveSize(int size, int measureSpec) {
		return View.supportResolveSizeAndState(size, measureSpec, 0)
				& MEASURED_SIZE_MASK;
	}

	public static int supportResolveSizeAndState(int size, int measureSpec,
			int childMeasuredState) {
		int result = size;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			result = size;
			break;
		case MeasureSpec.AT_MOST:
			if (specSize < size) {
				result = specSize | MEASURED_STATE_TOO_SMALL;
			} else {
				result = size;
			}
			break;
		case MeasureSpec.EXACTLY:
			result = specSize;
			break;
		}
		return result | childMeasuredState & MEASURED_STATE_MASK;
	}

	private final AnimatorProxy proxy;

	public View(Context context) {
		super(context);
		proxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	public View(Context context, AttributeSet attrs) {
		super(context, attrs);
		proxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	public View(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		proxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	@SuppressLint("NewApi")
	@Override
	public float getAlpha() {
		if (proxy != null) {
			return proxy.getAlpha();
		}
		return super.getAlpha();
	}

	public int getMeasuredStateInt() {
		return getMeasuredWidth() & MEASURED_STATE_MASK
				| getMeasuredHeight() >> MEASURED_HEIGHT_STATE_SHIFT
				& MEASURED_STATE_MASK >> MEASURED_HEIGHT_STATE_SHIFT;
	}

	@SuppressLint("NewApi")
	@Override
	public float getTranslationX() {
		if (proxy != null) {
			return proxy.getTranslationX();
		}
		return super.getTranslationX();
	}

	@SuppressLint("NewApi")
	@Override
	public float getTranslationY() {
		if (proxy != null) {
			return proxy.getTranslationY();
		}
		return super.getTranslationY();
	}

	@SuppressLint("NewApi")
	public void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
	}

	@SuppressLint("NewApi")
	@Override
	public void setAlpha(float alpha) {
		if (proxy != null) {
			proxy.setAlpha(alpha);
		}
		super.setAlpha(alpha);
	}

	@SuppressLint("NewApi")
	@Override
	public void setTranslationX(float translationX) {
		if (proxy != null) {
			proxy.setTranslationX(translationX);
		}
		super.setTranslationX(translationX);
	}

	@SuppressLint("NewApi")
	@Override
	public void setTranslationY(float translationY) {
		if (proxy != null) {
			proxy.setTranslationY(translationY);
		}
		super.setTranslationY(translationY);
	}

	@Override
	public void setVisibility(int visibility) {
		if (proxy != null) {
			if (visibility == GONE) {
				clearAnimation();
			} else if (visibility == VISIBLE) {
				setAnimation(proxy);
			}
		}
		super.setVisibility(visibility);
	}

	public ActionMode startActionMode(ActionMode.Callback actionModeCallback) {
		return ((SBase) getContext()).startActionMode(actionModeCallback);
	}
}
