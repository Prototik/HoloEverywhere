package com.WazaBe.HoloDemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.widget.Button;
import com.WazaBe.HoloEverywhere.widget.LinearLayout;

public class OtherButton extends LinearLayout {
	private ImageView icon;
	private OnClickListener onClickListener;
	private Button text;

	public OtherButton(Context context) {
		super(context);
	}

	public OtherButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OtherButton(Context context, AttributeSet attrs, int defStyleRes) {
		super(context, attrs, defStyleRes);
	}

	@Override
	protected void init(AttributeSet attrs, int defStyleRes) {
		super.init(attrs, defStyleRes);
		LayoutInflater.inflate(getContext(), R.layout.other_button, this, true);
		text = (Button) findViewById(R.id.text);
		icon = (ImageView) findViewById(R.id.icon);
		if (onClickListener != null) {
			text.setOnClickListener(onClickListener);
		}
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.OtherButton);
		String textR = a.getString(R.styleable.OtherButton_android_text);
		Drawable iconR = a.getDrawable(R.styleable.OtherButton_android_icon);
		a.recycle();
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		if (textR != null) {
			text.setText(textR);
		}
		if (iconR != null) {
			icon.setImageDrawable(iconR);
		}
	}

	public void setIcon(Drawable drawable) {
		icon.setImageDrawable(drawable);
	}

	public void setIcon(int resId) {
		icon.setImageResource(resId);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		if (text == null) {
			onClickListener = l;
		} else {
			text.setOnClickListener(l);
		}
	}

	public void setText(CharSequence s) {
		text.setText(s);
	}

	public void setText(int resId) {
		text.setText(resId);
	}
}
