package com.WazaBe.HoloEverywhere.internal;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

public class WindowCompat {
	private static boolean isOutOfBounds(Window window, MotionEvent event) {
		final int x = (int) event.getX(), y = (int) event.getY();
		final int slop = ViewConfiguration.get(window.getContext())
				.getScaledWindowTouchSlop();
		final View decorView = window.getDecorView();
		return x < -slop || y < -slop || x > decorView.getWidth() + slop
				|| y > decorView.getHeight() + slop;
	}

	public static boolean shouldCloseOnTouch(Window window, MotionEvent event) {
		return (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN
				&& isOutOfBounds(window, event)
				&& window.peekDecorView() != null;
	}

	private WindowCompat() {
	}
}
