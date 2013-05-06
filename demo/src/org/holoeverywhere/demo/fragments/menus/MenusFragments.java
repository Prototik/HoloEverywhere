
package org.holoeverywhere.demo.fragments.menus;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.widget.PopupMenu;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class MenusFragments extends OtherFragment {
    public static class MenuHelper implements Parcelable {
        public static final Creator<MenuHelper> CREATOR = new Creator<MenuHelper>() {
            @Override
            public MenuHelper createFromParcel(Parcel source) {
                return new MenuHelper(source);
            }

            @Override
            public MenuHelper[] newArray(int size) {
                return new MenuHelper[size];
            }
        };

        private SparseBooleanArray mCheckboxsState = new SparseBooleanArray(CHECKBOXS.length);
        private MenusFragments mFragment;
        private final OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean result = false;
                final int id = menuItem.getItemId();
                for (int i : RADIOS) {
                    if (i == id) {
                        mRadioState = id;
                        result = true;
                    }
                }
                for (int i : CHECKBOXS) {
                    if (i == id) {
                        mCheckboxsState.put(id, !mCheckboxsState.get(id, false));
                        result = true;
                    }
                }
                if (result && mFragment != null) {
                    mFragment.getSupportActivity().supportInvalidateOptionsMenu();
                }
                return result;
            }
        };
        private int mRadioState = RADIOS[0];

        public MenuHelper() {
        }

        protected MenuHelper(Parcel source) {
            mCheckboxsState = source.readSparseBooleanArray();
            mRadioState = source.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public void makeMenu(Menu menu, MenuInflater inflater) {
            menu.clear();
            inflater.inflate(R.menu.menu, menu);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setOnMenuItemClickListener(mOnMenuItemClickListener);
            }
            menu.findItem(mRadioState).setChecked(true);
            for (int i : CHECKBOXS) {
                menu.findItem(i).setChecked(mCheckboxsState.get(i, false));
            }
        }

        public void setFragment(MenusFragments fragment) {
            mFragment = fragment;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSparseBooleanArray(mCheckboxsState);
            dest.writeInt(mRadioState);
        }
    }

    private static final int[] CHECKBOXS = {
            R.id.item4, R.id.item5
    };
    private static final String KEY_MENU_STATE = "menuState";
    private static final int[] RADIOS = {
            R.id.item1, R.id.item2
    };
    private OnOtherItemClickListener mContextMenuListener = new OnOtherItemClickListener() {
        @Override
        public void onClick(OtherItem otherItem) {
            final View view = otherItem.lastView;
            registerForContextMenu(view);
            openContextMenu(view);
            unregisterForContextMenu(view);
        }
    };

    private MenuHelper mDemoMenuHelper;

    @Override
    protected CharSequence getTitle() {
        return "Menus";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDemoMenuHelper = savedInstanceState.getParcelable(KEY_MENU_STATE);
        }
        if (mDemoMenuHelper == null) {
            mDemoMenuHelper = new MenuHelper();
        }
        mDemoMenuHelper.setFragment(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        mDemoMenuHelper.makeMenu(menu, getMenuInflater());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mDemoMenuHelper.makeMenu(menu, inflater);
    }

    @Override
    protected void onHandleData() {
        addItem("PopupMenu", new OnOtherItemClickListener() {
            @Override
            public void onClick(OtherItem otherItem) {
                PopupMenu menu = new PopupMenu(getSupportActivity(), otherItem.lastView);
                mDemoMenuHelper.makeMenu(menu.getMenu(), getMenuInflater());
                menu.show();
            }
        });
        addItem("ContextMenu", mContextMenuListener);
        addItemWithLongClick("ContextMenu (long click)", mContextMenuListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MENU_STATE, mDemoMenuHelper);
    }
}
