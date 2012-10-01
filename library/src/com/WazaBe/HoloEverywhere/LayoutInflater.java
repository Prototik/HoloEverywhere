package com.WazaBe.HoloEverywhere;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class LayoutInflater extends android.view.LayoutInflater implements
		Cloneable {
	public static interface OnInitInflaterListener {
		public void onInitInflater(LayoutInflater inflater);
	}

	private static boolean inited = false;
	private static final Map<Context, LayoutInflater> INSTANCES_MAP = new WeakHashMap<Context, LayoutInflater>();
	private static OnInitInflaterListener listener;
	private static final Map<String, String> VIEWS_MAP = new HashMap<String, String>();

	static {
		putToMap(Settings.getWidgetsPackage(), "ProgressBar", "LinearLayout",
				"Switch", "TextView", "EditText", "AutoCompleteTextView",
				"MultiAutoCompleteTextView", "CalendarView", "Spinner",
				"NumberPicker", "DatePicker", "TimePicker", "ListView",
				"Divider", "SeekBar");
		putToMap("android.support.v4.view", "ViewPager", "PagerTitleStrip");
		putToMap("android.webkit", "WebView");
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

	public static void putToMap(String prefix, String... classess) {
		for (String clazz : classess) {
			VIEWS_MAP.put(clazz, prefix);
		}
	}

	public static void setOnInitInflaterListener(OnInitInflaterListener listener) {
		LayoutInflater.listener = listener;
	}

	protected LayoutInflater(android.view.LayoutInflater original,
			Context newContext) {
		super(original, newContext);
		init();
	}

	protected LayoutInflater(Context context) {
		super(context);
		init();
	}

	@Override
	public LayoutInflater cloneInContext(Context newContext) {
		return new LayoutInflater(this, newContext);
	}

	public View inflate(int resource) {
		return inflate(resource, null);
	}

	private void init() {
		if (!inited) {
			synchronized (LayoutInflater.class) {
				if (!inited) {
					inited = true;
					if (listener != null) {
						listener.onInitInflater(this);
					}
				}
			}
		}
	}

	@Override
	protected View onCreateView(String name, AttributeSet attrs)
			throws ClassNotFoundException {
		name = name.intern();
		if (VIEWS_MAP.containsKey(name)) {
			return createView(name, VIEWS_MAP.get(name) + ".", attrs);
		}
		try {
			return createView(name, "android.widget.", attrs);
		} catch (ClassNotFoundException e) {
			return createView(name, "android.view.", attrs);
		}
	}
}
