
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

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

    public void init(ListAdapter adapter,
            OnItemClickListener onItemClickListener, int theme) {
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClickListener);
        list.performItemClick(null, 0, 0);
        final int themePicker;
        if (ThemeManager.isDark(theme)) {
            themePicker = R.id.themePickerDark;
        } else if (ThemeManager.isLight(theme)) {
            themePicker = R.id.themePickerLight;
        } else {
            themePicker = R.id.themePickerMixed;
        }
        ((DemoNavigationItem) findViewById(themePicker))
                .setSelectionHandlerColorResource(R.color.holo_blue_light);
    }
}
