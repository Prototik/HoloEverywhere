package com.WazaBe.HoloEverywhere.preference;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.WazaBe.HoloEverywhere.R;

public class VolumePreference extends SeekBarDialogPreference implements
		PreferenceManager.OnActivityStopListener, View.OnKeyListener {
	private static class SavedState extends BaseSavedState {
		VolumeStore mVolumeStore = new VolumeStore();

		public SavedState(Parcel source) {
			super(source);
			mVolumeStore.volume = source.readInt();
			mVolumeStore.originalVolume = source.readInt();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		VolumeStore getVolumeStore() {
			return mVolumeStore;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mVolumeStore.volume);
			dest.writeInt(mVolumeStore.originalVolume);
		}
	}

	public class SeekBarVolumizer implements OnSeekBarChangeListener, Runnable {
		private AudioManager mAudioManager;
		private Context mContext;
		private Handler mHandler = new Handler();
		private int mLastProgress = -1;
		private int mOriginalStreamVolume;
		private Ringtone mRingtone;
		private SeekBar mSeekBar;
		private int mStreamType;
		private int mVolumeBeforeMute = -1;

		private ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if (mSeekBar != null && mAudioManager != null) {
					int volume = mAudioManager.getStreamVolume(mStreamType);
					mSeekBar.setProgress(volume);
				}
			}
		};

		public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
			this(context, seekBar, streamType, null);
		}

		public SeekBarVolumizer(Context context, SeekBar seekBar,
				int streamType, Uri defaultUri) {
			mContext = context;
			mAudioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			mStreamType = streamType;
			mSeekBar = seekBar;

			initSeekBar(seekBar, defaultUri);
		}

		public void changeVolumeBy(int amount) {
			mSeekBar.incrementProgressBy(amount);
			if (!isSamplePlaying()) {
				startSample();
			}
			postSetVolume(mSeekBar.getProgress());
			mVolumeBeforeMute = -1;
		}

		public SeekBar getSeekBar() {
			return mSeekBar;
		}

		@SuppressLint("NewApi")
		private void initSeekBar(SeekBar seekBar, Uri defaultUri) {
			seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
			mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
			seekBar.setProgress(mOriginalStreamVolume);
			seekBar.setOnSeekBarChangeListener(this);
			// TODO fix content observer
			/*
			 * mContext.getContentResolver().registerContentObserver(
			 * System.getUriFor(System.VOLUME_SETTINGS[mStreamType]), false,
			 * mVolumeObserver);
			 */
			if (defaultUri == null) {
				if (mStreamType == AudioManager.STREAM_RING) {
					defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
				} else if (mStreamType == AudioManager.STREAM_NOTIFICATION) {
					defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
				} else {
					if (VERSION.SDK_INT >= 5) {
						defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
					} else {
						defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
					}
				}
			}
			mRingtone = RingtoneManager.getRingtone(mContext, defaultUri);

			if (mRingtone != null) {
				mRingtone.setStreamType(mStreamType);
			}
		}

		public boolean isSamplePlaying() {
			return mRingtone != null && mRingtone.isPlaying();
		}

		public void muteVolume() {
			if (mVolumeBeforeMute != -1) {
				mSeekBar.setProgress(mVolumeBeforeMute);
				startSample();
				postSetVolume(mVolumeBeforeMute);
				mVolumeBeforeMute = -1;
			} else {
				mVolumeBeforeMute = mSeekBar.getProgress();
				mSeekBar.setProgress(0);
				stopSample();
				postSetVolume(0);
			}
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromTouch) {
			if (!fromTouch) {
				return;
			}

			postSetVolume(progress);
		}

		public void onRestoreInstanceState(VolumeStore volumeStore) {
			if (volumeStore.volume != -1) {
				mOriginalStreamVolume = volumeStore.originalVolume;
				mLastProgress = volumeStore.volume;
				postSetVolume(mLastProgress);
			}
		}

		public void onSaveInstanceState(VolumeStore volumeStore) {
			if (mLastProgress >= 0) {
				volumeStore.volume = mLastProgress;
				volumeStore.originalVolume = mOriginalStreamVolume;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (!isSamplePlaying()) {
				startSample();
			}
		}

		void postSetVolume(int progress) {
			mLastProgress = progress;
			mHandler.removeCallbacks(this);
			mHandler.post(this);
		}

		public void revertVolume() {
			mAudioManager
					.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
		}

		@Override
		public void run() {
			mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
		}

		public void startSample() {
			onSampleStarting(this);
			if (mRingtone != null) {
				mRingtone.play();
			}
		}

		public void stop() {
			stopSample();
			mContext.getContentResolver().unregisterContentObserver(
					mVolumeObserver);
			mSeekBar.setOnSeekBarChangeListener(null);
		}

		public void stopSample() {
			if (mRingtone != null) {
				mRingtone.stop();
			}
		}
	}

	public static class VolumeStore {
		public int originalVolume = -1;
		public int volume = -1;
	}

	private SeekBarVolumizer mSeekBarVolumizer;

	private int mStreamType;

	public VolumePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.VolumePreference, 0, 0);
		mStreamType = a.getInt(R.styleable.VolumePreference_streamType, 0);
		a.recycle();
	}

	private void cleanup() {
		getPreferenceManager().unregisterOnActivityStopListener(this);

		if (mSeekBarVolumizer != null) {
			Dialog dialog = getDialog();
			if (dialog != null && dialog.isShowing()) {
				View view = dialog.getWindow().getDecorView()
						.findViewById(R.id.seekbar);
				if (view != null) {
					view.setOnKeyListener(null);
				}
				mSeekBarVolumizer.revertVolume();
			}
			mSeekBarVolumizer.stop();
			mSeekBarVolumizer = null;
		}

	}

	@Override
	public void onActivityStop() {
		if (mSeekBarVolumizer != null) {
			mSeekBarVolumizer.stopSample();
		}
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
		mSeekBarVolumizer = new SeekBarVolumizer(getContext(), seekBar,
				mStreamType);
		getPreferenceManager().registerOnActivityStopListener(this);
		view.setOnKeyListener(this);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (!positiveResult && mSeekBarVolumizer != null) {
			mSeekBarVolumizer.revertVolume();
		}

		cleanup();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (mSeekBarVolumizer == null) {
			return true;
		}
		boolean isdown = event.getAction() == KeyEvent.ACTION_DOWN;
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (isdown) {
				mSeekBarVolumizer.changeVolumeBy(-1);
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (isdown) {
				mSeekBarVolumizer.changeVolumeBy(1);
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			if (isdown) {
				mSeekBarVolumizer.muteVolume();
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (mSeekBarVolumizer != null) {
			mSeekBarVolumizer.onRestoreInstanceState(myState.getVolumeStore());
		}
	}

	protected void onSampleStarting(SeekBarVolumizer volumizer) {
		if (mSeekBarVolumizer != null && volumizer != mSeekBarVolumizer) {
			mSeekBarVolumizer.stopSample();
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		if (mSeekBarVolumizer != null) {
			mSeekBarVolumizer.onSaveInstanceState(myState.getVolumeStore());
		}
		return myState;
	}

	public void setStreamType(int streamType) {
		mStreamType = streamType;
	}
}