
package org.holoeverywhere;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public abstract class Setting<T extends Setting<T>> {
    public static class BooleanProperty extends Property<Boolean> {
        @Override
        protected void onSetDefaultValue(SettingProperty settingProperty) {
            setValue(settingProperty.defaultBoolean());
        }
    }

    public static class EnumProperty<T extends Enum<T>> extends Property<T> {
        private String defaultValue;

        @SuppressWarnings("unchecked")
        @Override
        protected void onSetDefaultValue(SettingProperty settingProperty) {
            defaultValue = settingProperty.defaultEnum();
            Class<?> t = settingProperty.enumClass();
            Class<T> clazz = t == NullEnum.class ? null : (Class<T>) t;
            if (clazz != null) {
                setEnumClass(clazz);
            }
        }

        @SuppressWarnings("unchecked")
        public void setEnumClass(Class<T> clazz) {
            if (defaultValue != null) {
                try {
                    Method method = clazz.getMethod("valueOf", String.class);
                    method.setAccessible(true);
                    setValue((T) method.invoke(null, defaultValue));
                } catch (Exception e) {
                    Log.w(Setting.TAG, "Error on getting enum value", e);
                }
            }
        }
    }

    public static class IntegerProperty extends Property<Integer> {
        @Override
        protected void onSetDefaultValue(SettingProperty settingProperty) {
            setValue(settingProperty.defaultInt());
        }
    }

    private static enum NullEnum {

    }

    public static class Property<Z> {
        private Setting<?> setting;
        private Z value;
        private boolean wasAttach = false;

        public void attach(Setting<?> setting) {
            if (setting != null) {
                setting.attach(this);
            }
        }

        public void detach() {
            if (setting != null) {
                setting.detach(this);
            }
        }

        public final Setting<?> getSetting() {
            return setting;
        }

        public Z getValue() {
            return value;
        }

        public boolean isValid() {
            return true;
        }

        public final void notifyOnChange() {
            if (setting != null) {
                setting.onPropertyChange(this);
            }
        }

        protected void onAttach(Setting<?> setting) {

        }

        protected void onDetach() {

        }

        protected void onSetDefaultValue(SettingProperty settingProperty) {

        }

        private final void setSetting(Setting<?> setting) {
            this.setting = setting;
            if (setting != null) {
                if (wasAttach) {
                    onDetach();
                }
                onAttach(setting);
                wasAttach = true;
            } else if (wasAttach) {
                onDetach();
                wasAttach = false;
            }
        }

        public void setValue(Z value) {
            if (this.value != value) {
                this.value = value;
                notifyOnChange();
            }
        }
    }

    public static interface SettingListener<T extends Setting<T>> {
        public void onAttach(T setting);

        public void onDetach(T setting);

        public void onPropertyChange(T setting, Property<?> property);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface SettingProperty {
        public boolean create() default false;

        public boolean defaultBoolean() default false;

        public String defaultEnum() default "";

        public int defaultInt() default 0;

        public String defaultString() default "";

        public Class<? extends Enum<?>> enumClass() default NullEnum.class;
    }

    public static class StringProperty extends Property<String> {
        @Override
        protected void onSetDefaultValue(SettingProperty settingProperty) {
            setValue(settingProperty.defaultString());
        }
    }

    private static final Map<Class<? extends Setting<?>>, Setting<?>> settingMap = new HashMap<Class<? extends Setting<?>>, Setting<?>>();

    private static final String TAG = Setting.class.getSimpleName();

    public static <T extends Setting<T>> void addListener(Class<T> clazz,
            SettingListener<T> settingListener) {
        Setting.get(clazz).addListener(settingListener);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Setting<T>> T get(Class<T> clazz) {
        if (!Setting.settingMap.containsKey(clazz)) {
            try {
                T t = clazz.newInstance();
                Setting.settingMap.put(clazz, t);
                t.init();
            } catch (Exception e) {
                Log.e(Setting.TAG, "Error init setting instance", e);
            }
        }
        return (T) Setting.settingMap.get(clazz);
    }

    public static <T extends Setting<T>> void removeListener(Class<T> clazz,
            SettingListener<T> settingListener) {
        if (Setting.settingMap.containsKey(clazz)) {
            Setting.get(clazz).removeListener(settingListener);
        }
    }

    private final List<SettingListener<T>> listeners = new ArrayList<SettingListener<T>>();

    private final List<Property<?>> propertyList = new ArrayList<Property<?>>();

    @SuppressWarnings("unchecked")
    public final T addListener(SettingListener<T> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("SettingListener can't be null");
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            listener.onAttach((T) this);
        } else {
            listeners.remove(listener);
            listeners.add(listener);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T attach(Property<?> property) {
        if (property == null || !property.isValid()) {
            throw new RuntimeException("Property not valid");
        }
        if (property.getSetting() != null && property.getSetting() != this) {
            throw new RuntimeException("Property already attached to "
                    + property.getSetting().getClass().getName());
        }
        property.setSetting(this);
        propertyList.add(property);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T detach(Property<?> property) {
        if (property != null) {
            propertyList.remove(property);
            property.setSetting(null);
        }
        return (T) this;
    }

    private Field[] getFields() {
        try {
            return getClass().getDeclaredFields();
        } catch (Exception e) {
            return getClass().getFields();
        }
    }

    protected final void init() {
        for (Field field : getFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(SettingProperty.class)) {
                    SettingProperty settingProperty = field
                            .getAnnotation(SettingProperty.class);
                    if (settingProperty.create()) {
                        field.set(this, field.getType().newInstance());
                    }
                    Property<?> property = (Property<?>) field.get(this);
                    property.onSetDefaultValue(settingProperty);
                    attach(property);
                }
            } catch (Exception e) {
                Log.e(Setting.TAG, "Error on processing property", e);
            }
        }
        onInit();
    }

    protected void onInit() {

    }

    @SuppressWarnings("unchecked")
    protected void onPropertyChange(Property<?> property) {
        for (SettingListener<T> listener : listeners) {
            try {
                listener.onPropertyChange((T) this, property);
            } catch (RuntimeException e) {
                Log.w(Setting.TAG, "Listener error", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final T removeListener(SettingListener<T> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("SettingListener can't be null");
        }
        if (listeners.contains(listener)) {
            listener.onDetach((T) this);
            listeners.remove(listener);
        }
        return (T) this;
    }
}
