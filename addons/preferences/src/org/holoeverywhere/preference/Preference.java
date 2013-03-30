
package org.holoeverywhere.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.util.CharSequences;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Preference implements Comparable<Preference>,
        OnDependencyChangeListener {
    public static class BaseSavedState extends AbsSavedState {
        public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.Creator<BaseSavedState>() {
            @Override
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            @Override
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };

        public BaseSavedState(Parcel source) {
            super(source);
        }

        public BaseSavedState(Parcelable superState) {
            super(superState);
        }
    }

    interface OnPreferenceChangeInternalListener {
        void onPreferenceChange(Preference preference);

        void onPreferenceHierarchyChange(Preference preference);
    }

    public interface OnPreferenceChangeListener {
        boolean onPreferenceChange(Preference preference, Object newValue);
    }

    public interface OnPreferenceClickListener {
        boolean onPreferenceClick(Preference preference);
    }

    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;

    static {
        PreferenceInit.init();
    }

    private boolean mBaseMethodCalled;
    private Context mContext;
    private Object mDefaultValue;
    private int mDependencyId;
    private String mDependencyKey;
    private boolean mDependencyMet = true;
    private List<Preference> mDependents;
    private boolean mEnabled = true;
    private Bundle mExtras;
    private String mFragment;
    private boolean mHasSpecifiedLayout = false;
    private Drawable mIcon;
    private int mIconResId;
    private long mId;
    private Intent mIntent;
    private String mKey;
    private int mLayoutResId = R.layout.preference_holo;
    private OnPreferenceChangeInternalListener mListener;
    private OnPreferenceChangeListener mOnChangeListener;
    private OnPreferenceClickListener mOnClickListener;
    private int mOrder = Preference.DEFAULT_ORDER;
    private boolean mPersistent = true;
    private PreferenceManager mPreferenceManager;
    private boolean mRequiresKey;
    private int mResId;
    private boolean mSelectable = true;
    private boolean mShouldDisableView = true;
    private CharSequence mSummary;
    private CharSequence mTitle;
    private int mTitleRes;

    private int mWidgetLayoutResId;

    public Preference(Context context) {
        this(context, null);
    }

    public Preference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public Preference(Context context, AttributeSet attrs, int defStyle) {
        mContext = PreferenceInit.context(context);
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.Preference, defStyle, R.style.Holo_Preference);
        mKey = a.getString(R.styleable.Preference_key);
        setResId(a.getResourceId(R.styleable.Preference_id, 0));
        mIconResId = a.getResourceId(R.styleable.Preference_icon, 0);
        mTitleRes = a.getResourceId(R.styleable.Preference_title, 0);
        mTitle = a.getString(R.styleable.Preference_title);
        mSummary = a.getString(R.styleable.Preference_summary);
        mOrder = a.getInt(R.styleable.Preference_order, mOrder);
        mFragment = a.getString(R.styleable.Preference_fragment);
        mLayoutResId = a.getResourceId(R.styleable.Preference_layout,
                mLayoutResId);
        mWidgetLayoutResId = a.getResourceId(
                R.styleable.Preference_widgetLayout, mWidgetLayoutResId);
        mEnabled = a.getBoolean(R.styleable.Preference_enabled, true);
        mSelectable = a.getBoolean(R.styleable.Preference_selectable, true);
        mPersistent = a.getBoolean(R.styleable.Preference_persistent,
                mPersistent);
        mDependencyKey = a.getString(R.styleable.Preference_dependency);
        mDependencyId = a.getResourceId(R.styleable.Preference_depends, 0);
        mDefaultValue = onGetDefaultValue(a,
                R.styleable.Preference_defaultValue);
        mShouldDisableView = a.getBoolean(
                R.styleable.Preference_shouldDisableView, mShouldDisableView);
        a.recycle();
        if (!getClass().getName().startsWith(PreferenceInit.PACKAGE)) {
            mHasSpecifiedLayout = true;
        }
    }

    protected boolean callChangeListener(Object newValue) {
        return mOnChangeListener == null ? true : mOnChangeListener
                .onPreferenceChange(this, newValue);
    }

    @Override
    public int compareTo(Preference another) {
        if (mOrder != Preference.DEFAULT_ORDER
                || mOrder == Preference.DEFAULT_ORDER
                && another.mOrder != Preference.DEFAULT_ORDER) {
            return mOrder - another.mOrder;
        } else if (mTitle == null) {
            return 1;
        } else if (another.mTitle == null) {
            return -1;
        } else {

            return CharSequences.compareToIgnoreCase(mTitle, another.mTitle);
        }
    }

    void dispatchRestoreInstanceState(Bundle container) {
        if (hasKey()) {
            Parcelable state = container.getParcelable(mKey);
            if (state != null) {
                mBaseMethodCalled = false;
                onRestoreInstanceState(state);
                if (!mBaseMethodCalled) {
                    throw new IllegalStateException(
                            "Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }

    void dispatchSaveInstanceState(Bundle container) {
        if (hasKey()) {
            mBaseMethodCalled = false;
            Parcelable state = onSaveInstanceState();
            if (!mBaseMethodCalled) {
                throw new IllegalStateException(
                        "Derived class did not call super.onSaveInstanceState()");
            }
            if (state != null) {
                container.putParcelable(mKey, state);
            }
        }
    }

    private void dispatchSetInitialValue() {
        final boolean shouldPersist = shouldPersist();
        if (!shouldPersist || !getSharedPreferences().contains(mKey)) {
            if (mDefaultValue != null) {
                onSetInitialValue(false, mDefaultValue);
            }
        } else {
            onSetInitialValue(true, null);
        }
    }

    protected Preference findPreferenceInHierarchy(int id) {
        if (id <= 0 || mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.findPreference(id);
    }

    protected Preference findPreferenceInHierarchy(String key) {
        if (TextUtils.isEmpty(key) || mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.findPreference(key);
    }

    public Context getContext() {
        return mContext;
    }

    public String getDependency() {
        return mDependencyKey;
    }

    public int getDependencyId() {
        return mDependencyId;
    }

    public SharedPreferences.Editor getEditor() {
        if (mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.getEditor();
    }

    public Bundle getExtras() {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        return mExtras;
    }

    StringBuilder getFilterableStringBuilder() {
        StringBuilder sb = new StringBuilder();
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            sb.append(title).append(' ');
        }
        CharSequence summary = getSummary();
        if (!TextUtils.isEmpty(summary)) {
            sb.append(summary).append(' ');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }

    public String getFragment() {
        return mFragment;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    long getId() {
        return mId;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public String getKey() {
        return mKey;
    }

    public int getLayoutResource() {
        return mLayoutResId;
    }

    public OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return mOnChangeListener;
    }

    public OnPreferenceClickListener getOnPreferenceClickListener() {
        return mOnClickListener;
    }

    public int getOrder() {
        return mOrder;
    }

    protected boolean getPersistedBoolean(boolean defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return mPreferenceManager.getSharedPreferences().getBoolean(mKey,
                defaultReturnValue);
    }

    protected float getPersistedFloat(float defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return mPreferenceManager.getSharedPreferences().getFloat(mKey,
                defaultReturnValue);
    }

    protected Set<Float> getPersistedFloatSet(Set<Float> defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getFloatSet(mKey,
                defaultReturnValue);
    }

    protected int getPersistedInt(int defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return mPreferenceManager.getSharedPreferences().getInt(mKey,
                defaultReturnValue);
    }

    protected Set<Integer> getPersistedIntSet(Set<Integer> defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getIntSet(mKey,
                defaultReturnValue);
    }

    protected JSONArray getPersistedJSONArray(JSONArray defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getJSONArray(mKey,
                defaultReturnValue);
    }

    protected JSONObject getPersistedJSONObject(JSONObject defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getJSONObject(mKey,
                defaultReturnValue);
    }

    protected long getPersistedLong(long defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return mPreferenceManager.getSharedPreferences().getLong(mKey,
                defaultReturnValue);
    }

    protected Set<Long> getPersistedLongSet(Set<Long> defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getLongSet(mKey,
                defaultReturnValue);
    }

    protected String getPersistedString(String defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return mPreferenceManager.getSharedPreferences().getString(mKey,
                defaultReturnValue);
    }

    protected Set<String> getPersistedStringSet(Set<String> defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        return mPreferenceManager.getSharedPreferences().getStringSet(mKey,
                defaultReturnValue);
    }

    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    public int getResId() {
        return mResId;
    }

    public SharedPreferences getSharedPreferences() {
        if (mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.getSharedPreferences();
    }

    public boolean getShouldDisableView() {
        return mShouldDisableView;
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public int getTitleRes() {
        return mTitleRes;
    }

    public View getView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = onCreateView(parent);
        }
        onBindView(convertView);
        return convertView;
    }

    public int getWidgetLayoutResource() {
        return mWidgetLayoutResId;
    }

    public boolean hasKey() {
        return !TextUtils.isEmpty(mKey);
    }

    boolean hasSpecifiedLayout() {
        return mHasSpecifiedLayout;
    }

    public boolean isEnabled() {
        return mEnabled && mDependencyMet;
    }

    public boolean isPersistent() {
        return mPersistent;
    }

    public boolean isSelectable() {
        return mSelectable;
    }

    protected void notifyChanged() {
        if (mListener != null) {
            mListener.onPreferenceChange(this);
        }
    }

    public void notifyDependencyChange(boolean disableDependents) {
        final List<Preference> dependents = mDependents;

        if (dependents == null) {
            return;
        }

        final int dependentsCount = dependents.size();
        for (int i = 0; i < dependentsCount; i++) {
            dependents.get(i).onDependencyChanged(this, disableDependents);
        }
    }

    protected void notifyHierarchyChanged() {
        if (mListener != null) {
            mListener.onPreferenceHierarchyChange(this);
        }
    }

    protected void onAttachedToActivity() {
        registerDependency();
    }

    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;

        mId = preferenceManager.getNextId();

        dispatchSetInitialValue();
    }

    protected void onBindView(View view) {
        final TextView titleView = (TextView) view.findViewById(R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }
        final TextView summaryView = (TextView) view.findViewById(R.id.summary);
        if (summaryView != null) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        if (imageView != null) {
            if (mIconResId != 0 || mIcon != null) {
                if (mIcon == null) {
                    mIcon = getContext().getResources().getDrawable(mIconResId);
                }
                if (mIcon != null) {
                    imageView.setImageDrawable(mIcon);
                }
            }
            imageView.setVisibility(mIcon != null ? View.VISIBLE : View.GONE);
        }
        if (mShouldDisableView) {
            setEnabledStateOnViews(view, isEnabled());
        }
    }

    protected void onClick() {
    }

    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View layout = layoutInflater.inflate(mLayoutResId, parent, false);
        final ViewGroup widgetFrame = (ViewGroup) layout
                .findViewById(R.id.widget_frame);
        if (widgetFrame != null) {
            if (mWidgetLayoutResId != 0) {
                layoutInflater.inflate(mWidgetLayoutResId, widgetFrame);
            } else {
                widgetFrame.setVisibility(View.GONE);
            }
        }
        return layout;
    }

    @Override
    public void onDependencyChanged(Preference dependency,
            boolean disableDependent) {
        if (mDependencyMet == disableDependent) {
            mDependencyMet = !disableDependent;
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    protected Object onGetDefaultValue(TypedArray a, int index) {
        return null;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    protected void onPrepareForRemoval() {
        unregisterDependency();
    }

    protected void onRestoreInstanceState(Parcelable state) {
        mBaseMethodCalled = true;
        if (state != AbsSavedState.EMPTY_STATE && state != null) {
            throw new IllegalArgumentException(
                    "Wrong state class -- expecting Preference State");
        }
    }

    protected Parcelable onSaveInstanceState() {
        mBaseMethodCalled = true;
        return AbsSavedState.EMPTY_STATE;
    }

    protected void onSetInitialValue(boolean restorePersistedValue,
            Object defaultValue) {
    }

    public Bundle peekExtras() {
        return mExtras;
    }

    void performClick(PreferenceScreen preferenceScreen) {

        if (!isEnabled()) {
            return;
        }

        onClick();

        if (mOnClickListener != null
                && mOnClickListener.onPreferenceClick(this)) {
            return;
        }

        PreferenceManager preferenceManager = getPreferenceManager();
        if (preferenceManager != null) {
            PreferenceManager.OnPreferenceTreeClickListener listener = preferenceManager
                    .getOnPreferenceTreeClickListener();
            if (preferenceScreen != null && listener != null
                    && listener.onPreferenceTreeClick(preferenceScreen, this)) {
                return;
            }
        }

        if (mIntent != null) {
            Context context = getContext();
            context.startActivity(mIntent);
        }
    }

    protected boolean persistBoolean(boolean value) {
        if (shouldPersist()) {
            if (value == getPersistedBoolean(!value)) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putBoolean(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistFloat(float value) {
        if (shouldPersist()) {
            if (value == getPersistedFloat(Float.NaN)) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putFloat(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistFloatSet(Set<Float> values) {
        if (shouldPersist()) {
            if (values.equals(getPersistedFloatSet(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putFloatSet(mKey, values);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistInt(int value) {
        if (shouldPersist()) {
            if (value == getPersistedInt(~value)) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putInt(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistIntSet(Set<Integer> values) {
        if (shouldPersist()) {
            if (values.equals(getPersistedIntSet(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putIntSet(mKey, values);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistJSONArray(JSONArray value) {
        if (shouldPersist()) {
            if (value.equals(getPersistedJSONArray(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putJSONArray(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistJSONObject(JSONObject value) {
        if (shouldPersist()) {
            if (value.equals(getPersistedJSONObject(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putJSONObject(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistLong(long value) {
        if (shouldPersist()) {
            if (value == getPersistedLong(~value)) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putLong(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistLongSet(Set<Long> values) {
        if (shouldPersist()) {
            if (values.equals(getPersistedLongSet(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putLongSet(mKey, values);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistString(String value) {
        if (shouldPersist()) {
            // Shouldn't store null
            if (value == getPersistedString(null)) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putString(mKey, value);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    protected boolean persistStringSet(Set<String> values) {
        if (shouldPersist()) {
            if (values.equals(getPersistedStringSet(null))) {
                return true;
            }
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putStringSet(mKey, values);
            tryCommit(editor);
            return true;
        }
        return false;
    }

    private void registerDependency() {
        if (!TextUtils.isEmpty(mDependencyKey)) {
            Preference preference = findPreferenceInHierarchy(mDependencyKey);
            if (preference != null) {
                preference.registerDependent(this);
            } else {
                throw new IllegalStateException("Dependency \"" + mDependencyKey
                        + "\" not found for preference \"" + mKey + "\" (title: \""
                        + mTitle + "\"");
            }
        }
        if (mDependencyId > 0) {
            Preference preference = findPreferenceInHierarchy(mDependencyId);
            if (preference != null) {
                preference.registerDependent(this);
            } else {
                throw new IllegalStateException("Dependency \"" + mDependencyKey
                        + "\" not found for preference \"" + mKey + "\" (title: \""
                        + mTitle + "\"");
            }
        }
    }

    private void registerDependent(Preference dependent) {
        if (mDependents == null) {
            mDependents = new ArrayList<Preference>();
        }

        mDependents.add(dependent);

        dependent.onDependencyChanged(this, shouldDisableDependents());
    }

    void requireKey() {
        if (mKey == null) {
            throw new IllegalStateException(
                    "Preference does not have a key assigned.");
        }

        mRequiresKey = true;
    }

    public void restoreHierarchyState(Bundle container) {
        dispatchRestoreInstanceState(container);
    }

    public void saveHierarchyState(Bundle container) {
        dispatchSaveInstanceState(container);
    }

    public void setDefaultValue(Object defaultValue) {
        mDefaultValue = defaultValue;
    }

    public void setDependency(int dependencyId) {
        unregisterDependency();
        mDependencyKey = null;
        mDependencyId = dependencyId;
        registerDependency();
    }

    public void setDependency(String dependencyKey) {
        unregisterDependency();
        mDependencyKey = dependencyKey;
        mDependencyId = 0;
        registerDependency();
    }

    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    private void setEnabledStateOnViews(View v, boolean enabled) {
        v.setEnabled(enabled);

        if (v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                setEnabledStateOnViews(vg.getChildAt(i), enabled);
            }
        }
    }

    public void setFragment(String fragment) {
        mFragment = fragment;
    }

    public void setIcon(Drawable icon) {
        if (icon == null && mIcon != null || icon != null && mIcon != icon) {
            mIcon = icon;

            notifyChanged();
        }
    }

    public void setIcon(int iconResId) {
        mIconResId = iconResId;
        setIcon(mContext.getResources().getDrawable(iconResId));
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public void setKey(String key) {
        mKey = key;

        if (mRequiresKey && !hasKey()) {
            requireKey();
        }
    }

    public void setLayoutResource(int layoutResId) {
        if (layoutResId != mLayoutResId) {
            // Layout changed
            mHasSpecifiedLayout = true;
        }

        mLayoutResId = layoutResId;
    }

    final void setOnPreferenceChangeInternalListener(
            OnPreferenceChangeInternalListener listener) {
        mListener = listener;
    }

    public void setOnPreferenceChangeListener(
            OnPreferenceChangeListener onPreferenceChangeListener) {
        mOnChangeListener = onPreferenceChangeListener;
    }

    public void setOnPreferenceClickListener(
            OnPreferenceClickListener onPreferenceClickListener) {
        mOnClickListener = onPreferenceClickListener;
    }

    public void setOrder(int order) {
        if (order != mOrder) {
            mOrder = order;

            // Reorder the list
            notifyHierarchyChanged();
        }
    }

    public void setPersistent(boolean persistent) {
        mPersistent = persistent;
    }

    public void setResId(int resId) {
        mResId = resId;
        if (TextUtils.isEmpty(mKey) && resId > 0) {
            mKey = PreferenceManager.makeNameById(resId);
        }
    }

    public void setSelectable(boolean selectable) {
        if (mSelectable != selectable) {
            mSelectable = selectable;
            notifyChanged();
        }
    }

    public void setShouldDisableView(boolean shouldDisableView) {
        mShouldDisableView = shouldDisableView;
        notifyChanged();
    }

    public void setSummary(CharSequence summary) {
        if (summary == null && mSummary != null || summary != null
                && !summary.equals(mSummary)) {
            mSummary = summary;
            notifyChanged();
        }
    }

    public void setSummary(int summaryResId) {
        setSummary(mContext.getString(summaryResId));
    }

    public void setTitle(CharSequence title) {
        if (title == null && mTitle != null || title != null
                && !title.equals(mTitle)) {
            mTitleRes = 0;
            mTitle = title;
            notifyChanged();
        }
    }

    public void setTitle(int titleResId) {
        setTitle(mContext.getString(titleResId));
        mTitleRes = titleResId;
    }

    public void setWidgetLayoutResource(int widgetLayoutResId) {
        if (widgetLayoutResId != mWidgetLayoutResId) {
            mHasSpecifiedLayout = true;
        }
        mWidgetLayoutResId = widgetLayoutResId;
    }

    public boolean shouldCommit() {
        if (mPreferenceManager == null) {
            return false;
        }

        return mPreferenceManager.shouldCommit();
    }

    public boolean shouldDisableDependents() {
        return !isEnabled();
    }

    protected boolean shouldPersist() {
        return mPreferenceManager != null && isPersistent() && hasKey();
    }

    @Override
    public String toString() {
        return getFilterableStringBuilder().toString();
    }

    private void tryCommit(SharedPreferences.Editor editor) {
        if (mPreferenceManager.shouldCommit()) {
            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }
        }
    }

    private void unregisterDependency() {
        if (mDependencyKey != null) {
            final Preference oldDependency = findPreferenceInHierarchy(mDependencyKey);
            if (oldDependency != null) {
                oldDependency.unregisterDependent(this);
            }
        }
        if (mDependencyId > 0) {
            final Preference oldDependency = findPreferenceInHierarchy(mDependencyId);
            if (oldDependency != null) {
                oldDependency.unregisterDependent(this);
            }
        }
    }

    private void unregisterDependent(Preference dependent) {
        if (mDependents != null) {
            mDependents.remove(dependent);
        }
    }
}
