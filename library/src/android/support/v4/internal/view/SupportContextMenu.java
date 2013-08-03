
package android.support.v4.internal.view;

import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.View;

public interface SupportContextMenu extends SupportMenu, ContextMenu {
    public void clearHeader();

    public SupportContextMenu setHeaderIcon(Drawable icon);

    public SupportContextMenu setHeaderIcon(int iconRes);

    public SupportContextMenu setHeaderTitle(CharSequence title);

    public SupportContextMenu setHeaderTitle(int titleRes);

    public SupportContextMenu setHeaderView(View view);
}
