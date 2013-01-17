
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

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i <= 100; i++) {
                if (isCancelled()) {
                    return null;
                }
                publishProgress(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            dismiss();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mDialog != null) {
                mDialog.setProgress(values[0]);
            }
        }
    }

    private ProgressDialog mDialog;
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
        mDialog = new ProgressDialog(getSupportActivity(), getTheme());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setIndeterminate(false);
        mDialog.setMax(100);
        mDialog.setMessage("Task running...");
        mDialog.setProgressNumberFormat("");
        mDialog.setCancelable(true);
        mTask = new CustomTask();
        mTask.execute();
        return mDialog;
    }
}
