
package org.holoeverywhere.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.internal.widget.ActionBarView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ListView;

import java.lang.reflect.Method;

public class PreferenceScreen extends PreferenceGroup implements
        AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {
    private PreferenceDialog mDialog;
    private ListView mListView;
    private ListAdapter mRootAdapter;

    public PreferenceScreen(Context context) {
        this(context, null);
    }

    public PreferenceScreen(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceScreenStyle);
    }

    public PreferenceScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void bind(ListView listView) {
        listView.setOnItemClickListener(this);
        listView.setAdapter(getRootAdapter());
        onAttachedToActivity();
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public ListAdapter getRootAdapter() {
        if (mRootAdapter == null) {
            mRootAdapter = onCreateRootAdapter();
        }

        return mRootAdapter;
    }

    protected int getThemeResId(Context context) {
        try {
            if (context instanceof Activity) {
                int t = ((Activity) context).getLastThemeResourceId();
                if (t > 0) {
                    return t;
                }
            }
            Method method = Context.class.getDeclaredMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
            return R.style.Holo_Theme_NoActionBar;
        }
    }

    @Override
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }

    @Override
    protected void onClick() {
        if (getIntent() != null || getFragment() != null
                || getPreferenceCount() == 0) {
            return;
        }
        showDialog(null);
    }

    protected ListAdapter onCreateRootAdapter() {
        return new PreferenceGroupAdapter(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mDialog = null;
        getPreferenceManager().removePreferencesScreen(dialog);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent instanceof ListView) {
            position -= ((ListView) parent).getHeaderViewsCount();
        }
        Object item = getRootAdapter().getItem(position);
        if (!(item instanceof Preference)) {
            return;
        }

        final Preference preference = (Preference) item;
        preference.performClick(this);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState myState = new SavedState(super.onSaveInstanceState());
        if (mDialog != null) {
            myState.isShowing = true;
            myState.dialogBundle = mDialog.onSaveInstanceState();
        } else {
            myState.isShowing = false;
        }
        return myState;
    }

    @SuppressLint("NewApi")
    protected void showDialog(Bundle state) {
        Context preferenceContext = getContext();
        Context context = PreferenceInit.unwrap(getContext());
        final int contextTheme = getThemeResId(context);
        if (mListView != null) {
            mListView.setAdapter(null);
        }
        View childPrefScreen = LayoutInflater.inflate(preferenceContext,
                R.layout.preference_list_fragment);
        mListView = (ListView) childPrefScreen.findViewById(android.R.id.list);
        bind(mListView);
        final CharSequence title = getTitle();
        final boolean titleEmpty = TextUtils.isEmpty(title);
        Dialog dialog = mDialog = new PreferenceDialog(context, contextTheme);
        if (titleEmpty) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            if (VERSION.SDK_INT >= 11) {
                dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
            }
            dialog.setContentView(childPrefScreen);
            dialog.setTitle(title);
        }
        dialog.setOnDismissListener(this);
        onPrepareDialog(dialog);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        getPreferenceManager().addPreferencesScreen(dialog);
        dialog.show();
    }

    void onPrepareDialog(Dialog dialog) {
    }

    boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle dialogBundle;
        boolean isShowing;

        public SavedState(Parcel source) {
            super(source);
            isShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }
    }

    private final class PreferenceDialog extends Dialog {
        public PreferenceDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            prepareActionBar();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            prepareActionBar();
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL) {
                return PreferenceScreen.this.onCreateOptionsMenu(menu);
            }
            return super.onCreatePanelMenu(featureId, menu);
        }

        @Override
        public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL) {
                if (item.getItemId() == android.R.id.home) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    return true;
                } else {
                    return PreferenceScreen.this.onOptionsItemSelected(item);
                }
            }
            return super.onMenuItemSelected(featureId, item);
        }

        private void prepareActionBar() {
            if (VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ActionBarView actionBarView = (ActionBarView) findViewById(R.id.action_bar);
                if (actionBarView != null) {
                    actionBarView.setWindowCallback(mDialog);
                }
            }
        }

        @Override
        public void setContentView(View view) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                super.setContentView(view);
            } else {
                FrameLayout content = (FrameLayout) findViewById(R.id.action_bar_activity_content);
                if (content == null) {
                    View container = getLayoutInflater().inflate(R.layout.abc_action_bar_decor);
                    content = (FrameLayout) container.findViewById(R.id.action_bar_activity_content);
                    super.setContentView(container);
                }
                content.removeAllViews();
                content.addView(view);
            }
        }

        @Override
        public void setTitle(CharSequence title) {
            super.setTitle(title);
            if (VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ((ActionBarView) findViewById(R.id.action_bar)).setTitle(title);
            }
        }

        @Override
        public void setTitle(int titleId) {
            setTitle(getContext().getText(titleId));
        }
    }
}
