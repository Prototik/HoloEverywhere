package com.WazaBe.HoloEverywhere.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.AlertDialog;

public final class PreferenceScreen extends PreferenceGroup implements
		AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {
	private static class SavedState extends BaseSavedState {
		Bundle dialogBundle;
		boolean isDialogShowing;

		public SavedState(Parcel source) {
			super(source);
			isDialogShowing = source.readInt() == 1;
			dialogBundle = source.readBundle();
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(isDialogShowing ? 1 : 0);
			dest.writeBundle(dialogBundle);
		}
	}

	private AlertDialog mDialog;
	private ListView mListView;
	private ListAdapter mRootAdapter;

	public PreferenceScreen(Context context, AttributeSet attrs) {
		super(context, attrs, R.attr.preferenceScreenStyle);
	}

	public void bind(ListView listView) {
		listView.setOnItemClickListener(this);
		listView.setAdapter(getRootAdapter());
		onAttachedToActivity();
	}

	public AlertDialog getDialog() {
		return mDialog;
	}

	public ListAdapter getRootAdapter() {
		if (mRootAdapter == null) {
			mRootAdapter = onCreateRootAdapter();
		}

		return mRootAdapter;
	}

	@Override
	protected boolean isOnSameScreenAsChildren() {
		return false;
	}

	@Override
	protected void onClick() {
		if (getIntent() != null || getFragment() != null
				|| getPreferenceCount() == 0) {
			return;
		}

		showDialog(null);
	}

	protected ListAdapter onCreateRootAdapter() {
		return new PreferenceGroupAdapter(this);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		mDialog = null;
		getPreferenceManager().removePreferencesScreen(dialog);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent instanceof ListView) {
			position -= ((ListView) parent).getHeaderViewsCount();
		}
		Object item = getRootAdapter().getItem(position);
		if (!(item instanceof Preference)) {
			return;
		}

		final Preference preference = (Preference) item;
		preference.performClick(this);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (myState.isDialogShowing) {
			showDialog(myState.dialogBundle);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		final AlertDialog dialog = mDialog;
		if (dialog == null || !dialog.isShowing()) {
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.isDialogShowing = true;
		myState.dialogBundle = dialog.onSaveInstanceState();
		return myState;
	}

	private void showDialog(Bundle state) {
		Context context = getContext();
		if (mListView != null) {
			mListView.setAdapter(null);
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View childPrefScreen = inflater.inflate(
				R.layout.preference_list_fragment, null);
		mListView = (ListView) childPrefScreen.findViewById(android.R.id.list);
		bind(mListView);
		final CharSequence title = getTitle();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(childPrefScreen);
		mDialog = builder.create();
		mDialog.setOnDismissListener(this);
		if (state != null) {
			mDialog.onRestoreInstanceState(state);
		}
		getPreferenceManager().addPreferencesScreen(mDialog);
		mDialog.show();
	}
}