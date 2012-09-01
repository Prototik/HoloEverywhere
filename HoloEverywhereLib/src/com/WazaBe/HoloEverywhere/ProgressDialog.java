/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.WazaBe.HoloEverywhere;

import java.text.NumberFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * <p>
 * A dialog showing a progress indicator and an optional text message or view.
 * Only a text message or a view can be used at the same time.
 * </p>
 * <p>
 * The dialog can be made cancelable on back key press.
 * </p>
 * <p>
 * The progress range is 0..10000.
 * </p>
 */
public class ProgressDialog extends AlertDialog {

	/**
	 * Creates a ProgressDialog with a horizontal progress bar.
	 */
	public static final int STYLE_HORIZONTAL = 1;

	/**
	 * Creates a ProgressDialog with a circular, spinning progress bar. This is
	 * the default.
	 */
	public static final int STYLE_SPINNER = 0;

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message) {
		return show(context, title, message, false);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static ProgressDialog show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

	private boolean mHasStarted;
	private int mIncrementBy;
	private int mIncrementSecondaryBy;

	private boolean mIndeterminate;
	private Drawable mIndeterminateDrawable;
	private int mMax;
	private CharSequence mMessage;
	private TextView mMessageView;
	private ProgressBar mProgress;
	private Drawable mProgressDrawable;
	private TextView mProgressNumber;
	private String mProgressNumberFormat;

	private TextView mProgressPercent;
	private NumberFormat mProgressPercentFormat;

	private int mProgressStyle = STYLE_SPINNER;

	private int mProgressVal;

	private int mSecondaryProgressVal;

	private Handler mViewUpdateHandler;

	public ProgressDialog(Context context) {
		super(context);
		initFormats();
	}

	public ProgressDialog(Context context, boolean useNative) {
		super(context, useNative);
		initFormats();
	}

	public ProgressDialog(Context context, int theme) {
		super(context, theme);
		initFormats();
	}

	public ProgressDialog(Context context, int theme, boolean useNative) {
		super(context, theme, useNative);
		initFormats();
	}

	public int getMax() {
		if (mProgress != null) {
			return mProgress.getMax();
		}
		return mMax;
	}

	public int getProgress() {
		if (mProgress != null) {
			return mProgress.getProgress();
		}
		return mProgressVal;
	}

	public int getSecondaryProgress() {
		if (mProgress != null) {
			return mProgress.getSecondaryProgress();
		}
		return mSecondaryProgressVal;
	}

	public void incrementProgressBy(int diff) {
		if (mProgress != null) {
			mProgress.incrementProgressBy(diff);
			onProgressChanged();
		} else {
			mIncrementBy += diff;
		}
	}

	public void incrementSecondaryProgressBy(int diff) {
		if (mProgress != null) {
			mProgress.incrementSecondaryProgressBy(diff);
			onProgressChanged();
		} else {
			mIncrementSecondaryBy += diff;
		}
	}

	private void initFormats() {
		mProgressNumberFormat = "%1d/%2d";
		mProgressPercentFormat = NumberFormat.getPercentInstance();
		mProgressPercentFormat.setMaximumFractionDigits(0);
	}

	public boolean isIndeterminate() {
		if (mProgress != null) {
			return mProgress.isIndeterminate();
		}
		return mIndeterminate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TypedArray a = getContext().obtainStyledAttributes(null,
				R.styleable.AlertDialog, android.R.attr.alertDialogStyle, 0);
		if (mProgressStyle == STYLE_HORIZONTAL) {

			/*
			 * Use a separate handler to update the text views as they must be
			 * updated on the same thread that created them.
			 */
			mViewUpdateHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					/* Update the number and percent */
					int progress = mProgress.getProgress();
					int max = mProgress.getMax();
					if (mProgressNumberFormat != null) {
						String format = mProgressNumberFormat;
						mProgressNumber.setText(String.format(format, progress,
								max));
					} else {
						mProgressNumber.setText("");
					}
					if (mProgressPercentFormat != null) {
						double percent = (double) progress / (double) max;
						SpannableString tmp = new SpannableString(
								mProgressPercentFormat.format(percent));
						tmp.setSpan(new StyleSpan(
								android.graphics.Typeface.BOLD), 0, tmp
								.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						mProgressPercent.setText(tmp);
					} else {
						mProgressPercent.setText("");
					}
				}
			};
			setView(a.getResourceId(
					R.styleable.AlertDialog_android_horizontalProgressLayout,
					R.layout.alert_dialog_progress));
			mProgress = (ProgressBar) getView().findViewById(R.id.progress);
			mProgressNumber = (TextView) getView().findViewById(
					R.id.progress_number);
			mProgressPercent = (TextView) getView().findViewById(
					R.id.progress_percent);
		} else {
			setView(a.getResourceId(
					R.styleable.AlertDialog_android_progressLayout,
					R.layout.progress_dialog_holo));
			mProgress = (ProgressBar) getView().findViewById(R.id.progress);
			mMessageView = (TextView) getView().findViewById(R.id.message);
		}
		a.recycle();
		if (mMax > 0) {
			setMax(mMax);
		}
		if (mProgressVal > 0) {
			setProgress(mProgressVal);
		}
		if (mSecondaryProgressVal > 0) {
			setSecondaryProgress(mSecondaryProgressVal);
		}
		if (mIncrementBy > 0) {
			incrementProgressBy(mIncrementBy);
		}
		if (mIncrementSecondaryBy > 0) {
			incrementSecondaryProgressBy(mIncrementSecondaryBy);
		}
		if (mProgressDrawable != null) {
			setProgressDrawable(mProgressDrawable);
		}
		if (mIndeterminateDrawable != null) {
			setIndeterminateDrawable(mIndeterminateDrawable);
		}
		if (mMessage != null) {
			setMessage(mMessage);
		}
		setIndeterminate(mIndeterminate);
		onProgressChanged();
		super.onCreate(savedInstanceState);
	}

	private void onProgressChanged() {
		if (mProgressStyle == STYLE_HORIZONTAL) {
			if (mViewUpdateHandler != null
					&& !mViewUpdateHandler.hasMessages(0)) {
				mViewUpdateHandler.sendEmptyMessage(0);
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mHasStarted = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHasStarted = false;
	}

	public void setIndeterminate(boolean indeterminate) {
		if (mProgress != null) {
			mProgress.setIndeterminate(indeterminate);
		} else {
			mIndeterminate = indeterminate;
		}
	}

	public void setIndeterminateDrawable(Drawable d) {
		if (mProgress != null) {
			mProgress.setIndeterminateDrawable(d);
		} else {
			mIndeterminateDrawable = d;
		}
	}

	public void setMax(int max) {
		if (mProgress != null) {
			mProgress.setMax(max);
			onProgressChanged();
		} else {
			mMax = max;
		}
	}

	@Override
	public void setMessage(CharSequence message) {
		if (mProgress != null) {
			if (mProgressStyle == STYLE_HORIZONTAL) {
				super.setMessage(message);
			} else {
				mMessageView.setText(message);
			}
		} else {
			mMessage = message;
		}
	}

	public void setProgress(int value) {
		if (mHasStarted) {
			mProgress.setProgress(value);
			onProgressChanged();
		} else {
			mProgressVal = value;
		}
	}

	public void setProgressDrawable(Drawable d) {
		if (mProgress != null) {
			mProgress.setProgressDrawable(d);
		} else {
			mProgressDrawable = d;
		}
	}

	/**
	 * Change the format of the small text showing current and maximum units of
	 * progress. The default is "%1d/%2d". Should not be called during the
	 * number is progressing.
	 * 
	 * @param format
	 *            A string passed to {@link String#format String.format()}; use
	 *            "%1d" for the current number and "%2d" for the maximum. If
	 *            null, nothing will be shown.
	 */
	public void setProgressNumberFormat(String format) {
		mProgressNumberFormat = format;
		onProgressChanged();
	}

	/**
	 * Change the format of the small text showing the percentage of progress.
	 * The default is {@link NumberFormat#getPercentInstance()
	 * NumberFormat.getPercentageInstnace().} Should not be called during the
	 * number is progressing.
	 * 
	 * @param format
	 *            An instance of a {@link NumberFormat} to generate the
	 *            percentage text. If null, nothing will be shown.
	 */
	public void setProgressPercentFormat(NumberFormat format) {
		mProgressPercentFormat = format;
		onProgressChanged();
	}

	public void setProgressStyle(int style) {
		mProgressStyle = style;
	}

	public void setSecondaryProgress(int secondaryProgress) {
		if (mProgress != null) {
			mProgress.setSecondaryProgress(secondaryProgress);
			onProgressChanged();
		} else {
			mSecondaryProgressVal = secondaryProgress;
		}
	}
}