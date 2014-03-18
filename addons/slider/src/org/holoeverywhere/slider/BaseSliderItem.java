package org.holoeverywhere.slider;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.drawable.DrawableCompat;

import java.lang.ref.WeakReference;

class BaseSliderItem<T extends BaseSliderItem<T>> implements Parcelable {
    int mCustomLayout = 0;
    Fragment.SavedState mSavedState;
    SliderMenu mSliderMenu;
    WeakReference<Fragment> mLastFragment;
    boolean mSaveState = true;
    int mBackgroundColor = 0;
    Bundle mFragmentArguments;
    Class<? extends Fragment> mFragmentClass;
    CharSequence mLabel;
    int mSelectionHandlerColor = 0;
    String mTag;
    int mTextAppereance = 0;
    int mTextAppereanceInverse = 0;
    Drawable mIcon;

    BaseSliderItem() {
    }

    BaseSliderItem(Parcel source) throws Exception {
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public T setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return (T) this;
    }

    public int getCustomLayout() {
        return mCustomLayout;
    }

    public T setCustomLayout(int customLayout) {
        mCustomLayout = customLayout;
        return (T) this;
    }

    public Bundle getFragmentArguments() {
        return mFragmentArguments;
    }

    public T setFragmentArguments(Bundle fragmentArguments) {
        mFragmentArguments = fragmentArguments;
        return (T) this;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    public T setFragmentClass(Class<? extends Fragment> fragmentClass) {
        if (mFragmentClass == fragmentClass) {
            return (T) this;
        }
        mFragmentClass = fragmentClass;
        mSavedState = null;
        return (T) this;
    }

    public CharSequence getLabel() {
        return mLabel;
    }

    public T setLabel(CharSequence label) {
        mLabel = label;
        invalidate();
        return (T) this;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public T setIconAttr(int attrId) {
        TypedArray a = mSliderMenu.getActivity().obtainStyledAttributes(new int[]{attrId});
        final Drawable drawable = a.getDrawable(0);
        a.recycle();
        return setIcon(drawable);
    }

    public T setIcon(int resId) {
        if (mSliderMenu == null) {
            throw new IllegalStateException("You cannot provide icon before adding item to SliderMenu");
        }
        return setIcon(DrawableCompat.getDrawable(mSliderMenu.getActivity().getResources(), resId));
    }

    public T setIcon(Drawable icon) {
        mIcon = icon;
        invalidate();
        return (T) this;
    }

    public int getSelectionHandlerColor() {
        return mSelectionHandlerColor;
    }

    public T setSelectionHandlerColor(int selectionHandlerColor) {
        mSelectionHandlerColor = selectionHandlerColor;
        return (T) this;
    }

    public String getTag() {
        return mTag;
    }

    public T setTag(String tag) {
        mTag = tag;
        return (T) this;
    }

    public int getTextAppereance() {
        return mTextAppereance;
    }

    public T setTextAppereance(int textAppereance) {
        mTextAppereance = textAppereance;
        return (T) this;
    }

    public int getTextAppereanceInverse() {
        return mTextAppereanceInverse;
    }

    public T setTextAppereanceInverse(int textAppereanceInverse) {
        mTextAppereanceInverse = textAppereanceInverse;
        return (T) this;
    }

    private void invalidate() {
        if (mSliderMenu != null) {
            mSliderMenu.invalidate();
        }
    }

    public boolean isSaveState() {
        return mSaveState;
    }

    public T setSaveState(boolean saveState) {
        if (mSaveState == saveState) {
            return (T) this;
        }
        mSaveState = saveState;
        if (!saveState) {
            mSavedState = null;
        }
        return (T) this;
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
        TextUtils.writeToParcel(mLabel, dest, flags);
    }

    public T fillColors(int[] colors) {
        if (colors != null && colors.length >= 2 && mSliderMenu != null) {
            final Resources res = mSliderMenu.getActivity().getResources();
            fillColors(res.getColor(colors[0]), res.getColor(colors[1]));
        }
        return (T) this;
    }

    public T fillColors(int backgroundColor, int selectionHandlerColor) {
        setBackgroundColor(backgroundColor);
        setSelectionHandlerColor(selectionHandlerColor);
        return (T) this;
    }

    public boolean hasVisiblePage() {
        return mFragmentClass != null;
    }
}
