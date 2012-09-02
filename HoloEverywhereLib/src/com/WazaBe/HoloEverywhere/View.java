package com.WazaBe.HoloEverywhere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public class View extends android.view.View {
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

	public static int resolveSize(int size, int measureSpec) {
		return resolveSizeAndState(size, measureSpec, 0) & MEASURED_SIZE_MASK;
	}

	@SuppressLint("NewApi")
	@Override
	public float getAlpha() {
		if (proxy != null) {
			return proxy.getAlpha();
		}
		return super.getAlpha();
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

	public static int resolveSizeAndState(int size, int measureSpec,
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
		return result | (childMeasuredState & MEASURED_STATE_MASK);
	}

	@SuppressLint("NewApi")
	public void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
	}
}
