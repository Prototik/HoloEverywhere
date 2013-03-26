
package org.holoeverywhere.slider;

import org.holoeverywhere.slider.SliderView.SliderDrawer;
import org.holoeverywhere.slider.SliderView.TouchMode;

import android.view.View;

public interface ISlider {
    /**
     * Disable any shadow
     */
    public void disableShadow();

    public SliderDrawer getDrawer();

    /**
     * Return left "drag bound" - factor of auto sliding when up pointer
     */
    public int getLeftDragBound();

    public float getLeftTranslateFactor();

    /**
     * Last computed or setted by usere width for left view
     */
    public int getLeftViewWidth();

    /**
     * Progress between -100 and 100, where -100 - left view fully opened, 100 -
     * right, 0 - content on center, no left or right view showed
     */
    public int getProgress();

    /**
     * Return right "drag bound" - factor of auto sliding when up pointer
     */
    public int getRightDragBound();

    public float getRightTranslateFactor();

    /**
     * Last computed or setted by usere width for right view
     */
    public int getRightViewWidth();

    public TouchMode getTouchMode();

    public int getTouchModeLeftMargin();

    public int getTouchModeRightMargin();

    public boolean isBlockLongMove();

    public boolean isContentShowed();

    public boolean isLeftShowed();

    public boolean isOverlayActionBar();

    public boolean isRightShowed();

    public void setBlockLongMove(boolean blockLongMove);

    public void setContentView(View view);

    public void setDragBound(int dragBound);

    public void setDrawer(SliderDrawer drawer);

    public void setLeftDragBound(int leftDragBound);

    public void setLeftTranslateFactor(float leftTranslateFactor);

    public void setLeftView(View view);

    public void setLeftViewWidth(int leftViewWidth);

    public void setOverlayActionBar(boolean overlayActionBar);

    public void setProgress(int progress);

    public void setRightDragBound(int rightDragBound);

    public void setRightTranslateFactor(float rightTranslateFactor);

    public void setRightView(View view);

    public void setRightViewWidth(int rightViewWidth);

    public void setTouchMode(TouchMode touchMode);

    public void setTouchModeLeftMargin(int touchModeLeftMargin);

    public void setTouchModeMargin(int touchModeMargin);

    public void setTouchModeRightMargin(int touchModeRightMargin);

    public void setTranslateFactor(float translateFactor);

    public void showContentDelayed();

    public void showContentView(boolean smooth);

    public void showLeftView(boolean smooth);

    public void showRightView(boolean smooth);

    /**
     * Toggle between left panel and content
     */
    public void toggle();
}
