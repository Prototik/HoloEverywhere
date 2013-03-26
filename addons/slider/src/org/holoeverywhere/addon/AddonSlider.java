
package org.holoeverywhere.addon;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.slider.ISlider;
import org.holoeverywhere.slider.R;
import org.holoeverywhere.slider.SliderView;
import org.holoeverywhere.slider.SliderView.SavedState;
import org.holoeverywhere.slider.SliderView.SliderDrawer;
import org.holoeverywhere.slider.SliderView.TouchMode;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class AddonSlider extends IAddon {
    public static class AddonSliderA extends IAddonActivity implements ISlider {
        private static final String KEY_SLIDER_STATE = "holo:slider:state";
        private View mContentView;
        private boolean mDragWithActionBar = false;
        private boolean mForceNotRestoreInstance = false;

        private boolean mRejectContentView = true;

        private SliderView mSliderView;

        @Override
        public void disableShadow() {
            mSliderView.disableShadow();
        }

        @Override
        public View findViewById(int id) {
            if (mContentView != null) {
                return mContentView.findViewById(id);
            }
            return null;
        }

        public void forceNotRestoreInstance() {
            mForceNotRestoreInstance = true;
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
        public int getLeftViewWidth() {
            return mSliderView.getLeftViewWidth();
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
            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SLIDER_STATE)
                    && !mForceNotRestoreInstance) {
                SavedState state = savedInstanceState.getParcelable(KEY_SLIDER_STATE);
                mSliderView.dispatchRestoreInstanceState(state);
            }
            mRejectContentView = false;
            TypedArray a = get().obtainStyledAttributes(new int[] {
                    android.R.attr.windowBackground
            });
            final int windowBackground = a.getResourceId(0, 0);
            a.recycle();
            if (mDragWithActionBar) {
                get().setContentView(mContentView);
                ViewGroup decorView = (ViewGroup) get().getWindow().getDecorView();
                View view = decorView.getChildAt(0);
                view.setBackgroundResource(windowBackground);
                decorView.removeView(view);
                mSliderView.setContentView(view);
                decorView.addView(mSliderView, 0);
            } else {
                if (windowBackground > 0) {
                    mContentView.setBackgroundResource(windowBackground);
                }
                mSliderView.setContentView(mContentView);
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

        @Override
        public void setBlockLongMove(boolean blockLongMove) {
            mSliderView.setBlockLongMove(blockLongMove);
        }

        @Override
        public void setContentView(View view) {
            if (mRejectContentView) {
                mContentView = view;
            } else {
                mSliderView.setContentView(view);
            }
        }

        @Override
        public boolean setContentView(View view, LayoutParams params) {
            if (mRejectContentView) {
                mContentView = view;
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
        public void setLeftView(View view) {
            mSliderView.setLeftView(view);
        }

        @Override
        public void setLeftViewWidth(int leftViewWidth) {
            mSliderView.setLeftViewWidth(leftViewWidth);
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
        public void setRightView(View view) {
            mSliderView.setRightView(view);
        }

        @Override
        public void setRightViewWidth(int rightViewWidth) {
            mSliderView.setRightViewWidth(rightViewWidth);
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
