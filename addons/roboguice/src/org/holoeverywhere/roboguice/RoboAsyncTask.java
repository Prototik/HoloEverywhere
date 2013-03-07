
package org.holoeverywhere.roboguice;

import java.util.concurrent.Executor;

import org.holoeverywhere.addon.AddonRoboguice;

import roboguice.util.SafeAsyncTask;
import android.content.Context;
import android.os.Handler;

public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    private final Context mContext;

    protected RoboAsyncTask(Context context) {
        inject(mContext = context);
    }

    protected RoboAsyncTask(Context context, Executor executor) {
        super(executor);
        inject(mContext = context);
    }

    protected RoboAsyncTask(Context context, Handler handler) {
        super(handler);
        inject(mContext = context);
    }

    protected RoboAsyncTask(Context context, Handler handler, Executor executor) {
        super(handler, executor);
        inject(mContext = context);
    }

    public Context getContext() {
        return mContext;
    }

    private void inject(Context context) {
        AddonRoboguice.getInjector(context).injectMembers(this);
    }
}
