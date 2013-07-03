
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.fragments.dialogs.DialogsFragment;
import org.holoeverywhere.demo.fragments.lists.ListsFragment;
import org.holoeverywhere.demo.fragments.menus.MenusFragments;
import org.holoeverywhere.demo.fragments.pickers.PickersFragment;
import org.holoeverywhere.demo.fragments.tabber.TabsFragment;
import org.holoeverywhere.demo.widget.DemoAdapter;
import org.holoeverywhere.demo.widget.DemoItem;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OtherFragment extends ListFragment {
    private final class ActivityListener implements OnOtherItemClickListener {
        private Intent mIntent;

        public ActivityListener(Class<? extends Activity> clazz) {
            this(new Intent(getSupportActivity(), clazz));
        }

        public ActivityListener(Intent intent) {
            mIntent = intent;
        }

        @Override
        public void onClick(OtherItem otherItem) {
            startActivity(mIntent);
        }
    }

    private final class FragmentListener implements OnOtherItemClickListener {
        private Class<? extends Fragment> mClass;

        public FragmentListener(Class<? extends Fragment> clazz) {
            mClass = clazz;
        }

        @Override
        public void onClick(OtherItem otherItem) {
            Fragment fragment = Fragment.instantiate(mClass);
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).show(getSupportActivity());
            } else {
                ((DemoActivity) getSupportActivity()).addonSlider()
                        .obtainSliderMenu().replaceFragment(fragment);
            }
        }
    }

    public static interface OnOtherItemClickListener {
        public void onClick(OtherItem otherItem);
    }

    private static final class OtherAdapter extends DemoAdapter {
        public OtherAdapter(Context context) {
            super(context);
        }
    }

    public static final class OtherItem extends DemoItem {
        public OnOtherItemClickListener listener;
        private boolean processLongClick = false;

        @Override
        public void onClick() {
            if (listener != null && !longClickable) {
                listener.onClick(this);
            }
        }

        @Override
        public boolean onLongClick() {
            if (listener != null && longClickable && !processLongClick) {
                processLongClick = true;
                listener.onClick(this);
                processLongClick = false;
                return true;
            }
            return super.onLongClick();
        }
    }

    private OtherAdapter mAdapter;

    public void addItem(CharSequence label, Class<? extends Fragment> clazz) {
        addItem(label, new FragmentListener(clazz));
    }

    public void addItem(CharSequence label, Intent activityIntent) {
        addItem(label, new ActivityListener(activityIntent));
    }

    public void addItem(CharSequence label, OnOtherItemClickListener listener) {
        addItem(label, listener, false);
    }

    private void addItem(CharSequence label, OnOtherItemClickListener listener,
            boolean longClickable) {
        OtherItem item = new OtherItem();
        item.label = label;
        item.listener = listener;
        item.longClickable = longClickable;
        addItem(item);
    }

    public void addItem(DemoItem item) {
        mAdapter.add(item);
    }

    public void addItemActivity(CharSequence label, Class<? extends Activity> clazz) {
        addItem(label, new ActivityListener(clazz));
    }

    public void addItemWithLongClick(CharSequence label, OnOtherItemClickListener listener) {
        addItem(label, listener, true);
    }

    protected CharSequence getTitle() {
        return "Other";
    }

    protected void onHandleData() {
        addItem("Lists", ListsFragment.class);
        addItem("Dialogs", DialogsFragment.class);
        addItem("Pickers", PickersFragment.class);
        addItem("Menus", MenusFragments.class);
        addItem("Font Palette", FontPalette.class);
        addItem("Calendar", CalendarFragment.class);
        addItem("Addon: Tabber", TabsFragment.class);
    }

    protected void onPrepareListView(ListView list) {

    }

    @Override
    public void onResume() {
        super.onResume();
        final CharSequence title = getTitle();
        if (title != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new OtherAdapter(getSupportActivity());
        onHandleData();
        boolean handleLongClick = false;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mAdapter.getItem(i).longClickable) {
                handleLongClick = true;
                break;
            }
        }
        ListView list = getListView();
        onPrepareListView(list);
        setListAdapter(mAdapter);
        list.setOnItemClickListener(mAdapter);
        if (handleLongClick) {
            list.setOnItemLongClickListener(mAdapter);
        }
    }
}
