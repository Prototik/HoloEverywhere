package com.WazaBe.HoloEverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public class NineFrameLayout extends FrameLayout {
	private final AnimatorProxy mProxy;

	public NineFrameLayout(Context context) {
		super(context);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	public NineFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	public NineFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
	}

	@Override
	@SuppressLint("NewApi")
	public float getAlpha() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getAlpha();
		} else {
			return super.getAlpha();
		}
	}

	@Override
	@SuppressLint("NewApi")
	public float getTranslationX() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getTranslationX();
		} else {
			return super.getTranslationX();
		}
	}

	@Override
	@SuppressLint("NewApi")
	public float getTranslationY() {
		if (AnimatorProxy.NEEDS_PROXY) {
			return mProxy.getTranslationY();
		} else {
			return super.getTranslationY();
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void setAlpha(float alpha) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setAlpha(alpha);
		} else {
			super.setAlpha(alpha);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void setTranslationX(float translationX) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setTranslationX(translationX);
		} else {
			super.setTranslationX(translationX);
		}
	}

	@Override
	@SuppressLint("NewApi")
	public void setTranslationY(float translationY) {
		if (AnimatorProxy.NEEDS_PROXY) {
			mProxy.setTranslationY(translationY);
		} else {
			super.setTranslationY(translationY);
		}
	}

	@Override
	public void setVisibility(int visibility) {
		if (mProxy != null) {
			if (visibility == GONE) {
				clearAnimation();
			} else if (visibility == VISIBLE) {
				setAnimation(mProxy);
			}
		}
		super.setVisibility(visibility);
	}
}
