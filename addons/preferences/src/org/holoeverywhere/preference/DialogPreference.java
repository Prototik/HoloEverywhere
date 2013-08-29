
package org.holoeverywhere.preference;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ContextThemeWrapperPlus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public abstract class DialogPreference extends Preference implements
        DialogInterface.OnClickListener, DialogInterface.OnDismissListener,
        PreferenceManager.OnActivityDestroyListener {
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

    private AlertDialog.Builder mBuilder;
    private Dialog mDialog;
    private Context mDialogContext;
    private Drawable mDialogIcon;
    private int mDialogLayoutResId;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private InputMethodManager mInputMethodManager;

    private CharSequence mNegativeButtonText;

    private CharSequence mPositiveButtonText;

    private int mWhichButtonClicked;

    public DialogPreference(Context context) {
        this(context, null);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DialogPreference, defStyle, R.style.Holo_PreferenceDialog);
        mDialogTitle = a.getString(R.styleable.DialogPreference_dialogTitle);
        if (mDialogTitle == null) {
            mDialogTitle = getTitle();
        }
        mDialogMessage = a
                .getString(R.styleable.DialogPreference_dialogMessage);
        mDialogIcon = a.getDrawable(R.styleable.DialogPreference_dialogIcon);
        mPositiveButtonText = a
                .getString(R.styleable.DialogPreference_positiveButtonText);
        mNegativeButtonText = a
                .getString(R.styleable.DialogPreference_negativeButtonText);
        mDialogLayoutResId = a.getResourceId(
                R.styleable.DialogPreference_dialogLayout, mDialogLayoutResId);
        a.recycle();

    }

    public Dialog getDialog() {
        return mDialog;
    }

    protected Context getDialogContext(boolean alert) {
        if (mDialogContext != null) {
            return mDialogContext;
        }
        final TypedArray a = getContext().obtainStyledAttributes(new int[] {
                alert ? R.attr.alertDialogTheme : R.attr.dialogTheme
        });
        final int theme = a.getResourceId(0, R.style.Holo_Theme_Dialog_Alert);
        a.recycle();
        return mDialogContext = new ContextThemeWrapperPlus(getContext(), theme);
    }

    public Drawable getDialogIcon() {
        return mDialogIcon;
    }

    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }

    public CharSequence getDialogMessage() {
        return mDialogMessage;
    }

    public CharSequence getDialogTitle() {
        return mDialogTitle;
    }

    public CharSequence getNegativeButtonText() {
        return mNegativeButtonText;
    }

    public CharSequence getPositiveButtonText() {
        return mPositiveButtonText;
    }

    protected boolean needInputMethod() {
        return false;
    }

    @Override
    public void onActivityDestroy() {
        if (mDialog == null || !mDialog.isShowing()) {
            return;
        }
        mDialog.dismiss();
    }

    protected void onBindDialogView(View view) {
        View dialogMessageView = view.findViewById(R.id.message);
        if (dialogMessageView != null) {
            final CharSequence message = getDialogMessage();
            int newVisibility = View.GONE;
            if (!TextUtils.isEmpty(message)) {
                if (dialogMessageView instanceof TextView) {
                    ((TextView) dialogMessageView).setText(message);
                }
                newVisibility = View.VISIBLE;
            }
            if (dialogMessageView.getVisibility() != newVisibility) {
                dialogMessageView.setVisibility(newVisibility);
            }
        }
    }

    @Override
    protected void onClick() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        showDialog(null);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mWhichButtonClicked = which;
    }

    protected Dialog onCreateDialog(Context context) {
        context = getDialogContext(true);
        mBuilder = new AlertDialog.Builder(context,
                ((ContextThemeWrapperPlus) context).getThemeResource());
        mBuilder.setTitle(mDialogTitle);
        mBuilder.setIcon(mDialogIcon);
        mBuilder.setPositiveButton(mPositiveButtonText, this);
        mBuilder.setNegativeButton(mNegativeButtonText, this);
        View contentView = onCreateDialogView(context);
        if (contentView != null) {
            onBindDialogView(contentView);
            mBuilder.setView(contentView);
        } else {
            mBuilder.setMessage(mDialogMessage);
        }
        onPrepareDialogBuilder(mBuilder);
        return mBuilder.create();
    }

    /**
     * Use {@link #onCreateDialogView(Context)} instead
     */
    @Deprecated
    protected View onCreateDialogView() {
        return null;
    }

    protected View onCreateDialogView(Context context) {
        final View view = onCreateDialogView();
        if (view != null) {
            return view;
        }
        if (mDialogLayoutResId == 0) {
            return null;
        }
        return LayoutInflater.inflate(context, mDialogLayoutResId);
    }

    protected void onDialogClosed(boolean positiveResult) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
        }
        if (mDialog != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mDialog.getWindow().getDecorView()
                    .getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        getPreferenceManager().unregisterOnActivityDestroyListener(this);
        mDialog = null;
        onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
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
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setDialogIcon(Drawable dialogIcon) {
        mDialogIcon = dialogIcon;
    }

    public void setDialogIcon(int dialogIconRes) {
        mDialogIcon = getContext().getResources().getDrawable(dialogIconRes);
    }

    public void setDialogLayoutResource(int dialogLayoutResId) {
        mDialogLayoutResId = dialogLayoutResId;
    }

    public void setDialogMessage(CharSequence dialogMessage) {
        mDialogMessage = dialogMessage;
    }

    public void setDialogMessage(int dialogMessageResId) {
        setDialogMessage(getContext().getString(dialogMessageResId));
    }

    public void setDialogTitle(CharSequence dialogTitle) {
        mDialogTitle = dialogTitle;
    }

    public void setDialogTitle(int dialogTitleResId) {
        setDialogTitle(getContext().getString(dialogTitleResId));
    }

    public void setNegativeButtonText(CharSequence negativeButtonText) {
        mNegativeButtonText = negativeButtonText;
    }

    public void setNegativeButtonText(int negativeButtonTextResId) {
        setNegativeButtonText(getContext().getString(negativeButtonTextResId));
    }

    public void setPositiveButtonText(CharSequence positiveButtonText) {
        mPositiveButtonText = positiveButtonText;
    }

    public void setPositiveButtonText(int positiveButtonTextResId) {
        setPositiveButtonText(getContext().getString(positiveButtonTextResId));
    }

    protected void showDialog(Bundle state) {
        mWhichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
        mDialog = onCreateDialog(getContext());
        getPreferenceManager().registerOnActivityDestroyListener(this);
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        if (needInputMethod()) {
            requestInputMethod(mDialog);
        }
        mDialog.setOnDismissListener(this);
        mDialog.show();
    }
}
