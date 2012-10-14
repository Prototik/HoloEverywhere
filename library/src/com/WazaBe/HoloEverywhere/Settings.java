package com.WazaBe.HoloEverywhere;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public final class Settings {
	public static class BooleanProperty extends Property<Boolean> {

	}

	public static class EnumProperty<T extends Enum<T>> extends Property<T> {

	}

	public static class IntegerProperty extends Property<Integer> {

	}

	public static class Property<Z> {
		private Setting<?> setting;

		private Z value;
		private boolean wasAttach = false;

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

	public static abstract class Setting<T extends Setting<T>> {
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		public static @interface SettingProperty {
			public boolean create() default false;
		}

		private final List<SettingListener> listeners = new ArrayList<SettingListener>();

		private final List<Property<?>> propertyList = new ArrayList<Property<?>>();

		@SuppressWarnings("unchecked")
		public final T addListener(SettingListener listener) {
			if (listener == null) {
				throw new IllegalArgumentException(
						"SettingListener can't be null");
			}
			if (!listeners.contains(listener)) {
				listeners.add(listener);
				listener.onAttach(this);
			} else {
				listeners.remove(listener);
				listeners.add(listener);
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

		private void init() {
			for (Field field : getFields()) {
				try {
					field.setAccessible(true);
					if (field.isAnnotationPresent(SettingProperty.class)) {
						SettingProperty settingProperty = field
								.getAnnotation(SettingProperty.class);
						if (settingProperty.create()) {
							field.set(this, field.getType().newInstance());
						}
						register((Property<?>) field.get(this));
					}
				} catch (Exception e) {
					Log.e(TAG, "Error on processing property", e);
				}
			}
			onInit();
		}

		protected void onInit() {

		}

		protected void onPropertyChange(Property<?> property) {
			for (SettingListener listener : listeners) {
				try {
					listener.onPropertyChange(this, property);
				} catch (RuntimeException e) {
					Log.w(TAG, "Listener error", e);
				}
			}
		}

		@SuppressWarnings("unchecked")
		public final T register(Property<?> property) {
			Log.v(TAG, "Register " + property);
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
		public final T removeListener(SettingListener listener) {
			if (listener == null) {
				throw new IllegalArgumentException(
						"SettingListener can't be null");
			}
			if (listeners.contains(listener)) {
				listener.onDetach(this);
				listeners.remove(listener);
			}
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public final T unregister(Property<?> property) {
			if (property != null) {
				propertyList.remove(property);
				property.setSetting(null);
			}
			return (T) this;
		}
	}

	public static interface SettingListener {
		public void onAttach(Setting<?> setting);

		public void onDetach(Setting<?> setting);

		public void onPropertyChange(Setting<?> setting, Property<?> property);
	}

	public static class StringProperty extends Property<String> {

	}

	private static final Map<Class<? extends Setting<?>>, Setting<?>> settingMap = new HashMap<Class<? extends Setting<?>>, Setting<?>>();
	private static final String TAG = Setting.class.getSimpleName();

	@SuppressWarnings("unchecked")
	public static <T extends Setting<T>> T get(Class<T> clazz) {
		if (!settingMap.containsKey(clazz)) {
			try {
				T t = clazz.newInstance();
				settingMap.put(clazz, t);
				t.init();
			} catch (Exception e) {
				Log.e(TAG, "Error init setting instance", e);
			}
		}
		return (T) settingMap.get(clazz);
	}

	public <T extends Setting<T>> void addListener(Class<T> clazz,
			SettingListener settingListener) {
		get(clazz).addListener(settingListener);
	}

	public <T extends Setting<T>> void removeListener(Class<T> clazz,
			SettingListener settingListener) {
		if (settingMap.containsKey(clazz)) {
			get(clazz).addListener(settingListener);
		}
	}
}
