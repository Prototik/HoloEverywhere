package org.holoeverywhere.internal;

import android.content.Context;
import android.graphics.Rect;

public class ActivityDecorView extends WindowDecorView
{
    private boolean mIsOverlay;

    public ActivityDecorView(Context context, boolean isOverlay)
    {
        super(context);
        mIsOverlay = isOverlay;
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if(!mIsOverlay)
            super.fitSystemWindows(insets);
        return true;
    }
}
