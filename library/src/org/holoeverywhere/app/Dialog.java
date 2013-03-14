
package org.holoeverywhere.app;

import static android.view.View.MeasureSpec.EXACTLY;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder;
import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;
import com.actionbarsherlock.internal.view.menu.ContextMenuItemWrapper;
import com.actionbarsherlock.internal.view.menu.ContextMenuListener;
import com.actionbarsherlock.internal.view.menu.ContextMenuWrapper;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public class Dialog extends android.app.Dialog implements ContextMenuListener {
    private static final class DialogDecorView extends ContextMenuDecorView {
        final TypedValue mMinWidthMajor = new TypedValue();

        final TypedValue mMinWidthMinor = new TypedValue();

        public DialogDecorView(Context context, View view,
                android.view.ViewGroup.LayoutParams params, ContextMenuListener listener) {
            super(context, view, params, listener);
            TypedArray a = context.obtainStyledAttributes(new int[] {
                    R.attr.windowMinWidthMajor, R.attr.windowMinWidthMinor
            });
            a.getValue(0, mMinWidthMajor);
            a.getValue(1, mMinWidthMinor);
            a.recycle();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = getMeasuredWidth();
            boolean measure = false;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);
            final TypedValue tv = isPortrait ? mMinWidthMinor : mMinWidthMajor;
            if (tv.type != TypedValue.TYPE_NULL) {
                final int min;
                if (tv.type == TypedValue.TYPE_DIMENSION) {
                    min = (int) tv.getDimension(metrics);
                } else if (tv.type == TypedValue.TYPE_FRACTION) {
                    min = (int) tv.getFraction(metrics.widthPixels, metrics.widthPixels);
                } else {
                    min = 0;
                }
                if (width < min) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, EXACTLY);
                    measure = true;
                }
            }
            if (measure) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private static final int checkTheme(Context context, int theme) {
        if (theme >= 0x01000000) {
            return theme;
        }
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.dialogTheme, value, true);
        if (value.resourceId > 0) {
            return value.resourceId;
        }
        return R.style.Holo_Theme_Dialog;
    }

    public Dialog(Context context) {
        this(context, 0);
    }

    public Dialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    public Dialog(Context context, int theme) {
        super(context, checkTheme(context, theme));
        setCancelable(true);
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        getWindow().addContentView(prepareDecorView(view, params), params);
    }

    @Override
    public void createContextMenu(ContextMenuBuilder contextMenuBuilder,
            View view, ContextMenuInfo menuInfo, ContextMenuListener listener) {
        listener.onCreateContextMenu(contextMenuBuilder, view, menuInfo);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    @Override
    public final boolean onContextItemSelected(android.view.MenuItem item) {
        return onContextItemSelected(new ContextMenuItemWrapper(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item instanceof ContextMenuItemWrapper) {
            return super.onContextItemSelected(((ContextMenuItemWrapper) item)
                    .unwrap());
        }
        return false;
    }

    @Override
    public void onContextMenuClosed(ContextMenu menu) {
        if (menu instanceof ContextMenuWrapper) {
            super.onContextMenuClosed(((ContextMenuWrapper) menu).unwrap());
        }
    }

    @Override
    public final void onContextMenuClosed(Menu menu) {
        if (menu instanceof android.view.ContextMenu) {
            onContextMenuClosed(new ContextMenuWrapper(
                    (android.view.ContextMenu) menu));
        } else {
            super.onContextMenuClosed(menu);
        }
    }

    @Override
    public final void onCreateContextMenu(android.view.ContextMenu menu,
            View view, ContextMenuInfo menuInfo) {
        onCreateContextMenu(new ContextMenuWrapper(menu), view, menuInfo);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        if (menu instanceof ContextMenuWrapper) {
            super.onCreateContextMenu(((ContextMenuWrapper) menu).unwrap(),
                    view, menuInfo);
        }
    }

    public View prepareDecorView(View v) {
        return prepareDecorView(v, null);
    }

    public View prepareDecorView(View v, ViewGroup.LayoutParams params) {
        return new DialogDecorView(getContext(), v, params, this);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        setCanceledOnTouchOutside(flag);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().makeDecorView(layoutResID, this));
    }

    @Override
    public void setContentView(View view) {
        setContentView(view,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        getWindow().setContentView(prepareDecorView(view, params), params);
    }
}
