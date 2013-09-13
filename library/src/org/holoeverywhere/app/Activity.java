
package org.holoeverywhere.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app._HoloActivity;
import android.view.KeyEvent;
import android.view.View;

import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.addon.IAddon;
import org.holoeverywhere.addon.IAddonActivity;
import org.holoeverywhere.addon.IAddonBasicAttacher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

public abstract class Activity extends _HoloActivity {
    public static final String ADDON_ROBOGUICE = "Roboguice";
    public static final String ADDON_SLIDER = "Slider";
    public static final String ADDON_TABBER = "Tabber";
    private final IAddonBasicAttacher<IAddonActivity, Activity> mAttacher =
            new IAddonBasicAttacher<IAddonActivity, Activity>(this);
    private final FindViewAction mFindViewAction = new FindViewAction();
    private final KeyEventAction mKeyEventAction = new KeyEventAction();
    private boolean mCreatedByThemeManager = false;

    @Override
    public <T extends IAddonActivity> T addon(Class<? extends IAddon> clazz) {
        return mAttacher.addon(clazz);
    }

    @Override
    public void addon(Collection<Class<? extends IAddon>> classes) {
        mAttacher.addon(classes);
    }

    @Override
    public <T extends IAddonActivity> T addon(String classname) {
        return mAttacher.addon(classname);
    }

    @Override
    public void closeOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.closeOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.closeOptionsMenu();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mKeyEventAction.mEvent = event;
        return performAddonAction(mKeyEventAction);
    }

    @Override
    public View findViewById(int id) {
        mFindViewAction.mView = null;
        mFindViewAction.mId = id;
        performAddonAction(mFindViewAction);
        return mFindViewAction.mView;
    }

    public Bundle instanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return savedInstanceState;
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(ThemeManager.KEY_INSTANCE_STATE)) {
            return extras.getBundle(ThemeManager.KEY_INSTANCE_STATE);
        }
        return null;
    }

    @Override
    public boolean isAddonAttached(Class<? extends IAddon> clazz) {
        return mAttacher.isAddonAttached(clazz);
    }

    public boolean isCreatedByThemeManager() {
        return mCreatedByThemeManager;
    }

    @Override
    public void lockAttaching() {
        mAttacher.lockAttaching();
    }

    @Override
    public Collection<Class<? extends IAddon>> obtainAddonsList() {
        return mAttacher.obtainAddonsList();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onActivityResult(requestCode, resultCode, data);
            }
        });
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        final Configuration oldConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onConfigurationChanged(oldConfig, newConfig);
            }
        });
    }

    @Override
    public void onSupportContentChanged() {
        super.onSupportContentChanged();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onContentChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Bundle state = instanceState(savedInstanceState);
        mCreatedByThemeManager = getIntent().getBooleanExtra(
                ThemeManager.KEY_CREATED_BY_THEME_MANAGER, false);
        mAttacher.inhert(getSupportApplication());
        forceInit(state);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPreCreate(state);
            }
        });
        super.onCreate(state);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onCreate(state);
            }
        });
    }

    @Override
    protected void onDestroy() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onDestroy();
            }
        });
        super.onDestroy();
    }

    @Override
    public boolean onHomePressed() {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onHomePressed();
            }

            @Override
            public boolean post() {
                return Activity.super.onHomePressed();
            }
        });
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onNewIntent(intent);
            }
        });
    }

    @Override
    public void onPanelClosed(final int featureId, final android.view.Menu menu) {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPanelClosed(featureId, menu);
            }
        });
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onPause() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPause();
            }
        });
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle sSavedInstanceState) {
        final Bundle savedInstanceState = instanceState(sSavedInstanceState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPostCreate(savedInstanceState);
            }
        });
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostInit(Holo config, Bundle savedInstanceState) {
        lockAttaching();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onPostResume();
            }
        });
    }

    @Override
    protected void onPreInit(Holo config, Bundle savedInstanceState) {
        if (getClass().isAnnotationPresent(Addons.class)) {
            for (String addon : getClass().getAnnotation(Addons.class).value()) {
                if (ADDON_SLIDER.equals(addon)) {
                    config.requireSlider = true;
                } else if (ADDON_ROBOGUICE.equals(addon)) {
                    config.requireRoboguice = true;
                } else if (ADDON_TABBER.equals(addon)) {
                    config.requireTabber = true;
                } else {
                    addon(addon);
                }
            }
        }
    }

    @Override
    public boolean onPreparePanel(final int featureId, final View view,
                                  final android.view.Menu menu) {
        return performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.onPreparePanel(featureId, view, menu);
            }

            @Override
            public boolean post() {
                return Activity.super.onPreparePanel(featureId, view, menu);
            }
        });
    }

    @Override
    protected void onRestart() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onRestart();
            }
        });
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onResume();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onSaveInstanceState(outState);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onStart();
            }
        });
    }

    @Override
    protected void onStop() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onStop();
            }
        });
        super.onStop();
    }

    @Override
    protected void onTitleChanged(final CharSequence title, final int color) {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public void justAction(IAddonActivity addon) {
                addon.onTitleChanged(title, color);
            }
        });
        super.onTitleChanged(title, color);
    }

    @Override
    public void openOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.openOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.openOptionsMenu();
            }
        });
    }

    @Override
    public boolean performAddonAction(AddonCallback<IAddonActivity> callback) {
        return mAttacher.performAddonAction(callback);
    }

    @Override
    public void requestWindowFeature(long featureIdLong) {
        super.requestWindowFeature(featureIdLong);
        final int featureId = (int) featureIdLong;
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.requestWindowFeature(featureId);
            }

            @Override
            public void justPost() {
                requestWindowFeature(featureId);
            }
        });
    }

    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle(getClassLoader());
        onSaveInstanceState(bundle);
        return bundle.size() > 0 ? bundle : null;
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        performAddonAction(new AddonCallback<IAddonActivity>() {
            @Override
            public boolean action(IAddonActivity addon) {
                return addon.invalidateOptionsMenu();
            }

            @Override
            public void justPost() {
                Activity.super.supportInvalidateOptionsMenu();
            }
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface Addons {
        public String[] value();
    }

    private final class FindViewAction extends AddonCallback<IAddonActivity> {
        private int mId;
        private View mView;

        @Override
        public boolean action(IAddonActivity addon) {
            return (mView = addon.findViewById(mId)) != null;
        }

        @Override
        public boolean post() {
            return (mView = getWindow().findViewById(mId)) != null;
        }
    }

    private final class KeyEventAction extends AddonCallback<IAddonActivity> {
        private KeyEvent mEvent;

        @Override
        public boolean action(IAddonActivity addon) {
            return addon.dispatchKeyEvent(mEvent);
        }

        @Override
        public boolean post() {
            return Activity.super.dispatchKeyEvent(mEvent);
        }
    }
}
