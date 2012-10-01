package com.WazaBe.HoloEverywhere.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityManager;

import com.WazaBe.HoloEverywhere.sherlock.SBase;
import com.actionbarsherlock.internal.nineoldandroids.view.NineViewGroup;
import com.actionbarsherlock.view.ActionMode;

public abstract class ViewGroup extends NineViewGroup {
	public static final int ACCESSIBILITY_FOCUS_BACKWARD = FOCUS_BACKWARD | 0x00000002;
	public static final int ACCESSIBILITY_FOCUS_FORWARD = FOCUS_FORWARD | 0x00000002;
	public static final int FLAG_DISALLOW_INTERCEPT = 0x80000;
	public static final int FOCUS_ACCESSIBILITY = 0x00001000;
	public static final int FOCUSABLES_ACCESSIBILITY = 0x00000002;

	public static boolean isAccessibilityManagerEnabled(Context context) {
		boolean enabled = false;
		try {
			enabled = ((AccessibilityManager) context
					.getSystemService(Context.ACCESSIBILITY_SERVICE))
					.isEnabled();
		} catch (Exception e) {
		}
		return enabled;
	}

	public ViewGroup(Context context) {
		super(context);
	}

	public ViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isAccessibilityManagerEnabled() {
		return isAccessibilityManagerEnabled(getContext());
	}

	public ActionMode startActionMode(ActionMode.Callback actionModeCallback) {
		return ((SBase) getContext()).startActionMode(actionModeCallback);
	}

}
