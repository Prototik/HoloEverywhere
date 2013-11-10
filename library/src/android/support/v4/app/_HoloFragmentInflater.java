
package android.support.v4.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity.FragmentTag;
import android.support.v7.internal.view.menu.ContextMenuDecorView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.FrameLayout;

public class _HoloFragmentInflater {
    static {
        NoSaveStateFrameLayout.sNoSaveStateFrameLayoutCreator = new NoSaveStateFrameLayout.NoSaveStateFrameLayoutCreator() {
            @Override
            public ViewGroup create(Fragment fragment, View view) {
                final FrameLayout wrapper;
                if (fragment instanceof _HoloFragment) {
					wrapper = new ContextMenuDecorView(view.getContext());
					((ContextMenuDecorView) wrapper).setProvider((_HoloFragment) fragment);
				} else {
					wrapper = new FrameLayout(view.getContext());
				}
                wrapper.setSaveChildrenState(false);
                ViewGroup.LayoutParams childParams = view.getLayoutParams();
                if (childParams != null) {
                    wrapper.setLayoutParams(childParams);
                }
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(lp);
                wrapper.addView(view);
                return wrapper;
            }
        };
    }
    
    public static void init() {
		
	}

    private static View inflate(AttributeSet attrs, View parent, FragmentActivity activity,
                                Fragment parentFragment) {
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
        int containerId = parent != null ? parent.getId() : View.NO_ID;
        if (containerId == View.NO_ID && id == View.NO_ID && tag == null) {
            throw new IllegalArgumentException(
                    attrs.getPositionDescription()
                            + ": Must specify unique android:id, android:tag, or have a parent with an id for "
                            + fname);
        }
        FragmentManagerImpl impl = obtainFragmentManager(activity, parentFragment);
        Fragment fragment = id != View.NO_ID ? impl.findFragmentById(id) : null;
        if (fragment == null && tag != null) {
            fragment = impl.findFragmentByTag(tag);
        }
        if (fragment == null && containerId != View.NO_ID) {
            fragment = impl.findFragmentById(containerId);
        }
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, fname);
            fragment.mParentFragment = parentFragment;
            fragment.mActivity = activity;
            fragment.mFromLayout = true;
            fragment.mFragmentId = id != 0 ? id : containerId;
            fragment.mContainer = (ViewGroup) parent;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = impl;
            fragment.onInflate(activity, attrs, fragment.mSavedFragmentState);
            impl.addFragment(fragment, true);
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
            impl.moveToState(fragment);
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

    public static View inflate(LayoutInflater layoutInflater, AttributeSet attrs, View parent,
                               Fragment fragment) {
        FragmentActivity activity = layoutInflater.getFragmentActivity();
        if (activity != null) {
            return inflate(attrs, parent, activity, fragment);
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
        return inflate(attrs, parent, activity, fragment);
    }

    private static FragmentManagerImpl obtainFragmentManager(FragmentActivity activity,
                                                             Fragment fragment) {
        FragmentManagerImpl fm = null;
        if (fragment != null) {
            fm = fragment.mChildFragmentManager;
            if (fm == null) {
                fm = (FragmentManagerImpl) fragment.getChildFragmentManager();
            }
        }
        if (fm == null && activity != null) {
            fm = activity.mFragments;
        }
        return fm;
    }
}
