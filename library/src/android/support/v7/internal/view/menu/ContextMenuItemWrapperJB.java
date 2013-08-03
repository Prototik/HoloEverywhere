
package android.support.v7.internal.view.menu;

import android.view.MenuItem;

public class ContextMenuItemWrapperJB extends MenuItemWrapperJB {
    private final MenuItem nativeItem;

    public ContextMenuItemWrapperJB(MenuItem nativeItem) {
        super(nativeItem);
        this.nativeItem = nativeItem;
    }
}
