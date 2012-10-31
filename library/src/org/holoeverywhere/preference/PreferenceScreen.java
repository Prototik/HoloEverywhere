
package org.holoeverywhere.preference;

import java.lang.reflect.Method;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.actionbarsherlock.internal.widget.ActionBarView;

public final class PreferenceScreen extends PreferenceGroup implements
        AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {
    private static class SavedState extends BaseSavedState {
        Bundle dialogBundle;
        boolean isDialogShowing;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }
    }

    private Dialog mDialog;
    private ListView mListView;
    private ListAdapter mRootAdapter;

    private final String TAG = getClass().getSimpleName();

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
            Class<?> clazz = context.getClass();
            while (clazz != Context.class) {
                clazz = clazz.getSuperclass();
            }
            Method method = clazz.getDeclaredMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed getting context theme", e);
            return R.style.Holo_Theme_Sherlock_NoActionBar;
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
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final Dialog dialog = mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    private void showDialog(Bundle state) {
        Context context = getContext();
        if (mListView != null) {
            mListView.setAdapter(null);
        }
        final int theme = getThemeResId(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View childPrefScreen = inflater.inflate(
                R.layout.preference_list_fragment, null);
        mListView = (ListView) childPrefScreen.findViewById(android.R.id.list);
        bind(mListView);
        final CharSequence title = getTitle();
        final boolean titleEmpty = TextUtils.isEmpty(title);
        Dialog dialog = mDialog = new Dialog(context, theme);
        if (titleEmpty) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle(title);
        }
        if (VERSION.SDK_INT >= 11 || titleEmpty) {
            dialog.setContentView(childPrefScreen);
        } else {
            View container = inflater.inflate(R.layout.abs__screen_action_bar);
            ((ActionBarView) container.findViewById(R.id.abs__action_bar))
                    .setTitle(title);
            ((FrameLayout) container.findViewById(R.id.abs__content))
                    .addView(childPrefScreen);
            dialog.setContentView(container);
        }
        dialog.setOnDismissListener(this);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        getPreferenceManager().addPreferencesScreen(dialog);
        dialog.show();
    }
}
