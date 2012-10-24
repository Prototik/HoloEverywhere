package com.WazaBe.HoloEverywhere;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.WazaBe.HoloEverywhere.SystemServiceManager.SystemServiceCreator.SystemService;

public final class SystemServiceManager {
	public static interface SuperSystemService {
		public Object superGetSystemService(String name);
	}

	public static interface SystemServiceCreator<T> {
		@Target(ElementType.TYPE)
		@Retention(RetentionPolicy.RUNTIME)
		public static @interface SystemService {
			public String value();
		}

		public T createService(Context context);
	}

	private static final Map<String, SystemServiceCreator<?>> MAP = new HashMap<String, SystemServiceCreator<?>>();

	public static Object getSystemService(Context context, String name) {
		if (context == null || context.isRestricted()) {
			throw new RuntimeException("Invalid context");
		} else if (name == null || name.length() == 0) {
			return null;
		}
		SystemServiceCreator<?> creator = MAP.get(name);
		if (creator != null) {
			Object o = creator.createService(context);
			if (o != null) {
				return o;
			}
		}
		if (context instanceof SuperSystemService) {
			return ((SuperSystemService) context).superGetSystemService(name);
		} else {
			return context.getSystemService(name);
		}
	}

	public static void register(Class<? extends SystemServiceCreator<?>> clazz) {
		try {
			register(clazz.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void register(SystemServiceCreator<?> creator) {
		Class<?> clazz = creator.getClass();
		if (!clazz.isAnnotationPresent(SystemService.class)) {
			throw new RuntimeException(
					"SystemServiceCreator must be implement SystemService");
		}
		SystemService systemService = clazz.getAnnotation(SystemService.class);
		final String name = systemService.value();
		if (name == null || name.length() == 0) {
			throw new RuntimeException("SystemService has incorrect name");
		}
		MAP.put(name, creator);
	}

	public static void unregister(SystemServiceCreator<?> creator) {
		for (Entry<String, SystemServiceCreator<?>> entry : MAP.entrySet()) {
			if (entry.getValue() == creator) {
				MAP.remove(entry.getKey());
				return;
			}
		}
	}

	private SystemServiceManager() {

	}
}
