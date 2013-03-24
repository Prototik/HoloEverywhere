
package org.holoeverywhere.bug.i361;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class MyCustomWidget extends RelativeLayout implements Checkable {
    public MyCustomWidget(Context context) {
        this(context, null);
    }

    public MyCustomWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private TextView mStateLabel;

    public MyCustomWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.inflate(context, R.layout.my_row, this, true);
        mStateLabel = (TextView) findViewById(R.id.stateLabel);
        /**
         * Reset state to unchecked
         */
        mChecked = true;
        setChecked(false);
    }

    private boolean mChecked;

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        setBackgroundColor(checked ? 0xFFFF8800 : 0x30FFBB33);
        mStateLabel.setText(checked ? "Checked" : "Unchecked");
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
