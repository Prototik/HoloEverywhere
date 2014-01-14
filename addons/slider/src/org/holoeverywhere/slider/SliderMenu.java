
package org.holoeverywhere.slider;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.addon.IAddonThemes;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.drawable.DrawableCompat;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SliderMenu implements OnBackStackChangedListener {
    public static final int[] BLUE = new int[]{
            R.color.holo_blue_dark, R.color.holo_blue_light
    };
    public static final int[] GREEN = new int[]{
            R.color.holo_green_dark, R.color.holo_green_light
    };
    public static final int[] ORANGE = new int[]{
            R.color.holo_orange_dark, R.color.holo_orange_light
    };
    public static final int[] PURPLE = new int[]{
            R.color.holo_purple_dark, R.color.holo_purple_light
    };
    public static final int[] RED = new int[]{
            R.color.holo_red_dark, R.color.holo_red_light
    };
    public static final int THEME_FLAG;
    private static final IAddonThemes sThemes;

    static {
        sThemes = new IAddonThemes();
        THEME_FLAG = sThemes.getThemeFlag();
        map(R.style.Holo_Theme_Slider, R.style.Holo_Theme_Slider_Light,
                R.style.Holo_Theme_Slider_Light_DarkActionBar);
    }

    private static final String KEY_CURRENT_PAGE = ":slider:currentPage";
    private final AddonSliderA mAddon;
    private final FragmentManager mFragmentManager;
    private final List<SliderItem> mItems;
    private ActionBar mActionBar;
    private SliderMenuAdapter mAdapter;
    private int mCurrentPage = -1;
    private int mFuturePosition = -1;
    private boolean mHandleHomeKey;
    private boolean mIgnoreBackStack = false;
    private int mInitialPage = 0;
    private boolean mInverseTextColorWhenSelected = false;
    private SelectionBehavior mSelectionBehavior = SelectionBehavior.Default;
    private NavigateUpBehavior mNavigateUpBehavior = NavigateUpBehavior.ShowMenu;

    public SliderMenu(AddonSliderA addon) {
        mAddon = addon;
        mActionBar = addon.get().getSupportActionBar();
        mFragmentManager = mAddon.get().getSupportFragmentManager();
        mItems = new ArrayList<SliderItem>();
    }

    public static int getThemeForType(int type) {
        return sThemes.getTheme(type);
    }

    /**
     * Remap all SliderMenu themes
     */
    public static void map(int theme) {
        map(theme, theme, theme);
    }

    /**
     * Remap SliderMenu themes, splited by dark and light color scheme. For
     * mixed color scheme will be using light theme
     */
    public static void map(int darkTheme, int lightTheme) {
        map(darkTheme, lightTheme, lightTheme);
    }

    /**
     * Remap SliderMenu themes, splited by color scheme
     */
    public static void map(int darkTheme, int lightTheme, int mixedTheme) {
        sThemes.map(darkTheme, lightTheme, mixedTheme);
    }

    private static void setTextAppearance(TextView textView, int resid) {
        if (resid != 0 && textView != null) {
            textView.setTextAppearance(textView.getContext(), resid);
        }
    }

    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments, int[] colors) {
        SliderItem item = new SliderItem();
        item.setLabel(label);
        item.setFragmentClass(fragmentClass);
        item.setFragmentArguments(fragmentArguments);
        if (colors != null && colors.length >= 2) {
            final Resources res = mAddon.get().getResources();
            item.setBackgroundColor(res.getColor(colors[0]));
            item.setSelectionHandlerColor(res.getColor(colors[1]));
        }
        return add(item);
    }

    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass,
                          int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    public SliderItem add(int label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    public SliderItem add(int label, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    public SliderItem add(int label, Class<? extends Fragment> fragmentClass,
                          Bundle fragmentArguments, int[] colors) {
        return add(mAddon.get().getText(label), fragmentClass, fragmentArguments, colors);
    }

    public SliderItem add(int label, Class<? extends Fragment> fragmentClass,
                          int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    public SliderItem add(SliderItem item) {
        if (item.mSliderMenu != null) {
            throw new IllegalArgumentException("Item already has a parent: "
                    + item + " (" + item.mSliderMenu + ")");
        }
        item.mSliderMenu = this;
        mItems.add(item);
        notifyChanged();
        return item;
    }

    public SliderItem add(SliderItem item, int position) {
        if (item.mSliderMenu != null) {
            throw new IllegalArgumentException("Item already has a parent: "
                    + item + " (" + item.mSliderMenu + ")");
        }
        item.mSliderMenu = this;
        mItems.add(position, item);
        notifyChanged();
        return item;
    }

    public void bind(Fragment listFragment) {
        bind(listFragment, null);
    }

    public void bind(Fragment listFragment, Context context) {
        bind((ListView) listFragment.getView().findViewById(android.R.id.list), context);
    }

    public void bind(ListView listView) {
        bind(listView, null);
    }

    public void bind(ListView listView, Context context) {
        if (mAdapter != null) {
            throw new IllegalStateException("No more than one view allowed");
        }
        mAdapter = new SliderMenuAdapter(context == null ? listView.getContext() : context);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mAdapter);
        if (mFuturePosition >= 0) {
            setCurrentPage(mFuturePosition, true);
            mFuturePosition = -1;
        }
    }

    public void bindOnLeftPanel() {
        bindOnLeftPanel(null);
    }

    public void bindOnLeftPanel(Context context) {
        tryToBind(mAddon.getLeftView(), context);
    }

    public void bindOnRightPanel() {
        bindOnRightPanel(null);
    }

    public void bindOnRightPanel(Context context) {
        tryToBind(mAddon.getRightView(), context);
    }

    @SuppressWarnings("deprecation")
    public View bindView(SliderItem item, View view, boolean selected,
                         int defaultTextAppearance, int defaultTextAppearanceInverse) {
        TextView labelView = (TextView) view.findViewById(android.R.id.text1);
        if (labelView != null) {
            labelView.setText(item.mLabel);
        }
        final int textAppereance = item.mTextAppereance != 0 ?
                item.mTextAppereance : defaultTextAppearance;
        final int textAppereanceInverse = item.mTextAppereanceInverse != 0 ?
                item.mTextAppereanceInverse : defaultTextAppearanceInverse;
        setTextAppearance(labelView,
                mInverseTextColorWhenSelected ? selected ? textAppereanceInverse
                        : textAppereance : textAppereance);
        ImageView iconView = (ImageView) view.findViewById(android.R.id.icon1);
        iconView.setImageDrawable(item.mIcon);
        iconView.setVisibility(item.mIcon == null ? View.GONE : View.VISIBLE);
        if (mSelectionBehavior == null) {
            return view;
        }
        View selectionHandlerView = view.findViewById(R.id.selectionHandler);
        switch (mSelectionBehavior) {
            case Default:
                if (selected) {
                    view.setBackgroundColor(item.mBackgroundColor);
                    if (selectionHandlerView != null) {
                        selectionHandlerView.setBackgroundColor(item.mSelectionHandlerColor);
                    }
                } else {
                    view.setBackgroundDrawable(null);
                    if (selectionHandlerView != null) {
                        selectionHandlerView.setBackgroundDrawable(null);
                    }
                }
                break;
            case BackgroundWhenSelected:
                if (selectionHandlerView != null) {
                    selectionHandlerView.setBackgroundColor(item.mSelectionHandlerColor);
                }
                if (selected) {
                    view.setBackgroundColor(item.mBackgroundColor);
                } else {
                    view.setBackgroundDrawable(null);
                }
                break;
            case OnlyBackground:
                if (selected) {
                    view.setBackgroundColor(item.mBackgroundColor);
                } else {
                    view.setBackgroundDrawable(null);
                }
                break;
            case OnlyHandler:
                if (selectionHandlerView != null) {
                    if (selected) {
                        selectionHandlerView.setBackgroundColor(item.mSelectionHandlerColor);
                    } else {
                        selectionHandlerView.setBackgroundDrawable(null);
                    }
                }
                break;
        }
        return view;
    }

    protected void changePage(int position) {
        changePage(position, mItems.get(position));
    }

    private void changePage(int position, SliderItem item) {
        mIgnoreBackStack = true;
        if (mCurrentPage >= 0) {
            final SliderItem lastItem = mAdapter.getItem(mCurrentPage);
            final WeakReference<Fragment> ref = lastItem.mLastFragment;
            final Fragment fragment = ref == null ? null : ref.get();
            if (fragment != null && fragment.isAdded()) {
                if (!fragment.isDetached()) {
                    mFragmentManager.beginTransaction().detach(fragment).commit();
                    mFragmentManager.executePendingTransactions();
                }
                if (lastItem.mSaveState) {
                    lastItem.mSavedState = mFragmentManager.saveFragmentInstanceState(fragment);
                }
            }
        }
        mCurrentPage = position;
        mAdapter.notifyDataSetInvalidated();
        while (mFragmentManager.popBackStackImmediate()) {
        }
        final Fragment fragment = Fragment.instantiate(item.mFragmentClass);
        if (item.mSavedState != null) {
            fragment.setInitialSavedState(item.mSavedState);
        }
        if (item.mFragmentArguments != null) {
            item.mFragmentArguments.setClassLoader(HoloEverywhere.class.getClassLoader());
            fragment.setArguments(item.mFragmentArguments);
        }
        item.mLastFragment = new WeakReference<Fragment>(fragment);
        final String tag;
        if (item.mTag != null) {
            tag = item.mTag;
        } else {
            tag = "fragment-" + fragment.hashCode();
        }
        clearBackStack();
        replaceFragment(mFragmentManager, fragment, tag, false);
        mIgnoreBackStack = false;
    }

    private void clearBackStack() {
        while (mFragmentManager.popBackStackImmediate()) {
            ;
        }
    }

    public int getInitialPage() {
        return mInitialPage;
    }

    public void setInitialPage(int initialPage) {
        mInitialPage = initialPage;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int position) {
        setCurrentPage(position, true);
    }

    public SelectionBehavior getSelectionBehavior() {
        return mSelectionBehavior;
    }

    public void setSelectionBehavior(SelectionBehavior selectionBehavior) {
        mSelectionBehavior = selectionBehavior;
    }

    public int indexOfItem(SliderItem item) {
        return mItems.indexOf(item);
    }

    public void invalidate() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetInvalidated();
        }
    }

    public boolean isHandleHomeKey() {
        return mHandleHomeKey;
    }

    public void setHandleHomeKey(boolean handleHomeKey) {
        mHandleHomeKey = handleHomeKey;
    }

    public boolean isInverseTextColorWhenSelected() {
        return mInverseTextColorWhenSelected;
    }

    public void setInverseTextColorWhenSelected(boolean inverseTextColorWhenSelected) {
        mInverseTextColorWhenSelected = inverseTextColorWhenSelected;
    }

    public void makeDefaultMenu() {
        makeDefaultMenu(false);
    }

    public void makeDefaultMenu(boolean useActionBarStyle) {
        makeDefaultMenu(mAddon.obtainMenuContext(useActionBarStyle));
    }

    public void makeDefaultMenu(Context context) {
        setInverseTextColorWhenSelected(ThemeManager.getThemeType(mAddon.get()) != ThemeManager.LIGHT);
        Fragment menuFragment = (Fragment) mFragmentManager.findFragmentById(R.id.leftView);
        if (menuFragment == null) {
            menuFragment = (Fragment) mFragmentManager.findFragmentById(R.id.rightView);
        }
        if (menuFragment == null) {
            throw new IllegalStateException("Couldn't find Fragment for menu");
        }
        bind(menuFragment, context);
    }

    public NavigateUpBehavior getNavigateUpBehavior() {
        return mNavigateUpBehavior;
    }

    public void setNavigateUpBehavior(NavigateUpBehavior behavior) {
        mNavigateUpBehavior = behavior;
    }

    private void notifyChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackStackChanged() {
        if (mHandleHomeKey) {
            mActionBar.setDisplayHomeAsUpEnabled(mAddon.isAddonEnabled() || mFragmentManager.getBackStackEntryCount() > 0);
        }
        if (mIgnoreBackStack || mCurrentPage < 0 || mCurrentPage >= mItems.size()) {
            return;
        }
        SliderItem item = mItems.get(mCurrentPage);
        WeakReference<Fragment> ref = item.mLastFragment;
        Fragment fragment = ref == null ? null : ref.get();
        if (fragment == null || !item.mSaveState) {
            return;
        }
        if (fragment.isAdded() && fragment.isDetached() && !fragment.isVisible()) {
            item.mSavedState = mFragmentManager.saveFragmentInstanceState(fragment);
        }
    }

    public boolean onNavigateUp() {
        if (mHandleHomeKey) {
            if (mNavigateUpBehavior == NavigateUpBehavior.ShowMenu) {
                mAddon.toggle();
            } else if (mNavigateUpBehavior == NavigateUpBehavior.PopUpFragment) {
                if (mAddon.isAddonEnabled() && mFragmentManager.getBackStackEntryCount() == 0) {
                    mAddon.toggle();
                } else {
                    mAddon.get().onBackPressed();
                }
            }
            return true;
        }
        return false;
    }

    public void onPostCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE, 0);
        }
        mIgnoreBackStack = true;
        mAddon.get().getSupportFragmentManager().addOnBackStackChangedListener(this);
        onBackStackChanged();
        mIgnoreBackStack = false;
    }

    public void onResume() {
        if (mCurrentPage < 0 && mItems.size() > 0) {
            setCurrentPage(mInitialPage, true);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    public void remove(int position) {
        mItems.remove(position).mSliderMenu = null;
        notifyChanged();
    }

    public void remove(SliderItem item) {
        if (mItems.remove(item)) {
            item.mSliderMenu = null;
        }
        notifyChanged();
    }

    public void replaceFragment(Fragment newFragment) {
        replaceFragment(newFragment, null, true);
    }

    public void replaceFragment(Fragment newFragment, String tag, boolean addToBackStack) {
        if (!addToBackStack) {
            clearBackStack();
        }
        replaceFragment(mFragmentManager, newFragment, tag, addToBackStack);
    }

    private void replaceFragment(FragmentManager fm, Fragment fragment, String tag,
                                 boolean addToBackStack) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentView, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            ft.addToBackStack(tag);
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }

    private void setCurrentPage(int position, boolean force) {
        if (mAdapter == null) {
            mFuturePosition = position;
        }
        if (position < 0 || position >= mItems.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (force || mCurrentPage != position
                || mAddon.get().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            changePage(position, mAdapter.getItem(position));
            mCurrentPage = position;
        }
        if (mAddon.isAddonEnabled()) {
            mAddon.openContentViewDelayed(40);
        }
    }

    private void tryToBind(View view, Context context) {
        if (view instanceof ListView) {
            bind((ListView) view, context);
            return;
        }
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        if (listView != null) {
            bind(listView, context);
            return;
        }
        throw new IllegalStateException("Couldn't find ListView on panel");
    }

    public static enum SelectionBehavior {
        BackgroundWhenSelected, Default, OnlyBackground, OnlyHandler, Disabled;
    }

    public static enum NavigateUpBehavior {
        PopUpFragment, ShowMenu
    }

    public static class SliderMenuFragment extends Fragment {
        protected Context mMenuContext;

        private AddonSliderA addonSlider() {
            return getSupportActivity().addon(AddonSlider.class);
        }

        @Override
        public LayoutInflater getLayoutInflater() {
            if (mMenuContext == null) {
                mMenuContext = addonSlider().getMenuContext();
            }
            if (mMenuContext == null) {
                mMenuContext = addonSlider().get();
            }
            return LayoutInflater.from(mMenuContext);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final int menuLayout = addonSlider().getMenuLayout();
            return inflater.inflate(menuLayout != 0 ? menuLayout : R.layout.slider_default_list_layout, container, false);
        }
    }

    public static class SliderItem implements Parcelable {
        public static final Creator<SliderItem> CREATOR = new Creator<SliderMenu.SliderItem>() {
            @Override
            public SliderItem createFromParcel(Parcel source) {
                try {
                    return new SliderItem(source);
                } catch (Exception e) {
                    throw new BadParcelableException(e);
                }
            }

            @Override
            public SliderItem[] newArray(int size) {
                return new SliderItem[size];
            }
        };
        private int mBackgroundColor = 0;
        private int mCustomLayout = 0;
        private Bundle mFragmentArguments;
        private Class<? extends Fragment> mFragmentClass;
        private CharSequence mLabel;
        private WeakReference<Fragment> mLastFragment;
        private Fragment.SavedState mSavedState;
        private boolean mSaveState = true;
        private int mSelectionHandlerColor = 0;
        private SliderMenu mSliderMenu;
        private String mTag;
        private int mTextAppereance = 0;
        private int mTextAppereanceInverse = 0;
        private List<SliderSubItem> mSubItems;
        private Drawable mIcon;

        public SliderItem() {
        }

        protected SliderItem(Parcel source) throws Exception {
            String classname = source.readString();
            if (classname != null) {
                mFragmentClass = (Class<? extends Fragment>) Class.forName(classname);
            }
            mSavedState = source.readParcelable(Fragment.SavedState.class.getClassLoader());
            mSaveState = source.readInt() == 1;
            mCustomLayout = source.readInt();
            mBackgroundColor = source.readInt();
            mSelectionHandlerColor = source.readInt();
            mTextAppereance = source.readInt();
            mTextAppereanceInverse = source.readInt();
            mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            mSubItems = source.createTypedArrayList(SliderSubItem.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public int getBackgroundColor() {
            return mBackgroundColor;
        }

        public SliderItem setBackgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        public int getCustomLayout() {
            return mCustomLayout;
        }

        public SliderItem setCustomLayout(int customLayout) {
            mCustomLayout = customLayout;
            return this;
        }

        public Bundle getFragmentArguments() {
            return mFragmentArguments;
        }

        public SliderItem setFragmentArguments(Bundle fragmentArguments) {
            mFragmentArguments = fragmentArguments;
            return this;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return mFragmentClass;
        }

        public SliderItem setFragmentClass(Class<? extends Fragment> fragmentClass) {
            if (mFragmentClass == fragmentClass) {
                return this;
            }
            mFragmentClass = fragmentClass;
            mSavedState = null;
            return this;
        }

        public CharSequence getLabel() {
            return mLabel;
        }

        public SliderItem setLabel(CharSequence label) {
            mLabel = label;
            invalidate();
            return this;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public SliderItem setIcon(int resId) {
            if (mSliderMenu == null) {
                throw new IllegalStateException("You cannot provide icon before adding item to SliderMenu");
            }
            return setIcon(DrawableCompat.getDrawable(mSliderMenu.mAddon.get().getResources(), resId));
        }

        public SliderItem setIcon(Drawable icon) {
            mIcon = icon;
            invalidate();
            return this;
        }

        public int getSelectionHandlerColor() {
            return mSelectionHandlerColor;
        }

        public SliderItem setSelectionHandlerColor(int selectionHandlerColor) {
            mSelectionHandlerColor = selectionHandlerColor;
            return this;
        }

        public String getTag() {
            return mTag;
        }

        public SliderItem setTag(String tag) {
            mTag = tag;
            return this;
        }

        public int getTextAppereance() {
            return mTextAppereance;
        }

        public SliderItem setTextAppereance(int textAppereance) {
            mTextAppereance = textAppereance;
            return this;
        }

        public int getTextAppereanceInverse() {
            return mTextAppereanceInverse;
        }

        public SliderItem setTextAppereanceInverse(int textAppereanceInverse) {
            mTextAppereanceInverse = textAppereanceInverse;
            return this;
        }

        private void invalidate() {
            if (mSliderMenu != null) {
                mSliderMenu.invalidate();
            }
        }

        public boolean isSaveState() {
            return mSaveState;
        }

        public SliderItem setSaveState(boolean saveState) {
            if (mSaveState == saveState) {
                return this;
            }
            mSaveState = saveState;
            if (!saveState) {
                mSavedState = null;
            }
            return this;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mFragmentClass == null ? null : mFragmentClass.getName());
            dest.writeParcelable(mSaveState ? mSavedState : null, flags);
            dest.writeInt(mSaveState ? 1 : 0);
            dest.writeInt(mCustomLayout);
            dest.writeInt(mBackgroundColor);
            dest.writeInt(mSelectionHandlerColor);
            dest.writeInt(mTextAppereance);
            dest.writeInt(mTextAppereanceInverse);
            dest.writeTypedList(mSubItems);
            TextUtils.writeToParcel(mLabel, dest, flags);
        }

    }

    public static class SliderSubItem implements Parcelable {
        public static final Parcelable.Creator<SliderSubItem> CREATOR = new Parcelable.Creator<SliderSubItem>() {
            @Override
            public SliderSubItem[] newArray(int size) {
                return new SliderSubItem[size];
            }

            @Override
            public SliderSubItem createFromParcel(Parcel source) {
                return new SliderSubItem(source);
            }
        };

        protected SliderSubItem(Parcel source) {

        }

        public SliderSubItem() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }

    private final class SliderMenuAdapter extends BaseAdapter implements OnItemClickListener {
        private final int mDefaultTextAppearance;
        private final int mDefaultTextAppearanceInverse;
        private final LayoutInflater mLayoutInflater;

        private SliderMenuAdapter(Context context) {
            context = sThemes.context(context);
            mLayoutInflater = LayoutInflater.from(context);
            TypedArray a = context.obtainStyledAttributes(R.styleable.SliderMenu);
            mDefaultTextAppearance = a.getResourceId(
                    R.styleable.SliderMenu_textAppearanceSliderItem,
                    R.style.Holo_TextAppearance_Medium);
            mDefaultTextAppearanceInverse = a.getResourceId(
                    R.styleable.SliderMenu_textAppearanceSliderItemInverse,
                    R.style.Holo_TextAppearance_Medium_Inverse);
            a.recycle();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public SliderItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).mCustomLayout != 0 ? position
                    : Adapter.IGNORE_ITEM_VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final SliderItem item = getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(item.mCustomLayout != 0 ? item.mCustomLayout
                        : R.layout.slider_menu_item, parent, false);
            }
            return bindView(item, convertView, mCurrentPage == position,
                    mDefaultTextAppearance, mDefaultTextAppearanceInverse);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setCurrentPage(position, false);
        }
    }
}
