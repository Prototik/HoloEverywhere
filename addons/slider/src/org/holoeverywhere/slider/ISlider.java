
package org.holoeverywhere.slider;

import org.holoeverywhere.slider.SliderView.OnSlideListener;
import org.holoeverywhere.slider.SliderView.SliderDrawer;
import org.holoeverywhere.slider.SliderView.TouchMode;

import android.view.View;

public interface ISlider {
    /**
     * Disable any shadow
     */
    public void disableShadow();

    /**
     * Current content view associated with this slider. May contain *real*
     * content view with id @id/contentView and/or left/right sides with id
     * 
     * @id/leftView and @id/rightView
     */
    public View getContentView();

    /**
     * Drawer for views. Can be used for styling drag process. By default using
     * built-in drawer {@link SliderView.DefaultSlidingDrawer} which draw
     * shadows and translate view presentation when drag
     */
    public SliderDrawer getDrawer();

    /**
     * Return left "drag bound" - factor of auto sliding when up pointer
     */
    public int getLeftDragBound();

    /**
     * Translate factor
     * 
     * @see {@link #getDrawer()}
     */
    public float getLeftTranslateFactor();

    /**
     * Current left view
     */
    public View getLeftView();

    public int getLeftViewShadowColor();

    /**
     * Last computed or setted by user width for left view
     */
    public int getLeftViewWidth();

    /**
     * Listener for switch-pane events
     */
    public OnSlideListener getOnSlideListener();

    /**
     * Progress between -100 and 100, where -100 - left view fully opened, 100 -
     * right, 0 - content on center, no left or right view showed
     */
    public int getProgress();

    /**
     * Return right "drag bound" - factor of auto sliding when up pointer
     */
    public int getRightDragBound();

    /**
     * Translate factor
     * 
     * @see {@link #getDrawer()}
     */
    public float getRightTranslateFactor();

    /**
     * Current right view
     */
    public View getRightView();

    public int getRightViewShadowColor();

    /**
     * Last computed or setted by user width for right view
     */
    public int getRightViewWidth();

    /**
     * Return current {@link TouchMode}
     */
    public TouchMode getTouchMode();

    /**
     * Margin for intercept touches in left side. Works only when current
     * touchmode is {@link TouchMode#Left} or {@link TouchMode#LeftRight}
     */
    public int getTouchModeLeftMargin();

    /**
     * Margin for intercept touches in right side. Works only when current
     * touchmode is {@link TouchMode#Right} or {@link TouchMode#LeftRight}
     */
    public int getTouchModeRightMargin();

    /**
     * Block "long move" from side to side. Drag will be stoped when content
     * view show.
     */
    public boolean isBlockLongMove();

    /**
     * Return true when now showed content view
     */
    public boolean isContentShowed();

    /**
     * Return true when now showed left view
     */
    public boolean isLeftShowed();

    /**
     * Overlay action bar, slider will be ignore action bar
     */
    public boolean isOverlayActionBar();

    /**
     * Return true when now showed right view
     */
    public boolean isRightShowed();

    /**
     * Block "long move" from side to side. Drag will be stoped when content
     * view show.
     */
    public void setBlockLongMove(boolean blockLongMove);

    /**
     * Set left view via layout resource
     * 
     * @see #getContentView()
     */
    public void setContentView(int layoutId);

    /**
     * Set content view. Slider wouldn't be search leftView/rightView into this
     * view and set id contentView on this.
     * 
     * @see #getContentView()
     */
    public void setContentView(View view);

    /**
     * Set drag bound for double side
     */
    public void setDragBound(int dragBound);

    /**
     * @see #getDrawer()
     */
    public void setDrawer(SliderDrawer drawer);

    /**
     * @see #getLeftDragBound()
     */
    public void setLeftDragBound(int leftDragBound);

    /**
     * @see #getLeftTranslateFactor()
     */
    public void setLeftTranslateFactor(float leftTranslateFactor);

    /**
     * Set left view via layout resource
     * 
     * @see #getLeftView()
     */
    public void setLeftView(int layoutId);

    /**
     * Set left view
     * 
     * @see #getLeftView()
     */
    public void setLeftView(View view);

    public void setLeftViewShadowColor(int leftViewShadowColor);

    /**
     * Hardly set left view width in pixels. If value negative - width will be
     * computed automatically
     */
    public void setLeftViewWidth(int leftViewWidth);

    /**
     * @see #getOnSlideListener()
     */
    public void setOnSlideListener(OnSlideListener onSlideListener);

    /**
     * @see #isOverlayActionBar()
     */
    public void setOverlayActionBar(boolean overlayActionBar);

    /**
     * @see #getProgress()
     */
    public void setProgress(int progress);

    /**
     * @see #getRightDragBound()
     */
    public void setRightDragBound(int rightDragBound);

    /**
     * @see #getRightTranslateFactor()
     */
    public void setRightTranslateFactor(float rightTranslateFactor);

    /**
     * Set left view via layout resource
     * 
     * @see #getRightView()
     */
    public void setRightView(int layoutId);

    /**
     * Set right view
     * 
     * @see #getRightView()
     */
    public void setRightView(View view);

    public void setRightViewShadowColor(int rightViewShadowColor);

    /**
     * Hardly set right view width in pixels. If value negative - width will be
     * computed automatically
     */
    public void setRightViewWidth(int rightViewWidth);

    public void setShadowColor(int shadowColor);

    /**
     * @see #getTouchMode()
     */
    public void setTouchMode(TouchMode touchMode);

    /**
     * @see #getTouchModeLeftMargin()
     */
    public void setTouchModeLeftMargin(int touchModeLeftMargin);

    /**
     * Set touchmode margin for double side
     * 
     * @see #setTouchModeLeftMargin(int)
     * @see #setTouchModeRightMargin(int)
     */
    public void setTouchModeMargin(int touchModeMargin);

    /**
     * @see #getTouchModeRightMargin()
     */
    public void setTouchModeRightMargin(int touchModeRightMargin);

    /**
     * Set translate factor for double side
     * 
     * @see #setLeftTranslateFactor(float)
     * @see #setRightTranslateFactor(float)
     */
    public void setTranslateFactor(float translateFactor);

    /**
     * Show content with small delay, needed for smooth animations when content
     * changed
     */
    public void showContentDelayed();

    /**
     * Show content view
     * 
     * @param smooth Enable animation
     */
    public void showContentView(boolean smooth);

    /**
     * Show left view
     * 
     * @param smooth Enable animation
     */

    public void showLeftView(boolean smooth);

    /**
     * Show right view
     * 
     * @param smooth Enable animation
     */
    public void showRightView(boolean smooth);

    /**
     * Toggle between left and content views
     * 
     * @see #showContentView(boolean)
     * @see #showLeftView(boolean)
     */
    public void toggle();
}
