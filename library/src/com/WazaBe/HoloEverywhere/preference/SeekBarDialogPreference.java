package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.SeekBar;

public class SeekBarDialogPreference extends DialogPreference {
	protected static SeekBar getSeekBar(View dialogView) {
		return (SeekBar) dialogView.findViewById(R.id.seekbar);
	}

	private int mMax;
	private Drawable mMyIcon;

	private SeekBar mSeekBar;

	public SeekBarDialogPreference(Context context) {
		this(context, null);
	}

	public SeekBarDialogPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.seekBarDialogPreferenceStyle);
	}

	public SeekBarDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		createActionButtons();
		mMyIcon = getDialogIcon();
		setDialogIcon(null);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarPreference, defStyle, 0);
		setMax(a.getInt(R.styleable.SeekBarPreference_max, 0));
	}

	public void createActionButtons() {
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
	}

	public int getMax() {
		return mMax;
	}

	public SeekBar getSeekBar() {
		return mSeekBar;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
		if (mSeekBar != null) {
			mSeekBar.setMax(mMax);
		}
		final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		if (mMyIcon != null) {
			iconView.setImageDrawable(mMyIcon);
		} else {
			iconView.setVisibility(View.GONE);
		}
	}

	public void setMax(int max) {
		mMax = max;
	}
}