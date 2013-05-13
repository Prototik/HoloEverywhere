
package org.holoeverywhere.addon;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.addon.IAddon.Addon;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.slider.ISlider;
import org.holoeverywhere.slider.R;
import org.holoeverywhere.slider.SliderView;
import org.holoeverywhere.slider.SliderView.OnSlideListener;
import org.holoeverywhere.slider.SliderView.SavedState;
import org.holoeverywhere.slider.SliderView.SliderDrawer;
import org.holoeverywhere.slider.SliderView.TouchMode;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

@Addon(weight = 40)
public class AddonSlider extends IAddon {
    static {
        LayoutInflater.register(SliderView.class);
    }

    public static class AddonSliderA extends IAddonActivity implements ISlider {
        private static final String KEY_SLIDER_STATE = "holo:slider:state";
        private boolean mAddonEnabled = true;
        private boolean mDragWithActionBar = false;
        private boolean mForceNotRestoreInstance = false;
        private boolean mRejectContentView = true;
        private SliderView mSliderView;
        private View mView;

        @Override
        public void disableShadow() {
            mSliderView.disableShadow();
        }

        @Override
        public View findViewById(int id) {
            if (mView != null) {
                View view = mView.findViewById(id);
                if (view != null) {
                    return view;
                }
            }
            return mSliderView != null ? mSliderView.findViewById(id) : null;
        }

        public void forceNotRestoreInstance() {
            mForceNotRestoreInstance = true;
        }

        @Override
        public View getContentView() {
            return mSliderView.getContentView();
        }

        @Override
        public SliderDrawer getDrawer() {
            return mSliderView.getDrawer();
        }

        @Override
        public int getLeftDragBound() {
            return mSliderView.getLeftDragBound();
        }

        @Override
        public float getLeftTranslateFactor() {
            return mSliderView.getLeftTranslateFactor();
        }

        @Override
        public View getLeftView() {
            return mSliderView.getLeftView();
        }

        @Override
        public int getLeftViewShadowColor() {
            return mSliderView.getLeftViewShadowColor();
        }

        @Override
        public int getLeftViewWidth() {
            return mSliderView.getLeftViewWidth();
        }

        @Override
        public OnSlideListener getOnSlideListener() {
            return mSliderView.getOnSlideListener();
        }

        @Override
        public int getProgress() {
            return mSliderView.getProgress();
        }

        @Override
        public int getRightDragBound() {
            return mSliderView.getRightDragBound();
        }

        @Override
        public float getRightTranslateFactor() {
            return mSliderView.getRightTranslateFactor();
        }

        @Override
        public View getRightView() {
            return mSliderView.getRightView();
        }

        @Override
        public int getRightViewShadowColor() {
            return mSliderView.getRightViewShadowColor();
        }

        @Override
        public int getRightViewWidth() {
            return mSliderView.getRightViewWidth();
        }

        public SliderView getSliderView() {
            return mSliderView;
        }

        @Override
        public TouchMode getTouchMode() {
            return mSliderView.getTouchMode();
        }

        @Override
        public int getTouchModeLeftMargin() {
            return mSliderView.getTouchModeLeftMargin();
        }

        @Override
        public int getTouchModeRightMargin() {
            return mSliderView.getTouchModeRightMargin();
        }

        public boolean isAddonEnabled() {
            return mAddonEnabled;
        }

        @Override
        public boolean isBlockLongMove() {
            return mSliderView.isBlockLongMove();
        }

        @Override
        public boolean isContentShowed() {
            return mSliderView.isContentShowed();
        }

        public boolean isDragWithActionBar() {
            return mDragWithActionBar;
        }

        public boolean isForceNotRestoreInstance() {
            return mForceNotRestoreInstance;
        }

        @Override
        public boolean isLeftShowed() {
            return mSliderView.isLeftShowed();
        }

        @Override
        public boolean isOverlayActionBar() {
            return mSliderView.isOverlayActionBar();
        }

        @Override
        public boolean isRightShowed() {
            return mSliderView.isRightShowed();
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            mRejectContentView = false;
            if (!mAddonEnabled) {
                get().setContentView(mView);
                mSliderView = null;
                return;
            }
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SLIDER_STATE)
                    && !mForceNotRestoreInstance) {
                SavedState state = savedInstanceState.getParcelable(KEY_SLIDER_STATE);
                mSliderView.dispatchRestoreInstanceState(state);
            }
            View view = mView.findViewById(R.id.contentView);
            if (view != null) {
                mSliderView.setContentView(view);
            }
            view = mView.findViewById(R.id.leftView);
            if (view != null) {
                mSliderView.setLeftView(view);
            }
            view = mView.findViewById(R.id.rightView);
            if (view != null) {
                mSliderView.setRightView(view);
            }
            if (mSliderView.getContentView() == null && mSliderView.getLeftView() == null
                    && mSliderView.getRightView() == null) {
                mSliderView.setContentView(mView);
            }
            TypedArray a = get().obtainStyledAttributes(new int[] {
                    android.R.attr.windowBackground
            });
            final int windowBackground = a.getResourceId(0, 0);
            a.recycle();
            if (mDragWithActionBar) {
                final View v = mSliderView.getContentView();
                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v);
                }
                get().setContentView(v);
                ViewGroup decorView = (ViewGroup) get().getWindow().getDecorView();
                view = decorView.getChildAt(0);
                if (view.getBackground() == null) {
                    view.setBackgroundResource(windowBackground);
                }
                decorView.removeView(view);
                mSliderView.setContentView(view);
                decorView.addView(mSliderView, 0);
            } else {
                final View contentView = mSliderView.getContentView();
                if (windowBackground > 0 && contentView != null
                        && contentView.getBackground() == null) {
                    contentView.setBackgroundResource(windowBackground);
                }
                get().setContentView(mSliderView);
            }
        }

        @Override
        public void onPreCreate(Bundle savedInstanceState) {
            mSliderView = new SliderView(get());
            mSliderView.setId(R.id.slider);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            if (mSliderView != null) {
                outState.putParcelable(KEY_SLIDER_STATE, mSliderView.dispatchSaveInstanceState());
            }
        }

        @Override
        public boolean requestWindowFeature(int featureId) {
            if (featureId == Window.FEATURE_ACTION_BAR_OVERLAY) {
                setOverlayActionBar(true);
            }
            return super.requestWindowFeature(featureId);
        }

        public void setAddonEnabled(boolean addonEnabled) {
            mAddonEnabled = addonEnabled;
        }

        @Override
        public void setBlockLongMove(boolean blockLongMove) {
            mSliderView.setBlockLongMove(blockLongMove);
        }

        @Override
        public void setContentView(int layoutId) {
            mSliderView.setContentView(layoutId);
        }

        @Override
        public void setContentView(View view) {
            mSliderView.setContentView(view);
        }

        /**
         * Doesn't call this manually!
         */
        @Override
        @Deprecated
        public boolean setContentView(View view, LayoutParams params) {
            if (mRejectContentView) {
                mView = view;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setDragBound(int dragBound) {
            mSliderView.setDragBound(dragBound);
        }

        public void setDragWithActionBar(boolean dragWithActionBar) {
            mDragWithActionBar = dragWithActionBar;
        }

        @Override
        public void setDrawer(SliderDrawer drawer) {
            mSliderView.setDrawer(drawer);
        }

        @Override
        public void setLeftDragBound(int leftDragBound) {
            mSliderView.setLeftDragBound(leftDragBound);
        }

        @Override
        public void setLeftTranslateFactor(float leftTranslateFactor) {
            mSliderView.setLeftTranslateFactor(leftTranslateFactor);
        }

        @Override
        public void setLeftView(int layoutId) {
            mSliderView.setLeftView(layoutId);
        }

        @Override
        public void setLeftView(View view) {
            mSliderView.setLeftView(view);
        }

        @Override
        public void setLeftViewShadowColor(int leftViewShadowColor) {
            mSliderView.setLeftViewShadowColor(leftViewShadowColor);
        }

        @Override
        public void setLeftViewWidth(int leftViewWidth) {
            mSliderView.setLeftViewWidth(leftViewWidth);
        }

        @Override
        public void setOnSlideListener(OnSlideListener onSlideListener) {
            mSliderView.setOnSlideListener(onSlideListener);
        }

        @Override
        public void setOverlayActionBar(boolean overlayActionBar) {
            mSliderView.setOverlayActionBar(overlayActionBar);
        }

        @Override
        public void setProgress(int progress) {
            mSliderView.setProgress(progress);
        }

        @Override
        public void setRightDragBound(int rightDragBound) {
            mSliderView.setRightDragBound(rightDragBound);
        }

        @Override
        public void setRightTranslateFactor(float rightTranslateFactor) {
            mSliderView.setRightTranslateFactor(rightTranslateFactor);
        }

        @Override
        public void setRightView(int layoutId) {
            mSliderView.setRightView(layoutId);
        }

        @Override
        public void setRightView(View view) {
            mSliderView.setRightView(view);
        }

        @Override
        public void setRightViewShadowColor(int rightViewShadowColor) {
            mSliderView.setRightViewShadowColor(rightViewShadowColor);
        }

        @Override
        public void setRightViewWidth(int rightViewWidth) {
            mSliderView.setRightViewWidth(rightViewWidth);
        }

        @Override
        public void setShadowColor(int shadowColor) {
            mSliderView.setShadowColor(shadowColor);
        }

        @Override
        public void setTouchMode(TouchMode touchMode) {
            mSliderView.setTouchMode(touchMode);
        }

        @Override
        public void setTouchModeLeftMargin(int touchModeLeftMargin) {
            mSliderView.setTouchModeLeftMargin(touchModeLeftMargin);
        }

        @Override
        public void setTouchModeMargin(int touchModeMargin) {
            mSliderView.setTouchModeMargin(touchModeMargin);
        }

        @Override
        public void setTouchModeRightMargin(int touchModeRightMargin) {
            mSliderView.setTouchModeRightMargin(touchModeRightMargin);

        }

        @Override
        public void setTranslateFactor(float translateFactor) {
            mSliderView.setTranslateFactor(translateFactor);
        }

        @Override
        public void showContentDelayed() {
            mSliderView.showContentDelayed();
        }

        @Override
        public void showContentView(boolean smooth) {
            mSliderView.showContentView(smooth);
        }

        @Override
        public void showLeftView(boolean smooth) {
            mSliderView.showLeftView(smooth);
        }

        @Override
        public void showRightView(boolean smooth) {
            mSliderView.showRightView(smooth);
        }

        @Override
        public void toggle() {
            mSliderView.toggle();
        }
    }

    public AddonSlider() {
        register(Activity.class, AddonSliderA.class);
    }
}
