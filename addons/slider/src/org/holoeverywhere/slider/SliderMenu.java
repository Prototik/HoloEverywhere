package org.holoeverywhere.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;

import org.holoeverywhere.HoloEverywhere;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.AddonSlider.AddonSliderA;
import org.holoeverywhere.addon.IAddonThemes;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ContextThemeWrapperPlus;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.drawable.ColorDrawable;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.ViewStubHolo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SliderMenu implements OnBackStackChangedListener, IMenuAdder<SliderItem> {
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

    /**
     * Change color of small line from left of item
     */
    public static final int FLAG_SELECTION_HANDLER = 1 << 1;

    /**
     * Change background color of item
     */
    public static final int FLAG_SELECTION_BACKGROUND = 1 << 2;

    /**
     * Allow to keep visible subitems only on group in the same time
     */
    public static final int FLAG_EXPAND_ONE_GROUP = 1 << 3;

    static final int GROUP_POSITION_SHIFT = 16;
    static final int GROUP_POSITION_MASK = 0xFFFFFFFF << GROUP_POSITION_SHIFT; // 0xFFFF0000
    static final int CHILD_POSITION_MASK = 0xFFFFFFFF >>> (32 - GROUP_POSITION_SHIFT); // 0x0000FFFF
    static final int GROUP_INVALID = 0xFFFF << GROUP_POSITION_SHIFT; // 0xFFFF0000
    static final int CHILD_INVALID = 0xFFFF; // 0x0000FFFF
    static final int ITEM_INVALID = GROUP_INVALID | CHILD_INVALID; // 0xFFFFFFFF

    static {
        sThemes = new IAddonThemes();
        THEME_FLAG = sThemes.getThemeFlag();
        mapGlobal(R.style.Holo_Theme_Slider, R.style.Holo_Theme_Slider_Light,
                R.style.Holo_Theme_Slider_Light_DarkActionBar);
    }

    private static final String KEY_CURRENT_PAGE = ":slider:currentPage";
    private static final int MENU_VIEW_ID = R.id.slider_menu;
    private final AddonSliderA mAddon;
    private final FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private IAdapter mAdapter;
    private int mFuturePosition = 0;
    private boolean mHandleHomeKey;
    private boolean mIgnoreBackStack = false;
    private int mInitialPage = 0;
    private boolean mInverseTextColorWhenSelected = false;
    private NavigateUpBehavior mNavigateUpBehavior = NavigateUpBehavior.ShowMenu;
    private ViewStubHolo mListViewStub;
    private Context mListViewStubContext;
    private int mAppearanceFlags;
    private boolean mExpandableMenu;
    private IAddonThemes mThemes;
    private OnPageChangeListener mOnPageChangeListener;
    private boolean mPageWasChanged = false;

    final List<SliderItem> mItems;
    int mCurrentPage = 0;

    public SliderMenu(AddonSliderA addon) {
        mAddon = addon;
        mActionBar = addon.get().getSupportActionBar();
        mFragmentManager = mAddon.get().getSupportFragmentManager();
        mItems = new ArrayList<SliderItem>();
        mThemes = new IAddonThemes(sThemes);

        mAppearanceFlags = FLAG_SELECTION_BACKGROUND | FLAG_SELECTION_HANDLER | FLAG_EXPAND_ONE_GROUP;
    }

    public void addAppearanceFlags(int appearanceFlags) {
        mAppearanceFlags |= appearanceFlags;
    }

    public void removeAppearanceFlags(int appearanceFlags) {
        mAppearanceFlags &= ~appearanceFlags;
    }

    public void setAppearanceFlags(int appearanceFlags) {
        mAppearanceFlags = appearanceFlags;
    }

    public int getAppearanceFlags() {
        return mAppearanceFlags;
    }

    public int getThemeForType(int type) {
        return mThemes.getTheme(type);
    }

    /**
     * Remap all SliderMenu themes
     */
    public static void mapGlobal(int theme) {
        mapGlobal(theme, theme, theme);
    }

    /**
     * Remap SliderMenu themes, splited by dark and light color scheme. For
     * mixed color scheme will be using dark theme
     */
    public static void mapGlobal(int darkTheme, int lightTheme) {
        mapGlobal(darkTheme, lightTheme, darkTheme);
    }

    /**
     * Remap SliderMenu themes, splited by color scheme
     */
    public static void mapGlobal(int darkTheme, int lightTheme, int mixedTheme) {
        sThemes.map(darkTheme, lightTheme, mixedTheme);
    }

    private static void setTextAppearance(TextView textView, int resid) {
        if (resid != 0 && textView != null) {
            textView.setTextAppearance(textView.getContext(), resid);
        }
    }

    @Override
    public SliderItem add(CharSequence label) {
        return add(label, null, null, null);
    }

    @Override
    public SliderItem add(CharSequence label, int[] colors) {
        return add(label, null, null, colors);
    }

    @Override
    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    @Override
    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    @Override
    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors) {
        return add(new SliderItem()
                        .setLabel(label)
                        .setFragmentClass(fragmentClass)
                        .setFragmentArguments(fragmentArguments)
        ).fillColors(colors);
    }

    @Override
    public SliderItem add(int label) {
        return add(label, null, null, null);
    }

    @Override
    public SliderItem add(int label, int[] colors) {
        return add(label, null, null, colors);
    }

    @Override
    public SliderItem add(CharSequence label, Class<? extends Fragment> fragmentClass, int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    @Override
    public SliderItem add(int label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    @Override
    public SliderItem add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    @Override
    public SliderItem add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors) {
        return add(mAddon.get().getText(label), fragmentClass, fragmentArguments, colors);
    }

    @Override
    public SliderItem add(int label, Class<? extends Fragment> fragmentClass, int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    @Override
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

    @Override
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
        bind(listFragment.getView().findViewById(MENU_VIEW_ID), context);
    }

    private void bind(View view, Context context) {
        if (view instanceof ViewStubHolo) {
            mListViewStub = (ViewStubHolo) view;
            mListViewStubContext = context;
            return;
        }
        while (true) {
            if (view instanceof ExpandableListView) {
                bind((ExpandableListView) view, context);
                return;
            } else if (view instanceof ListView) {
                bind((ListView) view, context);
                return;
            } else if (view != null) {
                view = view.findViewById(MENU_VIEW_ID);
            } else {
                break;
            }
        }
        throw new RuntimeException("Couldn't bind menu on view");
    }

    private boolean hasSubItems() {
        for (SliderItem item : mItems) {
            if (item.hasSubItems()) {
                return true;
            }
        }
        return false;
    }

    public void bind(ListView listView, Context context) {
        if (mAdapter != null) {
            throw new IllegalStateException("No more than one view allowed");
        }
        mExpandableMenu = false;
        SliderMenuAdapter adapter = new SliderMenuAdapter(obtainMenuContext(context == null ? listView.getContext() : context), this);
        mAdapter = adapter;
        adapter.bind(listView);
    }

    public void bind(ExpandableListView listView, Context context) {
        if (mAdapter != null) {
            throw new IllegalStateException("No more than one view allowed");
        }
        mExpandableMenu = true;
        SliderMenuExpandableAdapter adapter = new SliderMenuExpandableAdapter(obtainMenuContext(context == null ? listView.getContext() : context), this);
        mAdapter = adapter;
        adapter.bind(listView);
    }

    public void bindOnLeftPanel() {
        bindOnLeftPanel(null);
    }

    public void bindOnLeftPanel(Context context) {
        bind(mAddon.getLeftView(), context);
    }

    public void bindOnRightPanel() {
        bindOnRightPanel(null);
    }

    public void bindOnRightPanel(Context context) {
        bind(mAddon.getRightView(), context);
    }

    public int encodePage(int groupPosition, int childPosition) {
        if ((childPosition & CHILD_POSITION_MASK) != childPosition) {
            throw new RuntimeException("Ooops. Items too many you have, young padawan");
        }
        return (groupPosition << GROUP_POSITION_SHIFT) | childPosition;
    }

    public void decodePage(int position, int[] array) {
        array[0] = (position & GROUP_POSITION_MASK) >>> GROUP_POSITION_SHIFT;
        array[1] = position & CHILD_POSITION_MASK;
    }

    @SuppressWarnings("deprecation")
    View bindView(BaseSliderItem<?> item, View view, boolean selected, int defaultTextAppearance, int defaultTextAppearanceInverse) {
        TextView labelView = (TextView) view.findViewById(android.R.id.text1);
        if (labelView != null) {
            labelView.setText(item.mLabel);
        }
        final int textAppearance = item.mTextAppereance != 0 ? item.mTextAppereance : defaultTextAppearance;
        final int textAppearanceInverse = item.mTextAppereanceInverse != 0 ? item.mTextAppereanceInverse : defaultTextAppearanceInverse;
        setTextAppearance(labelView, mInverseTextColorWhenSelected && selected ? textAppearanceInverse : textAppearance);

        ImageView iconView = (ImageView) view.findViewById(android.R.id.icon1);
        iconView.setImageDrawable(item.mIcon);
        iconView.setVisibility(item.mIcon == null ? View.GONE : View.VISIBLE);

        return bindAppearanceView(view, selected, item);
    }

    private View bindAppearanceView(View view, boolean selected, BaseSliderItem<?> item) {
        View selectionHandlerView = view.findViewById(R.id.selectionHandler);
        View backgroundGroupIndicator = view.findViewById(R.id.groupIndicator);
        if (flag(FLAG_SELECTION_HANDLER) && selectionHandlerView != null) {
            selectionHandlerView.setBackgroundDrawable(selected ? new ColorDrawable(item.mSelectionHandlerColor) : null);
        }
        if (flag(FLAG_SELECTION_BACKGROUND)) {
            view.setBackgroundDrawable(selected ? new ColorDrawable(item.mBackgroundColor) : null);
        }
        if (flag(FLAG_SELECTION_HANDLER) && item instanceof SliderSubItem && backgroundGroupIndicator != null) {
            backgroundGroupIndicator.setBackgroundDrawable(new ColorDrawable(((SliderSubItem) item).mParentItem.mSelectionHandlerColor));
        }
        return view;
    }

    protected void changePage(int position) {
        changePage(position, getItemFromPosition(position));
    }

    private void changePage(int position, BaseSliderItem<?> item) {
        mIgnoreBackStack = true;
        if (mCurrentPage >= 0) {
            final BaseSliderItem<?> lastItem = getItemFromPosition(mCurrentPage);
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
        clearBackStack();
        if (item.mFragmentClass != null) {
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
                tag = "slider-fragment-" + fragment.hashCode();
            }
            clearBackStack();
            replaceFragment(mFragmentManager, fragment, tag, false);
        }
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
        mPageWasChanged = true;
        setCurrentPage(position, true, true);
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
        makeDefaultMenu(mAddon.obtainMenuContext(this));
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

    void notifyChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackStackChanged() {
        if (mHandleHomeKey) {
            mActionBar.setDisplayHomeAsUpEnabled(mAddon.isAddonEnabled() || mFragmentManager.getBackStackEntryCount() > 0);
        }
        if (mIgnoreBackStack) {
            return;
        }
        BaseSliderItem<?> item = getItemFromPosition(mCurrentPage);
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
        if (mListViewStub != null) {
            if (mListViewStub.getLayoutResource() == 0) {
                mListViewStub.setLayoutResource(hasSubItems() ? R.layout.slider_default_expandable_list_layout : R.layout.slider_default_list_layout);
            }
            if (mListViewStubContext != null) {
                mListViewStub.setLayoutInflater(LayoutInflater.from(mListViewStubContext));
            }
            bind(mListViewStub.inflate(), mListViewStubContext);
            mListViewStub = null;
            mListViewStubContext = null;
        }

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE, 0);
        }
        mIgnoreBackStack = true;
        mAddon.get().getSupportFragmentManager().addOnBackStackChangedListener(this);
        onBackStackChanged();
        mIgnoreBackStack = false;

        if (!mPageWasChanged && mItems.size() > 0) {
            setCurrentPage(mFuturePosition, true, true);
        }
    }

    public void onResume() {
        if (mCurrentPage < 0 && mItems.size() > 0) {
            setCurrentPage(mInitialPage, true, true);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
    }

    public boolean flag(int flag) {
        return (mAppearanceFlags & flag) == flag;
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

    void setCurrentPage(int position, boolean force, boolean openContentView) {
        if (mAdapter == null) {
            mFuturePosition = position;
            return;
        }
        if (force || mCurrentPage != position || mAddon.get().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            changePage(position, getItemFromPosition(position));
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageChange(mCurrentPage, position);
            }
            mCurrentPage = position;
        }
        if (mAddon.isAddonEnabled() && openContentView) {
            mAddon.openContentViewDelayed(40);
        }
    }

    private BaseSliderItem<?> getItemFromPosition(int position) {
        if (mExpandableMenu) {
            final int group = (position & GROUP_POSITION_MASK) >>> GROUP_POSITION_SHIFT;
            final int child = (position & CHILD_POSITION_MASK);
            if (group == GROUP_INVALID) {
                return null;
            }
            SliderItem item = mItems.get(group);
            if (child == CHILD_INVALID || item.mSubItems == null || item.mSubItems.size() <= child) {
                return item;
            }
            return item.mSubItems.get(child);
        } else {
            return mItems.get(position);
        }
    }

    public Activity getActivity() {
        return mAddon.get();
    }

    public void map(int theme) {
        map(theme, theme, theme);
    }

    public void map(int darkTheme, int lightTheme) {
        map(darkTheme, lightTheme, darkTheme);
    }

    public void map(int darkTheme, int lightTheme, int mixedTheme) {
        mThemes.map(darkTheme, lightTheme, mixedTheme);
    }

    void internalSetOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public Context obtainMenuContext(Context context) {
        context = sThemes.context(context);
        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.sliderMenuTheme});
        final int themeRes = a.getResourceId(0, 0);
        a.recycle();
        return themeRes == 0 ? context : new ContextThemeWrapperPlus(context, themeRes);
    }

    interface OnPageChangeListener {
        void onPageChange(int lastPage, int currentPage);
    }

    public static enum NavigateUpBehavior {
        PopUpFragment, ShowMenu
    }
}
