
package org.holoeverywhere.widget;

import android.content.Context;
import android.util.AttributeSet;

public class GridView extends android.widget.GridView {
    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
