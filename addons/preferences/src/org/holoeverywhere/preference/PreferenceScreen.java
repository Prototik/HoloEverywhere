
package org.holoeverywhere.preference;

import java.lang.reflect.Method;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.actionbarsherlock.internal.widget.ActionBarView;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window.Callback;

public final class PreferenceScreen extends PreferenceGroup implements
        AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {
    private final class PreferenceDialog extends Dialog implements Callback {
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
        public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
            return onMenuItemSelected(featureId, new MenuItemWrapper(item));
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            if (featureId == Window.FEATURE_OPTIONS_PANEL && item.getItemId() == android.R.id.home
                    && mDialog != null) {
                mDialog.dismiss();
                return true;
            }
            return false;
        }

        private void prepareActionBar() {
            if (VERSION.SDK_INT < 11) {
                ActionBarView actionBarView = (ActionBarView) findViewById(R.id.abs__action_bar);
                if (actionBarView != null) {
                    actionBarView.setWindowCallback(mDialog);
                }
            }
        }

        @Override
        public void setContentView(View view) {
            if (VERSION.SDK_INT >= 11) {
                super.setContentView(view);
            } else {
                FrameLayout content = (FrameLayout) findViewById(R.id.abs__content);
                if (content == null) {
                    View container = getLayoutInflater().inflate(R.layout.abs__screen_action_bar);
                    content = (FrameLayout) container.findViewById(R.id.abs__content);
                    super.setContentView(container);
                }
                content.removeAllViews();
                content.addView(view);
            }
        }

        @Override
        public void setTitle(CharSequence title) {
            super.setTitle(title);
            if (VERSION.SDK_INT < 11) {
                ((ActionBarView) findViewById(R.id.abs__action_bar)).setTitle(title);
            }
        }

        @Override
        public void setTitle(int titleId) {
            setTitle(getContext().getText(titleId));
        }
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

    private PreferenceDialog mDialog;
    private ListView mListView;
    private ListAdapter mRootAdapter;

    public PreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceScreenStyle);
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
    private void showDialog(Bundle state) {
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
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        getPreferenceManager().addPreferencesScreen(dialog);
        dialog.show();
    }
}
