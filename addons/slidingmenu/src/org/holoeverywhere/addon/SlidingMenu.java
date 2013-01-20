
package org.holoeverywhere.addon;

import org.holoeverywhere.addon.SlidingMenu.SlidingMenuA;
import org.holoeverywhere.addon.SlidingMenu.SlidingMenuF;
import org.holoeverywhere.addons.IAddon;
import org.holoeverywhere.addons.IAddonActivity;
import org.holoeverywhere.addons.IAddonFragment;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.slidingmenu.lib.app.SlidingActivityHelper;

public class SlidingMenu extends IAddon<SlidingMenuA, SlidingMenuF> {
    public static class SlidingMenuA extends IAddonActivity {
        public SlidingMenuA(Activity activity) {
            super(activity);
        }

        private SlidingActivityHelper mHelper;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mHelper = new SlidingActivityHelper(getActivity());
            mHelper.onCreate(savedInstanceState);
            mHelper.setSlidingActionBarEnabled(false);
        }

        @Override
        public View findViewById(int id) {
            return mHelper != null ? mHelper.findViewById(id) : null;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mHelper.onSaveInstanceState(outState);
        }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            mHelper.onPostCreate(savedInstanceState);
        }

        @Override
        public boolean setContentView(View view, LayoutParams params) {
            mHelper.registerAboveContentView(view, params);
            return false;
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

        public com.slidingmenu.lib.SlidingMenu getSlidingMenu() {
            return mHelper.getSlidingMenu();
        }

        public void toggle() {
            mHelper.toggle();
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

        public void setSlidingActionBarEnabled(boolean b) {
            // mHelper.setSlidingActionBarEnabled(b);
            // Sliding with ActionBar temporary not working
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            return mHelper.onKeyUp(keyCode, event);
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
