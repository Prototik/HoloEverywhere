
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.util.AttributeSet;

public class DemoNavigationWidget extends LinearLayout {

    private final ListView list;

    public DemoNavigationWidget(Context context) {
        this(context, null);
    }

    public DemoNavigationWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoNavigationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.inflate(context, R.layout.demo_navigation_widget, this, true);
        list = (ListView) findViewById(R.id.navigationListView);
    }

    public ListView getListView() {
        return list;
    }
}
