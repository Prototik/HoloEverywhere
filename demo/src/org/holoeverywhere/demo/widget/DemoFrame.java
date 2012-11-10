
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.demo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

public class DemoFrame extends ViewAnimator {
    public DemoFrame(Context context) {
        this(context, null);
    }

    public DemoFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setInAnimation(context, R.anim.demo_frame_in);
        setAnimateFirstView(true);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        setDisplayedChild(getChildCount() - 1);
    }

}
