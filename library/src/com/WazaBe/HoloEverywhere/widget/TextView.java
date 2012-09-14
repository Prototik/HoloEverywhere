package com.WazaBe.HoloEverywhere.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.WazaBe.HoloEverywhere.R;

public class TextView extends android.widget.TextView {

	private boolean allCaps = false;

	public TextView(Context context) {
		super(context);
		init(null, 0);
	}

	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	protected void init(AttributeSet attrs, int defStyle) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.TextView, defStyle, 0);
		allCaps = a.getBoolean(R.styleable.TextView_textAllCaps, false);
		a.recycle();
	}

	public boolean isAllCaps() {
		return allCaps;
	}

	@Override
	public void setAllCaps(boolean allCaps) {
		this.allCaps = allCaps;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(allCaps ? text.toString().toUpperCase() : text, type);
	}
}
