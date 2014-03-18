package org.holoeverywhere.slider;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;

public class SliderSubItem extends BaseSliderItem<SliderSubItem> implements Parcelable {
    public static final Parcelable.Creator<SliderSubItem> CREATOR = new Creator<SliderSubItem>() {
        @Override
        public SliderSubItem[] newArray(int size) {
            return new SliderSubItem[size];
        }

        @Override
        public SliderSubItem createFromParcel(Parcel source) {
            try {
                return new SliderSubItem(source);
            } catch (Exception e) {
                throw new BadParcelableException(e);
            }
        }
    };
    SliderItem mParentItem;

    protected SliderSubItem(Parcel source) throws Exception {
        super(source);
    }

    public SliderSubItem() {
    }

    public boolean isSelectable() {
        return hasVisiblePage();
    }
}
