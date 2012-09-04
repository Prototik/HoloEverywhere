package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.WazaBe.HoloEverywhere.R;

public class SeekBarPreference extends Preference implements
		OnSeekBarChangeListener {
	private static class SavedState extends BaseSavedState {
		int max;
		int progress;

		public SavedState(Parcel source) {
			super(source);
			progress = source.readInt();
			max = source.readInt();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(progress);
			dest.writeInt(max);
		}
	}

	private int mMax;
	private int mProgress;

	private boolean mTrackingTouch;

	public SeekBarPreference(Context context) {
		this(context, null);
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.seekBarPreferenceStyle);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarPreference, defStyle, 0);
		setMax(a.getInt(R.styleable.SeekBarPreference_max, mMax));
		a.recycle();
	}

	public int getProgress() {
		return mProgress;
	}

	@Override
	public CharSequence getSummary() {
		return null;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setMax(mMax);
		seekBar.setProgress(mProgress);
		seekBar.setEnabled(isEnabled());
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_UP) {
			if (keyCode == KeyEvent.KEYCODE_PLUS
					|| keyCode == KeyEvent.KEYCODE_EQUALS) {
				setProgress(getProgress() + 1);
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_MINUS) {
				setProgress(getProgress() - 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser && !mTrackingTouch) {
			syncProgress(seekBar);
		}
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (!state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		mProgress = myState.progress;
		mMax = myState.max;
		notifyChanged();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}
		final SavedState myState = new SavedState(superState);
		myState.progress = mProgress;
		myState.max = mMax;
		return myState;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setProgress(restoreValue ? getPersistedInt(mProgress)
				: (Integer) defaultValue);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mTrackingTouch = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mTrackingTouch = false;
		if (seekBar.getProgress() != mProgress) {
			syncProgress(seekBar);
		}
	}

	public void setMax(int max) {
		if (max != mMax) {
			mMax = max;
			notifyChanged();
		}
	}

	public void setProgress(int progress) {
		setProgress(progress, true);
	}

	private void setProgress(int progress, boolean notifyChanged) {
		if (progress > mMax) {
			progress = mMax;
		}
		if (progress < 0) {
			progress = 0;
		}
		if (progress != mProgress) {
			mProgress = progress;
			persistInt(progress);
			if (notifyChanged) {
				notifyChanged();
			}
		}
	}

	void syncProgress(SeekBar seekBar) {
		int progress = seekBar.getProgress();
		if (progress != mProgress) {
			if (callChangeListener(progress)) {
				setProgress(progress, false);
			} else {
				seekBar.setProgress(mProgress);
			}
		}
	}
}