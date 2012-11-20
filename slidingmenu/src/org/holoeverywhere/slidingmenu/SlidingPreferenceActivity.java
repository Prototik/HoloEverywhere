
package org.holoeverywhere.slidingmenu;

import org.holoeverywhere.preference.PreferenceActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

public class SlidingPreferenceActivity extends PreferenceActivity implements SlidingActivityBase {

    static {
        SlidingActivityHelper.init();
    }
    private SlidingActivityHelper mHelper;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#findViewById(int)
     */
    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null) {
            return v;
        }
        return mHelper.findViewById(id);
    }

    /*
     * (non-Javadoc)
     * @see org.holoeverywhere.slidingmenu.SlidingActivityBase#getSlidingMenu()
     */
    @Override
    public SlidingMenu getSlidingMenu() {
        return mHelper.getSlidingMenu();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SlidingActivityHelper(this);
        mHelper.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        listView.setId(android.R.id.list);
        setContentView(listView);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean b = mHelper.onKeyUp(keyCode, event);
        if (b) {
            return b;
        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHelper.onSaveInstanceState(outState);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.holoeverywhere.slidingmenu.SlidingActivityBase#setBehindContentView
     * (int)
     */
    @Override
    public void setBehindContentView(int id) {
        setBehindContentView(getLayoutInflater().inflate(id, null));
    }

    /*
     * (non-Javadoc)
     * @see
     * org.holoeverywhere.slidingmenu.SlidingActivityBase#setBehindContentView
     * (android .view.View)
     */
    @Override
    public void setBehindContentView(View v) {
        setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    /*
     * (non-Javadoc)
     * @see
     * org.holoeverywhere.slidingmenu.SlidingActivityBase#setBehindContentView
     * (android .view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setBehindContentView(View v, LayoutParams params) {
        mHelper.setBehindContentView(v, params);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(int id) {
        setContentView(getLayoutInflater().inflate(id, null));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View)
     */
    @Override
    public void setContentView(View v) {
        setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View,
     * android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View v, LayoutParams params) {
        super.setContentView(v, params);
        mHelper.registerAboveContentView(v, params);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.holoeverywhere.slidingmenu.SlidingActivityBase#setSlidingActionBarEnabled
     * (boolean)
     */
    @Override
    public void setSlidingActionBarEnabled(boolean b) {
        mHelper.setSlidingActionBarEnabled(b);
    }

    /*
     * (non-Javadoc)
     * @see org.holoeverywhere.slidingmenu.SlidingActivityBase#showAbove()
     */
    @Override
    public void showAbove() {
        mHelper.showAbove();
    }

    /*
     * (non-Javadoc)
     * @see org.holoeverywhere.slidingmenu.SlidingActivityBase#showBehind()
     */
    @Override
    public void showBehind() {
        mHelper.showBehind();
    }

    /*
     * (non-Javadoc)
     * @see org.holoeverywhere.slidingmenu.SlidingActivityBase#toggle()
     */
    @Override
    public void toggle() {
        mHelper.toggle();
    }
}
