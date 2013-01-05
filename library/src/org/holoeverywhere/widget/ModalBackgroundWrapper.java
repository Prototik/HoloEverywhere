
package org.holoeverywhere.widget;

import org.holoeverywhere.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Checkable;

public class ModalBackgroundWrapper extends FrameLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    private boolean mChecked = false;

    public ModalBackgroundWrapper(Context context) {
        this(context, null);
    }

    public ModalBackgroundWrapper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModalBackgroundWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (getBackground() == null) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.modalBackgroundWrapper, value, true);
            setBackgroundResource(value.resourceId);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            synchronized (this) {
                final int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child instanceof Checkable) {
                        ((Checkable) child).setChecked(checked);
                    }
                }
            }
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }
}
