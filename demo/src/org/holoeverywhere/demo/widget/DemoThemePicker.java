
package org.holoeverywhere.demo.widget;

import java.util.Hashtable;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;

import android.content.Context;
import android.content.res.TypedArray;
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
            if (ThemeManager.getDefaultTheme() == theme) {
                return;
            }
            ThemeManager.setDefaultTheme(theme);
            if (mActivity != null) {
                ThemeManager.restart(mActivity, false);
            }
        }
    }

    private static final Hashtable<Integer, Integer> THEME_HASHTABLE = new Hashtable<Integer, Integer>();

    static {
        THEME_HASHTABLE.put(ThemeManager.DARK, R.id.dark);
        THEME_HASHTABLE.put(ThemeManager.LIGHT, R.id.light);
        THEME_HASHTABLE.put(ThemeManager.MIXED, R.id.mixed);
    }

    private Activity mActivity;

    public DemoThemePicker(Context context) {
        this(context, null);
    }

    public DemoThemePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoThemePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int layout;
        TypedArray a = context.obtainStyledAttributes(attrs, new
                int[] {
                    android.R.attr.orientation
                }, defStyleAttr, 0);
        boolean horizontal = a.getInt(0, 0) == LinearLayout.HORIZONTAL;
        a.recycle();
        if (horizontal) {
            layout = R.layout.theme_picker_horizontal;
        } else {
            layout = R.layout.theme_picker_vertical;
        }
        addView(LayoutInflater.inflate(context, layout, this, false));
        findViewById(R.id.dark).setOnClickListener(new
                ThemeChangeListener(ThemeManager.DARK));
        findViewById(R.id.light).setOnClickListener(new
                ThemeChangeListener(ThemeManager.LIGHT));
        findViewById(R.id.mixed).setOnClickListener(new
                ThemeChangeListener(ThemeManager.MIXED));
    }

    public void setActivity(Activity activity) {
        if (mActivity != null) {
            return;
        }
        mActivity = activity;
        ((DemoListRowView) findViewById(THEME_HASHTABLE.get(ThemeManager.getTheme(activity)
                & ThemeManager.COLOR_SCHEME_MASK))).setSelectionHandlerVisiblity(true);
    }
}
