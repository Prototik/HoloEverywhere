
package org.holoeverywhere.addon;

import org.holoeverywhere.addon.IAddon.Addon;
import org.holoeverywhere.slider.R;
import org.holoeverywhere.slider.SliderMenu;
import org.holoeverywhere.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

@Addon(weight = 40)
public class AddonSlider extends IAddon {
    public static class AddonSliderA extends IAddonActivity {
        private boolean mAddonEnabled = true;
        private DrawerLayout mDrawerLayout;
        private Context mMenuContext;
        private int mMenuLayout;
        private boolean mOverlayActionBar = false;
        private SliderMenu mSliderMenu;

        private void attach(View view, int gravity) {
            if (view == null) {
                return;
            }
            final ViewGroup.LayoutParams initialParams = view.getLayoutParams();
            DrawerLayout.LayoutParams params;
            if (initialParams instanceof DrawerLayout.LayoutParams) {
                params = (LayoutParams) initialParams;
            } else if (initialParams != null) {
                params = new LayoutParams(initialParams);
            } else {
                params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            }
            params.gravity = gravity;
            view.setLayoutParams(params);
            ViewParent parent = view.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(view);
            }
            requestDrawerLayout();
            mDrawerLayout.addView(view, gravity == Gravity.NO_GRAVITY ? 0 : -1, params);
        }

        public void closeDrawer(int gravity) {
            requestDrawerLayout().closeDrawer(gravity);
        }

        public void closeDrawer(View drawerView) {
            requestDrawerLayout().closeDrawer(drawerView);
        }

        public void closeDrawers() {
            requestDrawerLayout().closeDrawers();
        }

        public void closeLeftView() {
            closeView(getLeftView());
        }

        public void closeRightView() {
            closeView(getRightView());
        }

        private void closeView(View view) {
            if (view != null && requestDrawerLayout().isDrawerOpen(view)) {
                closeDrawer(view);
            }
        }

        @Override
        public View findViewById(int id) {
            return mDrawerLayout != null ? mDrawerLayout.findViewById(id) : null;
        }

        public View getContentView() {
            return get().findViewById(R.id.contentView);
        }

        public DrawerLayout getDrawerLayout() {
            return mDrawerLayout;
        }

        public int getDrawerLockMode(int edgeGravity) {
            return requestDrawerLayout().getDrawerLockMode(edgeGravity);
        }

        public int getDrawerLockMode(View drawerView) {
            return requestDrawerLayout().getDrawerLockMode(drawerView);
        }

        public View getLeftView() {
            return get().findViewById(R.id.leftView);
        }

        public Context getMenuContext() {
            return mMenuContext;
        }

        public int getMenuLayout() {
            return mMenuLayout;
        }

        public View getRightView() {
            return get().findViewById(R.id.rightView);
        }

        public boolean isAddonEnabled() {
            return mAddonEnabled;
        }

        public boolean isDrawerOpen(int drawerGravity) {
            return requestDrawerLayout().isDrawerOpen(drawerGravity);
        }

        public boolean isDrawerOpen(View drawer) {
            return requestDrawerLayout().isDrawerOpen(drawer);
        }

        public boolean isDrawerVisible(int drawerGravity) {
            return requestDrawerLayout().isDrawerVisible(drawerGravity);
        }

        public boolean isDrawerVisible(View drawer) {
            return requestDrawerLayout().isDrawerVisible(drawer);
        }

        public boolean isOverlayActionBar() {
            return mOverlayActionBar;
        }

        public SliderMenu obtainDefaultSliderMenu() {
            return obtainDefaultSliderMenu(0);
        }

        public SliderMenu obtainDefaultSliderMenu(int menuLayout) {
            if (mSliderMenu != null) {
                return mSliderMenu;
            }
            mMenuLayout = menuLayout;
            mMenuContext = get().getSupportActionBarContext();
            setDrawerLayout(R.layout.slider_default_layout);
            setOverlayActionBar(true);
            mSliderMenu = new SliderMenu(this);
            mSliderMenu.setHandleHomeKey(true);
            mSliderMenu.makeDefaultMenu(mMenuContext);
            return mSliderMenu;
        }

        public SliderMenu obtainSliderMenu() {
            if (mSliderMenu == null) {
                mSliderMenu = new SliderMenu(this);
            }
            return mSliderMenu;
        }

        @Override
        public boolean onHomePressed() {
            if (mSliderMenu != null) {
                return mSliderMenu.onHomePressed();
            }
            return super.onHomePressed();
        }

        @SuppressLint("NewApi")
        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            if (mSliderMenu != null) {
                mSliderMenu.onPostCreate(savedInstanceState);
            }
            if (!mAddonEnabled) {
                return;
            }
            requestDrawerLayout();
            final View contentView = get().findViewById(R.id.contentView);
            if (contentView == null) {
                throw new IllegalStateException(
                        "You should specify your content view by @id/contentView");
            }
            final View leftView = get().findViewById(R.id.leftView), rightView = get()
                    .findViewById(R.id.rightView);
            mDrawerLayout.removeAllViewsInLayout();
            if (mOverlayActionBar) {
                mDrawerLayout.setFitsSystemWindows(true);
                get().setContentView(contentView);
                ViewGroup decorView = (ViewGroup) get().getWindow().getDecorView();
                View view = decorView.getChildAt(0);
                decorView.removeView(view);
                setContentView(view);
                decorView.addView(mDrawerLayout, 0);
            } else {
                get().setContentView(mDrawerLayout);
                setContentView(contentView);
            }
            setLeftView(leftView);
            setRightView(rightView);
        }

        @Override
        public void onResume() {
            if (mSliderMenu != null) {
                mSliderMenu.onResume();
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (mSliderMenu != null) {
                mSliderMenu.onSaveInstanceState(outState);
            }
        }

        public void openContentView() {
            closeDrawers();
        }

        public void openContentViewDelayed(int delay) {
            handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openContentView();
                }
            }, delay);
        }

        public void openDrawer(int gravity) {
            requestDrawerLayout().openDrawer(gravity);
        }

        public void openDrawer(View drawerView) {
            requestDrawerLayout().openDrawer(drawerView);
        }

        public void openLeftView() {
            openView(getLeftView());
        }

        public void openRightView() {
            openView(getRightView());
        }

        private void openView(View view) {
            if (view != null && !requestDrawerLayout().isDrawerOpen(view)) {
                openDrawer(view);
            }
        }

        private DrawerLayout requestDrawerLayout() {
            if (mDrawerLayout == null && mAddonEnabled) {
                setDrawerLayout(R.layout.slider_default_layout);
            }
            return mDrawerLayout;
        }

        @Override
        public boolean requestWindowFeature(int featureId) {
            if (Window.FEATURE_ACTION_BAR_OVERLAY == featureId) {
                setOverlayActionBar(true);
            }
            return super.requestWindowFeature(featureId);
        }

        public void setAddonEnabled(boolean addonEnabled) {
            mAddonEnabled = addonEnabled;
        }

        @SuppressLint("InlinedApi")
        public void setContentView(View view) {
            attach(view, Gravity.NO_GRAVITY);
        }

        public void setDrawerLayout(DrawerLayout drawerLayout) {
            mDrawerLayout = drawerLayout;
            if (mDrawerLayout != null) {
                mDrawerLayout.setId(R.id.slider);
            }
        }

        public void setDrawerLayout(int layoutResource) {
            final View view = get().getThemedLayoutInflater().inflate(layoutResource, null, false);
            if (view instanceof DrawerLayout) {
                setDrawerLayout((DrawerLayout) view);
            } else {
                get().setContentView(view);
                setAddonEnabled(false);
            }
        }

        public void setDrawerListener(DrawerListener listener) {
            requestDrawerLayout().setDrawerListener(listener);
        }

        public void setDrawerLockMode(int lockMode) {
            requestDrawerLayout().setDrawerLockMode(lockMode);
        }

        public void setDrawerLockMode(int lockMode, int edgeGravity) {
            requestDrawerLayout().setDrawerLockMode(lockMode, edgeGravity);
        }

        public void setDrawerLockMode(int lockMode, View drawerView) {
            requestDrawerLayout().setDrawerLockMode(lockMode, drawerView);
        }

        public void setDrawerShadow(Drawable shadowDrawable, int gravity) {
            requestDrawerLayout().setDrawerShadow(shadowDrawable, gravity);
        }

        public void setDrawerShadow(int resId, int gravity) {
            requestDrawerLayout().setDrawerShadow(resId, gravity);
        }

        @SuppressLint("InlinedApi")
        public void setLeftView(View view) {
            attach(view, Gravity.START);
        }

        public void setOverlayActionBar(boolean overlayActionBar) {
            mOverlayActionBar = overlayActionBar;
        }

        @SuppressLint("InlinedApi")
        public void setRightView(View view) {
            attach(view, Gravity.END);
        }

        public void setScrimColor(int color) {
            requestDrawerLayout().setScrimColor(color);
        }

        public void toggle() {
            View view = getLeftView();
            if (view == null) {
                view = getRightView();
            }
            if (view == null) {
                return;
            }
            if (requestDrawerLayout().isDrawerOpen(view)) {
                openContentView();
            } else {
                openView(view);
            }
        }

    }

    public AddonSlider() {
        registerActivity(AddonSliderA.class);
    }
}
