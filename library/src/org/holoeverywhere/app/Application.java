
package org.holoeverywhere.app;

import org.holoeverywhere.IHolo;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.LayoutInflater.LayoutInflaterCreator;
import org.holoeverywhere.Setting;
import org.holoeverywhere.SystemServiceManager;
import org.holoeverywhere.SystemServiceManager.SuperSystemService;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.ThemeManager.SuperStartActivity;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

public class Application extends android.app.Application implements
        IHolo, SuperStartActivity, SuperSystemService {
    public static final class Config extends Setting<Config> {
        public static enum PreferenceImpl {
            JSON, XML
        }

        private static final String HOLO_EVERYWHERE_PACKAGE = "org.holoeverywhere";

        private static void onStateChange(Config config) {
            String p = config.holoEverywherePackage.getValue();
            if (p != null && p.length() > 0) {
                config.setWidgetsPackage(p + ".widget");
                config.setPreferencePackage(p + ".preference");
            }
        }

        private final SettingListener<Config> _DEFAULT_SETTINGS_LISTENER = new SettingListener<Config>() {
            @Override
            public void onAttach(Config config) {
                onStateChange(config);
            }

            @Override
            public void onDetach(Config config) {
            }

            @Override
            public void onPropertyChange(Config config, Property<?> property) {
                if (property == config.holoEverywherePackage) {
                    onStateChange(config);
                }
            }

        };
        @SettingProperty(create = true, defaultBoolean = false)
        private BooleanProperty alwaysUseParentTheme;
        @SettingProperty(create = true, defaultBoolean = false)
        private BooleanProperty debugMode;
        @SettingProperty(create = true)
        private BooleanProperty disableContextMenu;
        @SettingProperty(create = true, defaultString = Config.HOLO_EVERYWHERE_PACKAGE)
        private StringProperty holoEverywherePackage;
        @SettingProperty(create = true, defaultEnum = "XML", enumClass = PreferenceImpl.class)
        private EnumProperty<PreferenceImpl> preferenceImpl;
        @SettingProperty(create = true)
        private StringProperty preferencePackage;

        @SettingProperty(create = true)
        private StringProperty widgetsPackage;

        public Config attachDefaultListener() {
            return addListener(_DEFAULT_SETTINGS_LISTENER);
        }

        public Config detachDefaultListener() {
            return removeListener(_DEFAULT_SETTINGS_LISTENER);
        }

        public String getHoloEverywherePackage() {
            return holoEverywherePackage.getValue();
        }

        public PreferenceImpl getPreferenceImpl() {
            return preferenceImpl.getValue();
        }

        public String getPreferencePackage() {
            return preferencePackage.getValue();
        }

        public String getWidgetsPackage() {
            return widgetsPackage.getValue();
        }

        public boolean isAlwaysUseParentTheme() {
            return alwaysUseParentTheme.getValue();
        }

        public boolean isDebugMode() {
            return debugMode.getValue();
        }

        public boolean isDisableContextMenu() {
            return disableContextMenu.getValue();
        }

        /**
         * @deprecated This property always true
         */
        @Deprecated
        public boolean isUseThemeManager() {
            return true;
        }

        @Override
        protected void onInit() {
            attachDefaultListener();
            SystemServiceManager.register(LayoutInflaterCreator.class);
        }

        public Config setAlwaysUseParentTheme(boolean alwaysUseParentTheme) {
            this.alwaysUseParentTheme.setValue(alwaysUseParentTheme);
            return this;
        }

        public Config setDebugMode(boolean debugMode) {
            this.debugMode.setValue(debugMode);
            return this;
        }

        public Config setDisableContextMenu(boolean disableContextMenu) {
            this.disableContextMenu.setValue(disableContextMenu);
            return this;
        }

        public Config setHoloEverywherePackage(String holoEverywherePackage) {
            this.holoEverywherePackage.setValue(holoEverywherePackage);
            return this;
        }

        public Config setPreferenceImpl(PreferenceImpl preferenceImpl) {
            this.preferenceImpl.setValue(preferenceImpl);
            return this;
        }

        public Config setPreferencePackage(String preferencePackage) {
            this.preferencePackage.setValue(preferencePackage);
            return this;
        }

        /**
         * @deprecated This property always true
         */
        @Deprecated
        public Config setUseThemeManager(boolean useThemeManager) {
            if (!useThemeManager) {
                throw new RuntimeException("This property always true");
            }
            return this;
        }

        public Config setWidgetsPackage(String widgetsPackage) {
            this.widgetsPackage.setValue(widgetsPackage);
            return this;
        }
    }

    private static Application lastInstance;

    public static Config config() {
        return Setting.get(Config.class);
    }

    public static Application getLastInstance() {
        return Application.lastInstance;
    }

    public static boolean isDebugMode() {
        return Application.config().isDebugMode();
    }

    public Application() {
        Application.lastInstance = this;
    }

    @Override
    public Config getConfig() {
        return config();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl) {
        return PreferenceManager.getDefaultSharedPreferences(this, impl);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(this);
    }

    @Override
    public SharedPreferences getSharedPreferences(PreferenceImpl impl, String name, int mode) {
        return PreferenceManager.wrap(this, impl, name, mode);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return PreferenceManager.wrap(this, name, mode);
    }

    @Override
    public Application getSupportApplication() {
        return this;
    }

    @Override
    public boolean isABSSupport() {
        return true;
    }

    @Override
    public void onTerminate() {
        LayoutInflater.clearInstances();
        super.onTerminate();
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivities(Intent[] intents, Bundle options) {
        for (Intent intent : intents) {
            startActivity(intent, options);
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        if (config().isAlwaysUseParentTheme()) {
            ThemeManager.startActivity(this, intent, options);
        } else {
            superStartActivity(intent, -1, options);
        }
    }

    public android.content.SharedPreferences superGetSharedPreferences(
            String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public Object superGetSystemService(String name) {
        return super.getSystemService(name);
    }

    @Override
    @SuppressLint("NewApi")
    public void superStartActivity(Intent intent, int requestCode,
            Bundle options) {
        if (VERSION.SDK_INT >= 16) {
            super.startActivity(intent, options);
        } else {
            super.startActivity(intent);
        }
    }
}
