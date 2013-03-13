
package android.support.v4.app;

import org.holoeverywhere.LayoutInflater;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity.FragmentTag;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class _HoloFragmentInflater {
    private static final String TAG = "HoloFragmentInflater";

    public static View inflate(AttributeSet attrs, View parent, FragmentActivity activity) {
        String fname = attrs.getAttributeValue(null, "class");
        TypedArray a = activity.obtainStyledAttributes(attrs, FragmentTag.Fragment);
        if (fname == null) {
            fname = a.getString(FragmentTag.Fragment_name);
        }
        if (fname.startsWith(".")) {
            fname = activity.getPackageName() + fname;
        }
        int id = a.getResourceId(FragmentTag.Fragment_id, View.NO_ID);
        String tag = a.getString(FragmentTag.Fragment_tag);
        a.recycle();
        int containerId = parent != null ? parent.getId() : 0;
        if (containerId == View.NO_ID && id == View.NO_ID && tag == null) {
            throw new IllegalArgumentException(
                    attrs.getPositionDescription()
                            + ": Must specify unique android:id, android:tag, or have a parent with an id for "
                            + fname);
        }
        Fragment fragment = id != View.NO_ID ? activity.mFragments.findFragmentById(id) : null;
        if (fragment == null && tag != null) {
            fragment = activity.mFragments.findFragmentByTag(tag);
        }
        if (fragment == null && containerId != View.NO_ID) {
            fragment = activity.mFragments.findFragmentById(containerId);
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "onCreateView: id=0x"
                    + Integer.toHexString(id) + " fname=" + fname
                    + " existing=" + fragment);
        }
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, fname);
            fragment.mFromLayout = true;
            fragment.mFragmentId = id != 0 ? id : containerId;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = activity.mFragments;
            fragment.onInflate(activity, attrs, fragment.mSavedFragmentState);
            activity.mFragments.addFragment(fragment, true);
        } else if (fragment.mInLayout) {
            throw new IllegalArgumentException(attrs.getPositionDescription()
                    + ": Duplicate id 0x" + Integer.toHexString(id)
                    + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId)
                    + " with another fragment for " + fname);
        } else {
            fragment.mInLayout = true;
            if (!fragment.mRetaining) {
                fragment.onInflate(activity, attrs, fragment.mSavedFragmentState);
            }
            activity.mFragments.moveToState(fragment);
        }

        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + fname
                    + " did not create a view.");
        }
        if (id != 0) {
            fragment.mView.setId(id);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(tag);
        }
        return fragment.mView;
    }

    public static View inflate(LayoutInflater layoutInflater, AttributeSet attrs, View parent) {
        FragmentActivity activity = layoutInflater.getFragmentActivity();
        if (activity != null) {
            return inflate(attrs, parent, activity);
        }
        Context context = layoutInflater.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof FragmentActivity) {
                activity = (FragmentActivity) context;
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (activity == null) {
            throw new IllegalStateException("Cannot find any reference to FragmentActivity");
        }
        return inflate(attrs, parent, activity);
    }
}
