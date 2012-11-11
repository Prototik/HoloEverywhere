
package org.holoeverywhere.demo.widget;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

public class DemoNavigationItem extends LinearLayout {
    private final TextView label;
    private final View selectionHandler;

    public DemoNavigationItem(Context context) {
        this(context, null);
    }

    public DemoNavigationItem(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.demoNavigationItemStyle);
    }

    @SuppressWarnings("deprecation")
    public DemoNavigationItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.inflate(context, R.layout.demo_navigation_item, this, true);
        selectionHandler = findViewById(R.id.selectionHandler);
        label = (TextView) findViewById(android.R.id.text1);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DemoNavigationItem,
                defStyleAttr, R.style.Holo_Demo_NavigationItem);
        if (a.hasValue(R.styleable.DemoNavigationItem_android_text)) {
            setLabel(a.getText(R.styleable.DemoNavigationItem_android_text));
        }
        if (a.hasValue(R.styleable.DemoNavigationItem_android_color)) {
            setSelectionHandlerColor(a.getColor(R.styleable.DemoNavigationItem_android_color, 0));
        }
        if (a.hasValue(R.styleable.DemoNavigationItem_android_background)) {
            setBackgroundDrawable(a.getDrawable(R.styleable.DemoNavigationItem_android_background));
        }
        a.recycle();
    }

    public void setLabel(CharSequence label) {
        this.label.setText(label);
    }

    public void setLabel(int resId) {
        setLabel(getResources().getText(resId));
    }

    public void setSelectionHandlerColor(int color) {
        selectionHandler.setBackgroundColor(color);
    }

    public void setSelectionHandlerColorResource(int resId) {
        setSelectionHandlerColor(getResources().getColor(resId));
    }

    public void setSelectionHandlerVisiblity(int visiblity) {
        selectionHandler.setVisibility(visiblity);
    }
}
