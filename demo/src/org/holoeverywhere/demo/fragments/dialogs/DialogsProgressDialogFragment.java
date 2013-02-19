
package org.holoeverywhere.demo.fragments.dialogs;

import java.util.concurrent.TimeUnit;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

public class DialogsProgressDialogFragment extends DialogFragment {
    private final class CustomTask extends AsyncTask<Void, Integer, Void> {
        private int mInitalPosition = 0;

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = mInitalPosition; i <= 100; i++) {
                if (isCancelled()) {
                    return null;
                }
                publishProgress(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            mLastPosition = 0;
            if (getDialog() != null && getDialog().isShowing()) {
                dismiss();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mDialog != null) {
                mDialog.setProgress(mLastPosition = values[0]);
            }
        }
    }

    private static final String KEY_INITIAL_POSITION = "initial_position";
    private ProgressDialog mDialog;
    private int mLastPosition = 0;
    private CustomTask mTask;

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onCancel(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastPosition = savedInstanceState.getInt(KEY_INITIAL_POSITION, 0);
        }
        mDialog = new ProgressDialog(getSupportActivity(), getTheme());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setIndeterminate(false);
        mDialog.setMax(100);
        mDialog.setMessage("Task running...");
        mDialog.setProgressNumberFormat("");
        mDialog.setCancelable(true);
        mTask = new CustomTask();
        mTask.mInitalPosition = mLastPosition;
        mTask.execute();
        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INITIAL_POSITION, mLastPosition);
    }
}
