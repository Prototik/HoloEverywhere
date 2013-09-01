
package android.support.v7.internal.view.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.internal.view.SupportContextMenu;
import android.util.EventLog;
import android.view.View;

import java.lang.reflect.Method;

public class ContextMenuBuilder extends MenuBuilder implements SupportContextMenu {
    private ContextMenuInfo mContextMenuInfo;
    private ContextMenuListener mListener;

    public ContextMenuBuilder(Context context, ContextMenuListener listener) {
        super(context);
        setContextMenuListener(listener);
    }

    public ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    protected ContextMenuInfo getContextMenuInfo(View view) {
        if (view instanceof ContextMenuInfoGetter) {
            return ((ContextMenuInfoGetter) view).getContextMenuInfo();
        }
        ContextMenuInfo menuInfo = null;
        try {
            Method method = View.class.getDeclaredMethod("getContextMenuInfo");
            method.setAccessible(true);
            menuInfo = (ContextMenuInfo) method.invoke(view);
        } catch (Exception e) {
        }
        return menuInfo;
    }

    public ContextMenuListener getContextMenuListener() {
        return mListener;
    }

    public void setContextMenuListener(ContextMenuListener listener) {
        mListener = listener;
    }

    @Override
    public SupportContextMenu setHeaderIcon(Drawable icon) {
        return (SupportContextMenu) super.setHeaderIconInt(icon);
    }

    @Override
    public SupportContextMenu setHeaderIcon(int iconRes) {
        return (SupportContextMenu) super.setHeaderIconInt(iconRes);
    }

    @Override
    public SupportContextMenu setHeaderTitle(CharSequence title) {
        return (SupportContextMenu) super.setHeaderTitleInt(title);
    }

    @Override
    public SupportContextMenu setHeaderTitle(int titleRes) {
        return (SupportContextMenu) super.setHeaderTitleInt(titleRes);
    }

    @Override
    public SupportContextMenu setHeaderView(View view) {
        return (SupportContextMenu) super.setHeaderViewInt(view);
    }

    @SuppressLint("NewApi")
    public MenuDialogHelper show(View originalView, IBinder token) {
        if (mListener == null) {
            throw new IllegalStateException(
                    "Cannot show context menu without reference on ContextMenuListener");
        }
        mContextMenuInfo = getContextMenuInfo(originalView);
        mListener.onCreateContextMenu(this, originalView, mContextMenuInfo);
        if (getVisibleItems().size() > 0) {
            if (VERSION.SDK_INT >= 8) {
                EventLog.writeEvent(50001, 1);
            }
            MenuDialogHelper helper = new MenuDialogHelper(this);
            helper.show(token);
            return helper;
        }
        return null;
    }

    public static interface ContextMenuInfoGetter {
        public ContextMenuInfo getContextMenuInfo();
    }
}
