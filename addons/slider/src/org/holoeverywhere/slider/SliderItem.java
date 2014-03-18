package org.holoeverywhere.slider;

import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.holoeverywhere.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SliderItem extends BaseSliderItem<SliderItem> implements Parcelable, IMenuAdder<SliderSubItem> {
    public static final Parcelable.Creator<SliderItem> CREATOR = new Parcelable.Creator<SliderItem>() {
        @Override
        public SliderItem[] newArray(int size) {
            return new SliderItem[size];
        }

        @Override
        public SliderItem createFromParcel(Parcel source) {
            try {
                return new SliderItem(source);
            } catch (Exception e) {
                throw new BadParcelableException(e);
            }
        }
    };
    protected List<SliderSubItem> mSubItems;

    public SliderItem() {
        super();
    }

    protected SliderItem(Parcel source) throws Exception {
        super(source);
        mSubItems = source.createTypedArrayList(SliderSubItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(mSubItems);
    }

    @Override
    public SliderSubItem add(CharSequence label) {
        return add(label, null, null, null);
    }

    @Override
    public SliderSubItem add(CharSequence label, int[] colors) {
        return add(label, null, null, colors);
    }

    @Override
    public SliderSubItem add(CharSequence label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    @Override
    public SliderSubItem add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    @Override
    public SliderSubItem add(CharSequence label, Class<? extends Fragment> fragmentClass, int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    @Override
    public SliderSubItem add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors) {
        if (mSliderMenu == null) {
            // Very bad
            throw new RuntimeException("SliderItem hasn't attached to SliderMenu, I cannot do anything!");
        }
        return add(new SliderSubItem()
                .setLabel(label)
                .setFragmentClass(fragmentClass)
                .setFragmentArguments(fragmentArguments)
        ).fillColors(colors);
    }

    @Override
    public SliderSubItem add(int label) {
        return add(label, null, null, null);
    }

    @Override
    public SliderSubItem add(int label, int[] colors) {
        return add(label, null, null, colors);
    }

    @Override
    public SliderSubItem add(int label, Class<? extends Fragment> fragmentClass) {
        return add(label, fragmentClass, null, null);
    }

    @Override
    public SliderSubItem add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
        return add(label, fragmentClass, fragmentArguments, null);
    }

    @Override
    public SliderSubItem add(int label, Class<? extends Fragment> fragmentClass, int[] colors) {
        return add(label, fragmentClass, null, colors);
    }

    @Override
    public SliderSubItem add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors) {
        if (mSliderMenu == null) {
            // Very bad
            throw new RuntimeException("SliderItem hasn't attached to SliderMenu, I cannot do anything!");
        }
        return add(mSliderMenu.getActivity().getText(label), fragmentClass, fragmentArguments, colors);
    }

    @Override
    public SliderSubItem add(SliderSubItem item) {
        if (item.mSliderMenu != null || item.mParentItem != null) {
            throw new IllegalArgumentException("SubItem already has a parent: "
                    + item + " (" + item.mSliderMenu + ")");
        }
        item.mSliderMenu = mSliderMenu;
        item.mParentItem = this;
        item.mBackgroundColor = mBackgroundColor;
        item.mSelectionHandlerColor = mSelectionHandlerColor;
        obtainSubItemsList().add(item);
        mSliderMenu.notifyChanged();
        return item;
    }

    @Override
    public SliderSubItem add(SliderSubItem item, int position) {
        if (item.mSliderMenu != null || item.mParentItem != null) {
            throw new IllegalArgumentException("SubItem already has a parent: "
                    + item + " (" + item.mSliderMenu + ")");
        }
        item.mSliderMenu = mSliderMenu;
        item.mParentItem = this;
        item.mBackgroundColor = mBackgroundColor;
        item.mSelectionHandlerColor = mSelectionHandlerColor;
        obtainSubItemsList().add(position, item);
        mSliderMenu.notifyChanged();
        return item;
    }

    private List<SliderSubItem> obtainSubItemsList() {
        if (mSubItems == null) {
            mSubItems = new ArrayList<SliderSubItem>();
        }
        return mSubItems;
    }

    public boolean hasSubItems() {
        return mSubItems != null && mSubItems.size() > 0;
    }
}
