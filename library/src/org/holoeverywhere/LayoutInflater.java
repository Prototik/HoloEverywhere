package org.holoeverywhere;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.holoeverywhere.SystemServiceManager.SystemServiceCreator;
import org.holoeverywhere.SystemServiceManager.SystemServiceCreator.SystemService;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.internal.DialogTitle;
import org.holoeverywhere.internal.AlertController.RecycleListView;
import org.holoeverywhere.internal.NumberPickerEditText;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.view.menu.ExpandedMenuView;
import com.actionbarsherlock.internal.view.menu.HoloListMenuItemView;
import com.actionbarsherlock.internal.widget.ActionBarContainer;
import com.actionbarsherlock.internal.widget.ActionBarView;

public class LayoutInflater extends android.view.LayoutInflater implements
		Cloneable, android.view.LayoutInflater.Factory {
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

	@SystemService(Context.LAYOUT_INFLATER_SERVICE)
	public static class LayoutInflaterCreator implements
			SystemServiceCreator<LayoutInflater> {
		@Override
		public LayoutInflater createService(Context context) {
			return LayoutInflater.from(context);
		}
	}

	public static interface OnInitInflaterListener {
		public void onInitInflater(LayoutInflater inflater);
	}

	private static final Map<Context, WeakReference<LayoutInflater>> INSTANCES_MAP = new WeakHashMap<Context, WeakReference<LayoutInflater>>();
	private static OnInitInflaterListener listener;
	private static final Map<String, String> VIEWS_MAP = new HashMap<String, String>();

	static {
		remap(Application.getConfig().getWidgetsPackage(), "ProgressBar",
				"LinearLayout", "Switch", "TextView", "EditText",
				"AutoCompleteTextView", "MultiAutoCompleteTextView",
				"CalendarView", "Spinner", "NumberPicker", "DatePicker",
				"TimePicker", "ListView", "Divider", "SeekBar", "Button",
				"CheckedTextView", "FragmentBreadCrumbs");
		remap(Application.getConfig().getPreferencePackage(),
				"PreferenceFrameLayout");
		remap("android.support.v4.view", "ViewPager", "PagerTitleStrip");
		remap("android.webkit", "WebView");
		remapInternal(ActionBarView.class, HoloListMenuItemView.class,
				ExpandedMenuView.class, ActionBarContainer.class,
				RecycleListView.class, DialogTitle.class,
				NumberPickerEditText.class);
	}

	public static void clearInstances() {
		LayoutInflater.INSTANCES_MAP.clear();
	}

	public static LayoutInflater from(android.view.LayoutInflater inflater) {
		if (inflater instanceof LayoutInflater) {
			return (LayoutInflater) inflater;
		}
		return new LayoutInflater(inflater, inflater.getContext());
	}

	public static LayoutInflater from(Context context) {
		LayoutInflater inflater = null;
		WeakReference<LayoutInflater> reference = INSTANCES_MAP.get(context);
		if (reference != null) {
			inflater = reference.get();
		}
		if (inflater == null) {
			inflater = new LayoutInflater(context);
			reference = new WeakReference<LayoutInflater>(inflater);
			INSTANCES_MAP.put(context, reference);
		}
		return inflater;
	}

	public static LayoutInflater from(View view) {
		return LayoutInflater.from(view.getContext());
	}

	public static View inflate(Context context, int resource) {
		return LayoutInflater.from(context).inflate(resource, null);
	}

	public static View inflate(Context context, int resource, ViewGroup root) {
		return LayoutInflater.from(context).inflate(resource, root);
	}

	public static View inflate(Context context, int resource, ViewGroup root,
			boolean attachToRoot) {
		return LayoutInflater.from(context).inflate(resource, root,
				attachToRoot);
	}

	public static View inflate(View view, int resource) {
		return LayoutInflater.from(view).inflate(resource, null);
	}

	public static View inflate(View view, int resource, ViewGroup root) {
		return LayoutInflater.from(view).inflate(resource, root);
	}

	public static View inflate(View view, int resource, ViewGroup root,
			boolean attachToRoot) {
		return LayoutInflater.from(view).inflate(resource, root, attachToRoot);
	}

	public static void onDestroy(Context context) {
		LayoutInflater.INSTANCES_MAP.remove(context);
	}

	public static void remap(String prefix, String... classess) {
		for (String clazz : classess) {
			LayoutInflater.VIEWS_MAP.put(clazz, prefix + "." + clazz);
		}
	}

	public static void remapHard(String from, String to) {
		Log.v("LayoutInflater", "From: " + from + ". To: " + to);
		LayoutInflater.VIEWS_MAP.put(from, to);
	}

	private static void remapInternal(Class<?>... classess) {
		for (Class<?> clazz : classess) {
			remapHard("Internal." + clazz.getSimpleName(), clazz.getName());
		}
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
		factoryMerger.add(this);
		if (LayoutInflater.listener != null) {
			LayoutInflater.listener.onInitInflater(this);
		}
	}

	@Override
	protected View onCreateView(String name, AttributeSet attrs)
			throws ClassNotFoundException {
		String newName = LayoutInflater.VIEWS_MAP.get(name.intern());
		View view;
		if (newName != null) {
			view = tryCreateView(newName, null, attrs);
			if (view != null) {
				return view;
			}
		}
		if (name.indexOf('.') > 0) {
			view = tryCreateView(name, null, attrs);
			if (view != null) {
				return view;
			}
		}
		view = tryCreateView(name, "android.widget.", attrs);
		if (view != null) {
			return view;
		}
		view = tryCreateView(name, "android.view.", attrs);
		if (view != null) {
			return view;
		} else {
			throw new ClassNotFoundException("Could not find class: " + name);
		}
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		try {
			return onCreateView(name, attrs);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
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

	protected View tryCreateView(String name, String prefix, AttributeSet attrs) {
		String newName = prefix == null ? "" : prefix;
		newName += name;
		try {
			if (Class.forName(newName) != null) {
				return createView(newName, null, attrs);
			}
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
