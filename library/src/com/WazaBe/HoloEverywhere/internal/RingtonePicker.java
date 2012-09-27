package com.WazaBe.HoloEverywhere.internal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.AlertDialog;
import com.WazaBe.HoloEverywhere.app.AlertDialog.Builder;
import com.WazaBe.HoloEverywhere.internal.AlertController.AlertParams.OnPrepareListViewListener;
import com.WazaBe.HoloEverywhere.widget.ListView;

public class RingtonePicker implements OnItemSelectedListener, Runnable,
		OnClickListener, OnPrepareListViewListener, OnCancelListener,
		OnDismissListener {
	public static interface RingtonePickerListener {
		public void onRingtonePickerCanceled();

		public void onRingtonePickerChanged(Uri uri);
	}

	private static final int DELAY_MS_SELECTION_PLAYED = 300;
	private AlertDialog.Builder builder;
	private int clickedPos = -1, defaultRingtonePos = -1,
			sampleRingtonePos = -1, silentPos = -1, staticItemCount;
	private Context context;
	private Cursor cursor;
	private Ringtone defaultRingtone;
	private AlertDialog dialog;
	private Uri existingUri;
	private Handler handler;
	private boolean hasDefaultItem, hasSilentItem;
	private final Intent intent;
	private final RingtonePickerListener listener;
	private RingtoneManager ringtoneManager;
	private Uri uriForDefaultItem;

	public RingtonePicker(Context context, Intent intent,
			RingtonePickerListener listener) {
		this.context = context;
		this.intent = intent;
		this.listener = listener;
	}

	private int addDefaultRingtoneItem(ListView listView) {
		return addStaticItem(listView, R.string.ringtone_default);
	}

	private int addSilentItem(ListView listView) {
		return addStaticItem(listView, R.string.ringtone_silent);
	}

	private int addStaticItem(ListView listView, int textResId) {
		TextView textView = (TextView) LayoutInflater.inflate(context,
				R.layout.select_dialog_singlechoice_holo, listView, false);
		textView.setText(textResId);
		listView.addHeaderView(textView);
		staticItemCount++;
		return listView.getHeaderViewsCount() - 1;
	}

	public RingtonePicker cancel() {
		if (dialog != null) {
			dialog.cancel();
		}
		return this;
	}

	public RingtonePicker create() {
		if (dialog != null) {
			return this;
		}
		handler = new Handler();
		hasDefaultItem = intent.getBooleanExtra(
				RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		uriForDefaultItem = intent
				.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
		if (uriForDefaultItem == null) {
			uriForDefaultItem = Settings.System.DEFAULT_RINGTONE_URI;
		}
		hasSilentItem = intent.getBooleanExtra(
				RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
		ringtoneManager = new RingtoneManager(context);
		final boolean includeDrm = intent.getBooleanExtra(
				RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM, true);
		ringtoneManager.setIncludeDrm(includeDrm);
		int types = intent.getIntExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, -1);
		if (types != -1) {
			ringtoneManager.setType(types);
		}
		cursor = ringtoneManager.getCursor();
		if (context instanceof Activity) {
			((Activity) context).setVolumeControlStream(ringtoneManager
					.inferStreamType());
		}
		existingUri = intent
				.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI);
		builder = new AlertDialog.Builder(context);
		dialog = onCreateDialog(builder);
		return this;
	}

	public RingtonePicker dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
		return this;
	}

	private CharSequence getDialogTitle() {
		if (intent.hasExtra(RingtoneManager.EXTRA_RINGTONE_TITLE)) {
			return intent
					.getCharSequenceExtra(RingtoneManager.EXTRA_RINGTONE_TITLE);
		} else {
			return context.getText(R.string.ringtone_picker_title);
		}
	}

	private int getListPosition(int ringtoneManagerPos) {
		if (ringtoneManagerPos < 0) {
			return ringtoneManagerPos;
		}
		return ringtoneManagerPos + staticItemCount;
	}

	private int getRingtoneManagerPosition(int listPos) {
		return listPos - staticItemCount;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		stopAnyPlayingRingtone();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		boolean positiveResult = which == DialogInterface.BUTTON_POSITIVE;
		ringtoneManager.stopPreviousRingtone();
		if (positiveResult) {
			Uri uri = null;
			if (clickedPos == defaultRingtonePos) {
				uri = uriForDefaultItem;
			} else if (clickedPos == silentPos) {
				uri = null;
			} else {
				uri = ringtoneManager
						.getRingtoneUri(getRingtoneManagerPosition(clickedPos));
			}
			if (listener != null) {
				listener.onRingtonePickerChanged(uri);
			}
		} else {
			if (listener != null) {
				listener.onRingtonePickerCanceled();
			}
		}
	}

	private AlertDialog onCreateDialog(Builder builder) {
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);
		builder.setOnPrepareListViewListener(this);
		builder.setSingleChoiceItems(cursor, -1, MediaColumns.TITLE,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clickedPos = which;
						playRingtone(which, 0);
					}
				});
		builder.setTitle(getDialogTitle());
		builder.setOnCancelListener(this);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		stopAnyPlayingRingtone();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		playRingtone(position, DELAY_MS_SELECTION_PLAYED);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onPrepareListView(ListView listView) {
		if (hasDefaultItem) {
			defaultRingtonePos = addDefaultRingtoneItem(listView);
			if (RingtoneManager.isDefault(existingUri)) {
				clickedPos = defaultRingtonePos;
			}
		}
		if (hasSilentItem) {
			silentPos = addSilentItem(listView);
			if (existingUri == null) {
				clickedPos = silentPos;
			}
		}
		if (clickedPos == -1) {
			clickedPos = getListPosition(ringtoneManager
					.getRingtonePosition(existingUri));
		}
		builder.setCheckedItem(clickedPos);
	}

	private void playRingtone(int position, int delayMs) {
		handler.removeCallbacks(this);
		sampleRingtonePos = position;
		handler.postDelayed(this, delayMs);
	}

	@Override
	public void run() {
		if (sampleRingtonePos == silentPos) {
			ringtoneManager.stopPreviousRingtone();
			return;
		}
		if (defaultRingtone != null && defaultRingtone.isPlaying()) {
			defaultRingtone.stop();
			defaultRingtone = null;
		}
		Ringtone ringtone;
		if (sampleRingtonePos == defaultRingtonePos) {
			if (defaultRingtone == null) {
				defaultRingtone = RingtoneManager.getRingtone(context,
						uriForDefaultItem);
			}
			ringtone = defaultRingtone;
			ringtoneManager.stopPreviousRingtone();
		} else {
			ringtone = ringtoneManager
					.getRingtone(getRingtoneManagerPosition(sampleRingtonePos));
		}
		if (ringtone != null) {
			ringtone.play();
		}
	}

	public RingtonePicker show() {
		create();
		dialog.show();
		return this;
	}

	private void stopAnyPlayingRingtone() {
		if (defaultRingtone != null && defaultRingtone.isPlaying()) {
			defaultRingtone.stop();
		}
		if (ringtoneManager != null) {
			ringtoneManager.stopPreviousRingtone();
		}
	}
}