
package org.holoeverywhere.text;

import java.util.Locale;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class AllCapsTransformationMethod implements TransformationMethod {
    private static final String TAG = "AllCapsTransformationMethod";

    private boolean mEnabled;
    private Locale mLocale;

    public AllCapsTransformationMethod(Context context) {
        mLocale = context.getResources().getConfiguration().locale;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        if (mEnabled) {
            return source != null ? source.toString().toUpperCase(mLocale) : null;
        }
        Log.w(TAG, "Caller did not enable length changes; not transforming text");
        return source;
    }

    @Override
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction,
            Rect previouslyFocusedRect) {
    }

    @Override
    public void setLengthChangesAllowed(boolean allowLengthChanges) {
        mEnabled = allowLengthChanges;
    }
}
