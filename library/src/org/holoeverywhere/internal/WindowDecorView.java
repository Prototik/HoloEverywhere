
package org.holoeverywhere.internal;

import static android.view.View.MeasureSpec.EXACTLY;

import org.holoeverywhere.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;

public class WindowDecorView extends ContextMenuDecorView {
    private final TypedValue mMinWidthMajor = new TypedValue();
    private final TypedValue mMinWidthMinor = new TypedValue();

    public WindowDecorView(Context context, View view,
            android.view.ViewGroup.LayoutParams params, ContextMenuListener listener) {
        super(context, view, params, listener);
        TypedArray a = context.obtainStyledAttributes(new int[] {
                R.attr.windowMinWidthMajor, R.attr.windowMinWidthMinor
        });
        a.getValue(0, mMinWidthMajor);
        a.getValue(1, mMinWidthMinor);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        boolean measure = false;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);
        final TypedValue tv = isPortrait ? mMinWidthMinor : mMinWidthMajor;
        if (tv.type != TypedValue.TYPE_NULL) {
            final int min;
            if (tv.type == TypedValue.TYPE_DIMENSION) {
                min = (int) tv.getDimension(metrics);
            } else if (tv.type == TypedValue.TYPE_FRACTION) {
                min = (int) tv.getFraction(metrics.widthPixels, metrics.widthPixels);
            } else {
                min = 0;
            }
            if (width < min) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, EXACTLY);
                measure = true;
            }
        }
        if (measure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
