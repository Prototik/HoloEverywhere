package com.WazaBe.HoloEverywhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.WazaBe.HoloEverywhere.app.Application;

public class LayoutInflater extends android.view.LayoutInflater implements
		Cloneable {
	private final class HoloFactoryMerger extends ArrayList<Factory> implements
			Factory {
		private static final long serialVersionUID = -851134244408815411L;

		@Override
		public View onCreateView(String name, Context context,
				AttributeSet attrs) {
			for (Factory factory : this) {
				try {
					View view = factory.onCreateView(name, context, attrs);
					if (view != null) {
						return view;
					}
				} catch (RuntimeException e) {
				}
			}
			return null;
		}
	}

	public static interface OnInitInflaterListener {
		public void onInitInflater(LayoutInflater inflater);
	}

	private static final Map<Context, LayoutInflater> INSTANCES_MAP = new WeakHashMap<Context, LayoutInflater>();
	private static OnInitInflaterListener listener;
	private static final Map<String, String> VIEWS_MAP = new HashMap<String, String>();

	static {
		remap(Application.getConfig().getWidgetsPackage(), "ProgressBar",
				"LinearLayout", "Switch", "TextView", "EditText",
				"AutoCompleteTextView", "MultiAutoCompleteTextView",
				"CalendarView", "Spinner", "NumberPicker", "DatePicker",
				"TimePicker", "ListView", "Divider", "SeekBar", "Button",
				"CheckedTextView");
		remap("android.support.v4.view", "ViewPager", "PagerTitleStrip");
		remap("android.webkit", "WebView");
	}

	public static void clearInstances() {
		INSTANCES_MAP.clear();
	}

	public static LayoutInflater from(android.view.LayoutInflater inflater) {
		if (inflater instanceof LayoutInflater) {
			return (LayoutInflater) inflater;
		}
		return new LayoutInflater(inflater, inflater.getContext());
	}

	public static LayoutInflater from(Context context) {
		if (!INSTANCES_MAP.containsKey(context)) {
			synchronized (INSTANCES_MAP) {
				if (!INSTANCES_MAP.containsKey(context)) {
					INSTANCES_MAP.put(context, new LayoutInflater(context));
				}
			}
		}
		return INSTANCES_MAP.get(context);
	}

	public static LayoutInflater from(View view) {
		return from(view.getContext());
	}

	public static Object getSystemService(Object superService) {
		if (superService instanceof android.view.LayoutInflater) {
			if (superService instanceof LayoutInflater) {
				return superService;
			}
			return LayoutInflater
					.from((android.view.LayoutInflater) superService);
		}
		return superService;
	}

	public static View inflate(Context context, int resource) {
		return from(context).inflate(resource, null);
	}

	public static View inflate(Context context, int resource, ViewGroup root) {
		return from(context).inflate(resource, root);
	}

	public static View inflate(Context context, int resource, ViewGroup root,
			boolean attachToRoot) {
		return from(context).inflate(resource, root, attachToRoot);
	}

	public static View inflate(View view, int resource) {
		return from(view).inflate(resource, null);
	}

	public static View inflate(View view, int resource, ViewGroup root) {
		return from(view).inflate(resource, root);
	}

	public static View inflate(View view, int resource, ViewGroup root,
			boolean attachToRoot) {
		return from(view).inflate(resource, root, attachToRoot);
	}

	public static void onDestroy(Context context) {
		if (INSTANCES_MAP.containsKey(context)) {
			synchronized (INSTANCES_MAP) {
				if (INSTANCES_MAP.containsKey(context)) {
					INSTANCES_MAP.remove(context);
				}
			}
		}
	}

	public static void remap(String prefix, String... classess) {
		for (String clazz : classess) {
			VIEWS_MAP.put(clazz, prefix + "." + clazz);
		}
	}

	public static void remapHard(String from, String to) {
		VIEWS_MAP.put(from, to);
	}

	public static void setOnInitInflaterListener(OnInitInflaterListener listener) {
		LayoutInflater.listener = listener;
	}

	private final HoloFactoryMerger factoryMerger = new HoloFactoryMerger();

	private boolean factorySet = false;

	protected LayoutInflater(android.view.LayoutInflater original,
			Context newContext) {
		super(original, newContext);
		init();
	}

	protected LayoutInflater(Context context) {
		super(context);
		init();
	}

	public void addFactory(Factory factory) {
		checkFactoryOnNull(factory);
		factoryMerger.add(factory);
	}

	public void addFactory(Factory factory, int index) {
		checkFactoryOnNull(factory);
		factoryMerger.add(index, factory);
	}

	private void checkFactoryOnNull(Factory factory) {
		if (factory == null) {
			throw new NullPointerException("Given factory can not be null");
		}
	}

	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		return new LayoutInflater(this, newContext);
	}

	public View inflate(int resource) {
		return inflate(resource, null);
	}

	private void init() {
		super.setFactory(factoryMerger);
		if (listener != null) {
			listener.onInitInflater(this);
		}
	}

	@Override
	protected View onCreateView(String name, AttributeSet attrs)
			throws ClassNotFoundException {
		name = name.intern();
		if (VIEWS_MAP.containsKey(name)) {
			return createView(VIEWS_MAP.get(name), null, attrs);
		}
		try {
			return createView(name, "android.widget.", attrs);
		} catch (ClassNotFoundException e) {
			return createView(name, "android.view.", attrs);
		}
	}

	@Override
	public void setFactory(Factory factory) {
		if (factorySet) {
			throw new IllegalStateException(
					"A factory has already been set on this inflater");
		}
		addFactory(factory);
		factorySet = true;
	}
}
