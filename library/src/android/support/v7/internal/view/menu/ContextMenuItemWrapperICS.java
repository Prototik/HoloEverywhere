
package android.support.v7.internal.view.menu;

import android.view.MenuItem;

public class ContextMenuItemWrapperICS extends MenuItemWrapperICS {
    private final MenuItem nativeItem;

    public ContextMenuItemWrapperICS(MenuItem nativeItem) {
        super(nativeItem);
        this.nativeItem = nativeItem;
    }
}
