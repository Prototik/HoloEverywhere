
package org.holoeverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;

public class DrawerLayout extends android.support.v4.widget.DrawerLayout {
    private boolean mFitSystemWindows = false;

    public DrawerLayout(Context context) {
        this(context, null);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (mFitSystemWindows) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                child.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            }
        }
        return super.fitSystemWindows(insets);
    }

    @SuppressLint("NewApi")
    @Override
    public void setFitsSystemWindows(boolean fitSystemWindows) {
        mFitSystemWindows = fitSystemWindows;
        requestLayout();
        if (VERSION.SDK_INT >= 14) {
            super.setFitsSystemWindows(fitSystemWindows);
        }
    }
}
