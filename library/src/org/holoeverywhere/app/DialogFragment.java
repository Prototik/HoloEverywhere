
package org.holoeverywhere.app;

import java.util.Random;

import org.holoeverywhere.LayoutInflater;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DialogFragment extends Fragment implements
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    public static final class DialogTransaction {
        public String dialogTag;
        public FragmentManager fragmentManager;
        public int transactionId;
    }

    public static enum DialogType {
        AlertDialog, Dialog
    }

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
    private final String DIALOG_TAG = getClass().getName() + "@"
            + Integer.toHexString(new Random().nextInt(Integer.MAX_VALUE));
    int mBackStackId = -1;
    boolean mCancelable = true;
    Dialog mDialog;
    boolean mDismissed = true;
    boolean mShownByMe;
    boolean mShowsDialog = true;
    int mStyle = STYLE_NORMAL;
    int mTheme = 0;
    boolean mViewDestroyed;
    private DialogType type = DialogType.Dialog;

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

    public DialogType getDialogType() {
        return type;
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
        if (mDialog != null) {
            return LayoutInflater.from(mDialog.getContext());
        }
        return LayoutInflater.from(getActivity());
    }

    public boolean getShowsDialog() {
        return mShowsDialog;
    }

    public int getTheme() {
        return mTheme;
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
            if (mDialog instanceof AlertDialog) {
                ((AlertDialog) mDialog).setView(view);
            } else {
                mDialog.setContentView(view);
            }
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
        mShowsDialog = getContainerId() == 0;
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
        switch (type) {
            case AlertDialog:
                return new AlertDialog(getActivity(), getTheme());
            case Dialog:
            default:
                return new Dialog(getActivity(), getTheme());
        }
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

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }

    public void setDialogType(DialogType type) {
        this.type = type;
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

    public DialogTransaction show() {
        return show(getSupportFragmentManager());
    }

    public DialogTransaction show(Activity activity) {
        return show(activity == null ? null : activity
                .getSupportFragmentManager());
    }

    public DialogTransaction show(FragmentManager fm) {
        return show(fm, fm == null ? null : fm.beginTransaction());
    }

    public DialogTransaction show(FragmentManager fm, FragmentTransaction ft) {
        if (ft == null) {
            if (fm == null) {
                Activity activity = getSupportActivity();
                if (activity == null) {
                    throw new RuntimeException(
                            "DialogFragment don't have any reference to Context or FragmentManager");
                }
                fm = activity.getSupportFragmentManager();
            }
            ft = fm.beginTransaction();
        }
        if (!mDismissed) {
            dismiss();
        }
        DialogTransaction dialogTransaction = new DialogTransaction();
        dialogTransaction.fragmentManager = fm;
        dialogTransaction.dialogTag = DIALOG_TAG;
        dialogTransaction.transactionId = show(ft, DIALOG_TAG);
        return dialogTransaction;
    }

    @Deprecated
    public void show(FragmentManager manager, String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    public DialogTransaction show(FragmentTransaction ft) {
        return show(null, ft);
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
