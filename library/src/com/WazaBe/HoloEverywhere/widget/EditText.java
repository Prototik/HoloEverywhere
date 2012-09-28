package com.WazaBe.HoloEverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class EditText extends TextView {
	public EditText(Context context) {
		this(context, null);
	}

	public EditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public EditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void extendSelection(int index) {
		Selection.extendSelection(getText(), index);
	}

	@Override
	protected boolean getDefaultEditable() {
		return true;
	}

	@Override
	protected MovementMethod getDefaultMovementMethod() {
		return ArrowKeyMovementMethod.getInstance();
	}

	@Override
	public Editable getText() {
		return (Editable) super.getText();
	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(EditText.class.getName());
	}

	@Override
	@SuppressLint("NewApi")
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(EditText.class.getName());
	}

	public void selectAll() {
		Selection.selectAll(getText());
	}

	@Override
	public void setEllipsize(TextUtils.TruncateAt ellipsis) {
		if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
			throw new IllegalArgumentException(
					"EditText cannot use the ellipsize mode "
							+ "TextUtils.TruncateAt.MARQUEE");
		}
		super.setEllipsize(ellipsis);
	}

	public void setSelection(int index) {
		Selection.setSelection(getText(), index);
	}

	public void setSelection(int start, int stop) {
		Selection.setSelection(getText(), start, stop);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, BufferType.EDITABLE);
	}
}