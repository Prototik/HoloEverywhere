
package org.holoeverywhere.slidingmenu;

import org.holoeverywhere.addons.IAddon;
import org.holoeverywhere.addons.IAddonActivity;
import org.holoeverywhere.addons.IAddonFragment;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.slidingmenu.AddonSlidingMenu.SlidingMenuA;
import org.holoeverywhere.slidingmenu.AddonSlidingMenu.SlidingMenuF;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AddonSlidingMenu extends IAddon<SlidingMenuA, SlidingMenuF> {
    public static class SlidingMenuA extends IAddonActivity implements SlidingActivityBase {
        static {
            SlidingActivityHelper.init();
        }
        private SlidingActivityHelper mHelper;

        public SlidingMenuA(Activity activity) {
            super(activity);
        }

        protected SlidingActivityHelper getHelper() {
            if (mHelper == null) {
                mHelper = new SlidingActivityHelper(getActivity());
            }
            return mHelper;
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            return getHelper().onKeyUp(keyCode, event);
        }

        @Override
        public View findViewById(int id) {
            return getHelper().findViewById(id);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            getHelper().onCreate(savedInstanceState);
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            getHelper().onPostCreate(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            getHelper().onSaveInstanceState(outState);
        }

        @Override
        public boolean setContentView(View view, LayoutParams params) {
            getHelper().registerAboveContentView(view, params);
            return super.setContentView(view, params);
        }

        public void toggle() {
            getHelper().toggle();
        }

        @Override
        public SlidingMenu getSlidingMenu() {
            return getHelper().getSlidingMenu();
        }

        @Override
        public void setBehindContentView(int layoutResID) {
            setBehindContentView(getActivity().getLayoutInflater().inflate(layoutResID));
        }

        @Override
        public void setBehindContentView(View view) {
            setBehindContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }

        @Override
        public void setBehindContentView(View view, LayoutParams layoutParams) {
            getHelper().setBehindContentView(view, layoutParams);
        }

        @Override
        public void setSlidingActionBarEnabled(boolean slidingActionBarEnabled) {
            getHelper().setSlidingActionBarEnabled(slidingActionBarEnabled);
        }

        @Override
        public void showAbove() {
            getHelper().showAbove();
        }

        @Override
        public void showBehind() {
            getHelper().showBehind();
        }
    }

    public static class SlidingMenuF extends IAddonFragment {
        public SlidingMenuF(Fragment fragment) {
            super(fragment);
        }
    }

    @Override
    public SlidingMenuA createAddon(Activity activity) {
        return new SlidingMenuA(activity);
    }

    @Override
    public SlidingMenuF createAddon(Fragment fragment) {
        return new SlidingMenuF(fragment);
    }
}
