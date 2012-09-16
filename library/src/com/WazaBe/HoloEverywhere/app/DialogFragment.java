package com.WazaBe.HoloEverywhere.app;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.util.Pair;

public class DialogFragment extends Fragment implements
		DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
	private static final String SAVED_BACK_STACK_ID = "android:backStackId";
	private static final String SAVED_CANCELABLE = "android:cancelable";
	private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
	private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";

	private static final String SAVED_STYLE = "android:style";
	private static final String SAVED_THEME = "android:theme";
	public static final int STYLE_NO_FRAME = 2;
	public static final int STYLE_NO_INPUT = 3;
	public static final int STYLE_NO_TITLE = 1;
	public static final int STYLE_NORMAL = 0;

	protected final String classTag = getClass().getName() + "@!"
			+ Integer.toHexString(getClass().hashCode());
	int mBackStackId = -1;
	boolean mCancelable = true;
	Dialog mDialog;
	boolean mDismissed;

	boolean mShownByMe;
	boolean mShowsDialog = true;
	int mStyle = STYLE_NORMAL;
	int mTheme = 0;

	boolean mViewDestroyed;

	public void dismiss() {
		dismissInternal(false);
	}

	public void dismissAllowingStateLoss() {
		dismissInternal(true);
	}

	void dismissInternal(boolean allowStateLoss) {
		if (mDismissed) {
			return;
		}
		mDismissed = true;
		mShownByMe = false;
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mViewDestroyed = true;
		if (mBackStackId >= 0) {
			getFragmentManager().popBackStack(mBackStackId,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mBackStackId = -1;
		} else {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(this);
			if (allowStateLoss) {
				ft.commitAllowingStateLoss();
			} else {
				ft.commit();
			}
		}
	}

	public Dialog getDialog() {
		return mDialog;
	}

	@Override
	public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
		if (!mShowsDialog) {
			return super.getLayoutInflater(savedInstanceState);
		}

		mDialog = onCreateDialog(savedInstanceState);
		switch (mStyle) {
		case STYLE_NO_INPUT:
			mDialog.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		case STYLE_NO_FRAME:
		case STYLE_NO_TITLE:
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		try {
			if (mDialog != null) {
				return (LayoutInflater) mDialog.getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
			}
			return (LayoutInflater) getSupportActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		} catch (ClassCastException e) {
			return super.getLayoutInflater(savedInstanceState);
		}
	}

	public boolean getShowsDialog() {
		return mShowsDialog;
	}

	public int getTheme() {
		return mTheme;
	}

	public void hide() {
		hide(getSupportFragmentManager().beginTransaction());
	}

	public void hide(FragmentManager fm, FragmentTransaction ft) {
		Fragment fragment = (Fragment) fm.findFragmentByTag(classTag);
		if (fragment != null) {
			ft.remove(fragment);
		}
	}

	public void hide(FragmentTransaction ft) {
		hide(getSupportFragmentManager(), ft);
	}

	public boolean isCancelable() {
		return mCancelable;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!mShowsDialog) {
			return;
		}
		View view = getView();
		if (view != null) {
			if (view.getParent() != null) {
				throw new IllegalStateException(
						"DialogFragment can not be attached to a container view");
			}
			mDialog.setContentView(view);
		}
		mDialog.setOwnerActivity(getActivity());
		mDialog.setCancelable(mCancelable);
		mDialog.setOnCancelListener(this);
		mDialog.setOnDismissListener(this);
		if (savedInstanceState != null) {
			Bundle dialogState = savedInstanceState
					.getBundle(SAVED_DIALOG_STATE_TAG);
			if (dialogState != null) {
				mDialog.onRestoreInstanceState(dialogState);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!mShownByMe) {
			mDismissed = false;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Field field = getClass().getField("mContainerId");
			field.setAccessible(true);
			mShowsDialog = (Integer) field.get(field) == 0;
		} catch (Exception e) {
			mShowsDialog = false;
		}
		if (savedInstanceState != null) {
			mStyle = savedInstanceState.getInt(SAVED_STYLE, STYLE_NORMAL);
			mTheme = savedInstanceState.getInt(SAVED_THEME, 0);
			mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
			mShowsDialog = savedInstanceState.getBoolean(SAVED_SHOWS_DIALOG,
					mShowsDialog);
			mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
		}

	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new Dialog(getActivity(), getTheme());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mDialog != null) {
			mViewDestroyed = true;
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (!mShownByMe && !mDismissed) {
			mDismissed = true;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (!mViewDestroyed) {
			dismissInternal(true);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mDialog != null) {
			Bundle dialogState = mDialog.onSaveInstanceState();
			if (dialogState != null) {
				outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
			}
		}
		if (mStyle != STYLE_NORMAL) {
			outState.putInt(SAVED_STYLE, mStyle);
		}
		if (mTheme != 0) {
			outState.putInt(SAVED_THEME, mTheme);
		}
		if (!mCancelable) {
			outState.putBoolean(SAVED_CANCELABLE, mCancelable);
		}
		if (!mShowsDialog) {
			outState.putBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
		}
		if (mBackStackId != -1) {
			outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mDialog != null) {
			mViewDestroyed = false;
			mDialog.show();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mDialog != null) {
			mDialog.hide();
		}
	}

	public Pair<FragmentTransaction, Integer> replace() {
		return replace(getSupportFragmentManager());
	}

	public Pair<FragmentTransaction, Integer> replace(FragmentManager fm) {
		return replace(fm, fm.beginTransaction());
	}

	public Pair<FragmentTransaction, Integer> replace(FragmentManager fm,
			FragmentTransaction ft) {
		hide(fm, ft);
		ft.addToBackStack(null);
		return show(ft);
	}

	public Pair<FragmentTransaction, Integer> replace(FragmentTransaction ft) {
		return replace(getSupportFragmentManager(), ft);
	}

	public void setCancelable(boolean cancelable) {
		mCancelable = cancelable;
		if (mDialog != null) {
			mDialog.setCancelable(cancelable);
		}
	}

	public void setShowsDialog(boolean showsDialog) {
		mShowsDialog = showsDialog;
	}

	public void setStyle(int style, int theme) {
		mStyle = style;
		if (mStyle == STYLE_NO_FRAME || mStyle == STYLE_NO_INPUT) {
			mTheme = android.R.style.Theme_Panel;
		}
		if (theme != 0) {
			mTheme = theme;
		}
	}

	public Pair<FragmentTransaction, Integer> show() {
		return show(getSupportFragmentManager().beginTransaction());
	}

	@Deprecated
	public void show(FragmentManager manager, String tag) {
		mDismissed = false;
		mShownByMe = true;
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commit();
	}

	public Pair<FragmentTransaction, Integer> show(FragmentTransaction ft) {
		return Pair.create(ft, show(ft, classTag));
	}

	@Deprecated
	public int show(FragmentTransaction transaction, String tag) {
		mDismissed = false;
		mShownByMe = true;
		transaction.add(this, tag);
		mViewDestroyed = false;
		mBackStackId = transaction.commit();
		return mBackStackId;
	}
}
