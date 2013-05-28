
package org.holoeverywhere.app;

import org.holoeverywhere.internal.AlertController;
import org.holoeverywhere.internal.AlertController.AlertDecorViewInstaller;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

public abstract class AlertActivity extends Activity implements DialogInterface,
        AlertDecorViewInstaller {
    protected AlertController mAlert;
    protected AlertController.AlertParams mAlertParams;

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void installDecorView(Context context, int layout) {
        setContentView(layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlert = new AlertController(this, this, getWindow(), this);
        mAlertParams = new AlertController.AlertParams(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void setupAlert() {
        mAlertParams.apply(mAlert);
        mAlert.installContent();
    }
}
