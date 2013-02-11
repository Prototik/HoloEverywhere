
package org.holoeverywhere.addon;

import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuA;
import org.holoeverywhere.addon.AddonSlidingMenu.AddonSlidingMenuF;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.slidingmenu.lib.app.SlidingActivityHelper;

public class AddonSlidingMenu extends IAddon<AddonSlidingMenuA, AddonSlidingMenuF> {
    public static class AddonSlidingMenuA extends IAddonActivity {
        private SlidingActivityHelper mHelper;

        public AddonSlidingMenuA(Activity activity) {
            super(activity);
        }

        @Override
        public View findViewById(int id) {
            return mHelper != null ? mHelper.findViewById(id) : null;
        }

        public com.slidingmenu.lib.SlidingMenu getSlidingMenu() {
            return mHelper.getSlidingMenu();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mHelper = new SlidingActivityHelper(getActivity());
            mHelper.onCreate(savedInstanceState);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            return mHelper.onKeyUp(keyCode, event);
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            mHelper.onPostCreate(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mHelper.onSaveInstanceState(outState);
        }

        public void setBehindContentView(int id) {
            setBehindContentView(getActivity().getLayoutInflater().inflate(id));
        }

        public void setBehindContentView(View v) {
            setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }

        public void setBehindContentView(View v, LayoutParams params) {
            mHelper.setBehindContentView(v, params);
        }

        public void setContent(int resId) {
            getActivity().setContentView(resId);
        }

        public void setContent(View view) {
            getActivity().setContentView(view);
        }

        public void setContent(View view, LayoutParams params) {
            getActivity().setContentView(view, params);
        }

        @Override
        public boolean setContentView(View view, LayoutParams params) {
            mHelper.registerAboveContentView(view, params);
            return false;
        }

        public void setSlidingActionBarEnabled(boolean b) {
            mHelper.setSlidingActionBarEnabled(b);
        }

        public void showContent() {
            mHelper.showContent();
        }

        public void showMenu() {
            mHelper.showMenu();
        }

        public void showSecondaryMenu() {
            mHelper.showSecondaryMenu();
        }

        public void toggle() {
            mHelper.toggle();
        }
    }

    public static class AddonSlidingMenuF extends IAddonFragment {
        public AddonSlidingMenuF(Fragment fragment) {
            super(fragment);
        }
    }

    @Override
    public AddonSlidingMenuA createAddon(Activity activity) {
        return new AddonSlidingMenuA(activity);
    }

    @Override
    public AddonSlidingMenuF createAddon(Fragment fragment) {
        return new AddonSlidingMenuF(fragment);
    }
}
