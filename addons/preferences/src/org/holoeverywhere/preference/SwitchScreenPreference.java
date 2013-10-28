package org.holoeverywhere.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuPresenter;
import android.support.v7.internal.widget.ActionBarView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Switch;

public class SwitchScreenPreference extends PreferenceScreen implements MenuPresenter.Callback {
    private final Listener mListener = new Listener();
    boolean mChecked;
    private boolean mDisableDependentsState;
    private boolean mSendClickAccessibilityEvent;
    private CharSequence mSummaryOff, mSummaryOn;
    private CharSequence mSwitchOff, mSwitchOn;

    public SwitchScreenPreference(Context context) {
        this(context, null);
    }

    public SwitchScreenPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchScreenPreferenceStyle);
    }

    public SwitchScreenPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwoStatePreference,
                defStyle, R.style.Holo_PreferenceSwitchScreen);
        mSummaryOn = a.getText(R.styleable.TwoStatePreference_android_summaryOn);
        mSummaryOff = a.getText(R.styleable.TwoStatePreference_android_summaryOff);
        mDisableDependentsState = a.getBoolean(
                R.styleable.TwoStatePreference_android_disableDependentsState, false);
        a.recycle();

        a = context.obtainStyledAttributes(attrs, R.styleable.SwitchPreference,
                defStyle, R.style.Holo_PreferenceSwitchScreen);
        mSwitchOn = a.getString(R.styleable.SwitchPreference_android_switchTextOn);
        mSwitchOff = a.getString(R.styleable.SwitchPreference_android_switchTextOff);
        a.recycle();
    }

    public boolean getDisableDependentsState() {
        return mDisableDependentsState;
    }

    public void setDisableDependentsState(boolean disableDependentsState) {
        mDisableDependentsState = disableDependentsState;
    }

    public CharSequence getSummaryOff() {
        return mSummaryOff;
    }

    public void setSummaryOff(int summaryResId) {
        setSummaryOff(getContext().getString(summaryResId));
    }

    public void setSummaryOff(CharSequence summary) {
        mSummaryOff = summary;
        if (!isChecked()) {
            notifyChanged();
        }
    }

    public CharSequence getSummaryOn() {
        return mSummaryOn;
    }

    public void setSummaryOn(int summaryResId) {
        setSummaryOn(getContext().getString(summaryResId));
    }

    public void setSummaryOn(CharSequence summary) {
        mSummaryOn = summary;
        if (isChecked()) {
            notifyChanged();
        }
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            persistBoolean(checked);
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setChecked(myState.checked);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.checked = isChecked();
        return myState;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(restoreValue ? getPersistedBoolean(mChecked)
                : (Boolean) defaultValue);
    }

    @Override
    boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem item = menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, "");
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        int theme = ThemeManager.getThemeType(getDialog().getContext());
        if (theme != ThemeManager.LIGHT) {
            theme = ThemeManager.DARK;
        }
        LayoutInflater inflater = LayoutInflater.from(getDialog().getContext(), theme | PreferenceInit.THEME_FLAG);
        View actionView = inflater.inflate(R.layout.preference_widget_switch_screen_bar);
        Switch switchView = (Switch) actionView.findViewById(R.id.switchWidget);
        switchView.setChecked(mChecked);
        switchView.setTextOn(mSwitchOn);
        switchView.setTextOff(mSwitchOff);
        switchView.setOnCheckedChangeListener(mListener);
        MenuItemCompat.setActionView(item, actionView);

        return true;
    }

    @Override
    void onPrepareDialog(Dialog dialog) {
        if (VERSION.SDK_INT < 11) {
            MenuBuilder builder = new MenuBuilder(dialog.getContext());
            onCreateOptionsMenu(builder);
            ActionBarView abv = (ActionBarView) dialog.getWindow().getDecorView().findViewById(R.id.action_bar);
            abv.setMenu(builder, this);
        }
    }

    @SuppressLint("NewApi")
    void sendAccessibilityEvent(View view) {
        try {
            AccessibilityManager accessibilityManager = (AccessibilityManager) getContext()
                    .getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (mSendClickAccessibilityEvent
                    && accessibilityManager.isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
                if (VERSION.SDK_INT >= 14) {
                    view.onInitializeAccessibilityEvent(event);
                }
                view.dispatchPopulateAccessibilityEvent(event);
                accessibilityManager.sendAccessibilityEvent(event);
            }
        } catch (Exception e) {
        }
        mSendClickAccessibilityEvent = false;
    }

    @Override
    public boolean shouldDisableDependents() {
        return mDisableDependentsState ? mChecked : !mChecked
                || super.shouldDisableDependents();
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        return super.getView(convertView, parent);
    }

    void syncSummaryView(View view) {
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        if (summaryView != null) {
            boolean useDefaultSummary = true;
            if (mChecked && mSummaryOn != null) {
                summaryView.setText(mSummaryOn);
                useDefaultSummary = false;
            } else if (!mChecked && mSummaryOff != null) {
                summaryView.setText(mSummaryOff);
                useDefaultSummary = false;
            }
            if (useDefaultSummary) {
                final CharSequence summary = getSummary();
                if (summary != null) {
                    summaryView.setText(summary);
                    useDefaultSummary = false;
                }
            }
            int newVisibility = useDefaultSummary ? View.GONE : View.VISIBLE;
            if (newVisibility != summaryView.getVisibility()) {
                summaryView.setVisibility(newVisibility);
            }
        }
    }

    public CharSequence getSwitchTextOff() {
        return mSwitchOff;
    }

    public void setSwitchTextOff(int resId) {
        setSwitchTextOff(getContext().getText(resId));
    }

    public void setSwitchTextOff(CharSequence offText) {
        mSwitchOff = offText;
        notifyChanged();
    }

    public CharSequence getSwitchTextOn() {
        return mSwitchOn;
    }

    public void setSwitchTextOn(int resId) {
        setSwitchTextOn(getContext().getText(resId));
    }

    public void setSwitchTextOn(CharSequence onText) {
        mSwitchOn = onText;
        notifyChanged();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        View checkableView = view.findViewById(R.id.switchWidget);
        if (checkableView != null && checkableView instanceof Checkable) {
            ((Checkable) checkableView).setChecked(mChecked);
            sendAccessibilityEvent(checkableView);
            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setTextOn(mSwitchOn);
                switchView.setTextOff(mSwitchOff);
                switchView.setOnCheckedChangeListener(mListener);
            }
        }
        syncSummaryView(view);
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {

    }

    @Override
    public boolean onOpenSubMenu(MenuBuilder subMenu) {
        return false;
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean checked;

        public SavedState(Parcel source) {
            super(source);
            checked = source.readInt() == 1;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(checked ? 1 : 0);
        }
    }

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                buttonView.setChecked(!isChecked);
                return;
            }
            setChecked(isChecked);
        }
    }
}
