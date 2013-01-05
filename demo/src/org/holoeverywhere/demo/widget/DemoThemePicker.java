
package org.holoeverywhere.demo.widget;

import java.util.Hashtable;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.demo.PlaybackService;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public class DemoThemePicker extends FrameLayout {
    public DemoThemePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.orientation
        }, defStyleAttr, 0).getInt(0, LinearLayout.VERTICAL) == LinearLayout.HORIZONTAL);
    }

    private final Activity activity;

    private final class ThemeChangeListener implements OnClickListener {
        private final int theme;

        private ThemeChangeListener(int theme) {
            this.theme = theme;
        }

        @Override
        public void onClick(View view) {
            PlaybackService.ignore();
            ThemeManager.restartWithTheme(activity, theme);
        }
    }

    public DemoThemePicker(Context context, AttributeSet attrs, int defStyleAttr,
            boolean horizontal) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new RuntimeException("Context is not Activity");
        }
        this.activity = (Activity) context;
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

    private static final Hashtable<Integer, Integer> THEME_HASHTABLE = new Hashtable<Integer, Integer>();
    static {
        THEME_HASHTABLE.put(ThemeManager.DARK, R.id.dark);
        THEME_HASHTABLE.put(ThemeManager.LIGHT, R.id.light);
        THEME_HASHTABLE.put(ThemeManager.MIXED, R.id.mixed);
    }

    public DemoThemePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoThemePicker(Context context) {
        this(context, null);
    }

}
