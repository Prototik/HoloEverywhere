
package android.support.v4.internal.view;

import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.View;

/**
 * Extension of {@link android.view.Menu} for context menus providing functionality to modify
 * the header of the context menu.
 * <p>
 * Context menus do not support item shortcuts and item icons.
 * <p>
 * To show a context menu on long click, most clients will want to call
 * {@link android.app.Activity#registerForContextMenu} and override
 * {@link android.app.Activity#onCreateContextMenu}.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about creating menus, read the
 * <a href="{@docRoot}guide/topics/ui/menus.html">Menus</a> developer guide.</p>
 * </div>
 */
public interface SupportContextMenu extends SupportMenu, ContextMenu {
    /**
     * Sets the context menu header's title to the title given in <var>titleRes</var>
     * resource identifier.
     *
     * @param titleRes The string resource identifier used for the title.
     * @return This ContextMenu so additional setters can be called.
     */
    public SupportContextMenu setHeaderTitle(int titleRes);

    /**
     * Sets the context menu header's title to the title given in <var>title</var>.
     *
     * @param title The character sequence used for the title.
     * @return This ContextMenu so additional setters can be called.
     */
    public SupportContextMenu setHeaderTitle(CharSequence title);

    /**
     * Sets the context menu header's icon to the icon given in <var>iconRes</var>
     * resource id.
     *
     * @param iconRes The resource identifier used for the icon.
     * @return This ContextMenu so additional setters can be called.
     */
    public SupportContextMenu setHeaderIcon(int iconRes);

    /**
     * Sets the context menu header's icon to the icon given in <var>icon</var>
     * {@link Drawable}.
     *
     * @param icon The {@link Drawable} used for the icon.
     * @return This ContextMenu so additional setters can be called.
     */
    public SupportContextMenu setHeaderIcon(Drawable icon);

    /**
     * Sets the header of the context menu to the {@link View} given in
     * <var>view</var>. This replaces the header title and icon (and those
     * replace this).
     *
     * @param view The {@link View} used for the header.
     * @return This ContextMenu so additional setters can be called.
     */
    public SupportContextMenu setHeaderView(View view);

    /**
     * Clears the header of the context menu.
     */
    public void clearHeader();
}
