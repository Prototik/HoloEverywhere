package com.WazaBe.HoloDemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.WazaBe.HoloDemo.R;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.widget.LinearLayout;
import com.WazaBe.HoloEverywhere.widget.TextView;

public class OtherButton extends LinearLayout {

	public OtherButton(Context context) {
		super(context);
	}

	public OtherButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OtherButton(Context context, AttributeSet attrs, int defStyleRes) {
		super(context, attrs, defStyleRes);
	}

	private TextView text;
	private ImageView icon;

	@Override
	protected void init(Context context, AttributeSet attrs, int defStyleRes) {
		super.init(context, attrs, defStyleRes);
		LayoutInflater.inflate(context, R.layout.other_button, this, true);
		text = (TextView) findViewById(R.id.text);
		icon = (ImageView) findViewById(R.id.icon);
		if (onClickListener != null) {
			text.setOnClickListener(onClickListener);
		}
		TypedArray a = context.obtainStyledAttributes(attrs,
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

	private OnClickListener onClickListener;

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

	public void setIcon(Drawable drawable) {
		icon.setImageDrawable(drawable);
	}

	public void setIcon(int resId) {
		icon.setImageResource(resId);
	}
}
