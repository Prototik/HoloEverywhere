
package android.support.v7.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportContextMenu;
import android.view.ContextMenu;
import android.view.View;

public class ContextMenuWrapperICS extends MenuWrapperICS implements SupportContextMenu {
    private ContextMenu nativeMenu;

    public ContextMenuWrapperICS(ContextMenu nativeMenu) {
        super(nativeMenu);
        this.nativeMenu = nativeMenu;
    }

    @Override
    public void clearHeader() {
        nativeMenu.clearHeader();
    }

    @Override
    public SupportContextMenu setHeaderIcon(Drawable icon) {
        nativeMenu.setHeaderIcon(icon);
        return this;
    }

    @Override
    public SupportContextMenu setHeaderIcon(int iconRes) {
        nativeMenu.setHeaderIcon(iconRes);
        return this;
    }

    @Override
    public SupportContextMenu setHeaderTitle(CharSequence title) {
        nativeMenu.setHeaderTitle(title);
        return this;
    }

    @Override
    public SupportContextMenu setHeaderTitle(int titleRes) {
        nativeMenu.setHeaderTitle(titleRes);
        return this;
    }

    @Override
    public SupportContextMenu setHeaderView(View view) {
        nativeMenu.setHeaderView(view);
        return this;
    }

    @Override
    public ContextMenu getWrappedObject() {
        return nativeMenu;
    }
}
