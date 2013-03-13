
package org.holoeverywhere.demo.widget;

import java.util.Hashtable;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.FrameLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class DemoThemePicker extends FrameLayout {
    private final class ThemeChangeListener implements OnClickListener {
        private final int theme;

        private ThemeChangeListener(int theme) {
            this.theme = theme;
        }

        @Override
        public void onClick(View view) {
            ThemeManager.setDefaultTheme(theme);
            ThemeManager.restart(activity);
        }
    }

    private static final Hashtable<Integer, Integer> THEME_HASHTABLE = new Hashtable<Integer, Integer>();

    static {
        THEME_HASHTABLE.put(ThemeManager.DARK, R.id.dark);
        THEME_HASHTABLE.put(ThemeManager.LIGHT, R.id.light);
        THEME_HASHTABLE.put(ThemeManager.MIXED, R.id.mixed);
        THEME_HASHTABLE.put(ThemeManager.DARK | ThemeManager.FULLSCREEN, R.id.dark);
        THEME_HASHTABLE.put(ThemeManager.LIGHT | ThemeManager.FULLSCREEN, R.id.light);
        THEME_HASHTABLE.put(ThemeManager.MIXED | ThemeManager.FULLSCREEN, R.id.mixed);
    }

    private final Activity activity;

    public DemoThemePicker(Context context) {
        this(context, null);
    }

    public DemoThemePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoThemePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(
                context,
                attrs,
                defStyleAttr,
                context.obtainStyledAttributes(attrs, new int[] {
                        android.R.attr.orientation
                }, defStyleAttr, 0).getInt(0, android.widget.LinearLayout.VERTICAL) == android.widget.LinearLayout.HORIZONTAL);
    }

    public DemoThemePicker(Context context, AttributeSet attrs, int defStyleAttr,
            boolean horizontal) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new RuntimeException("Context is not Activity");
        }
        activity = (Activity) context;
        int layout;
        if (horizontal) {
            layout = R.layout.theme_picker_horizontal;
        } else {
            layout = R.layout.theme_picker_vertical;
        }
        addView(LayoutInflater.inflate(context, layout, this, false));
        findViewById(R.id.dark).setOnClickListener(new ThemeChangeListener(ThemeManager.DARK));
        findViewById(R.id.light).setOnClickListener(new ThemeChangeListener(ThemeManager.LIGHT));
        findViewById(R.id.mixed).setOnClickListener(new ThemeChangeListener(ThemeManager.MIXED));
        ((DemoListRowView) findViewById(THEME_HASHTABLE.get(ThemeManager.getTheme(activity))))
                .setSelectionHandlerVisiblity(true);
    }

}
