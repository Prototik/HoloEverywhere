
package org.holoeverywhere.demo.fragments.menus;

import org.holoeverywhere.demo.DemoMenuHelper;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.widget.PopupMenu;

import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.view.ContextMenu;

public class MenusFragments extends OtherFragment {
    private boolean allowCreateContextMenu = false;

    @Override
    protected CharSequence getTitle() {
        return "Menus";
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (allowCreateContextMenu) {
            DemoMenuHelper.makeMenu(menu, getMenuInflater());
        } else {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    protected void onHandleData() {
        addItem("PopupMenu", new OnOtherItemClickListener() {
            @Override
            public void onClick(OtherItem otherItem) {
                PopupMenu menu = new PopupMenu(getSupportActivity(), otherItem.lastView);
                DemoMenuHelper.makeMenu(menu.getMenu(), getMenuInflater());
                menu.show();
            }
        });
        addItemWithLongClick("ContextMenu (press and hold)", new OnOtherItemClickListener() {
            @Override
            public void onClick(OtherItem otherItem) {
                final View view = otherItem.lastView;
                allowCreateContextMenu = true;
                registerForContextMenu(view);
                openContextMenu(view);
                unregisterForContextMenu(view);
                allowCreateContextMenu = false;
            }
        });
    }
}
