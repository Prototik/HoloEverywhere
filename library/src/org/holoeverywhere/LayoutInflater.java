
package org.holoeverywhere;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app._HoloFragmentInflater;
import android.support.v7.internal.view.menu.ExpandedMenuView;
import android.support.v7.internal.widget.ActionBarContainer;
import android.support.v7.internal.widget.ActionBarView;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import org.holoeverywhere.SystemServiceManager.SystemServiceCreator;
import org.holoeverywhere.SystemServiceManager.SystemServiceCreator.SystemService;
import org.holoeverywhere.app.ContextThemeWrapperPlus;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.util.SparseIntArray;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.NumberPicker;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LayoutInflater extends android.view.LayoutInflater implements Cloneable {
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();
    private static final Class<?>[] sConstructorSignature = {
            Context.class, AttributeSet.class
    };
    private static final Map<Class<?>, Method> sFinishInflateMethods =
            new HashMap<Class<?>, Method>(100);
    private static final Map<Context, LayoutInflater> sInstances = new WeakHashMap<Context, LayoutInflater>();
    private static final List<String> sPackages = new ArrayList<String>();
    private static final Map<String, String> sRemaps = new HashMap<String, String>();
    private static final SparseIntArray sLayoutRemap = new SparseIntArray();
    private static final String TAG_1995 = "blink";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_MERGE = "merge";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";

    static {
        registerPackage("android.webkit");
        registerPackage("android.view");
        registerPackage("android.widget");
        registerPackage("android.support.v4.view");
        registerPackage(HoloEverywhere.PACKAGE + ".widget");

        asInternal(ActionBarView.class);
        asInternal(ExpandedMenuView.class);
        asInternal(ActionBarContainer.class);
        asInternal(Dialog.DialogTitle.class);
        asInternal(NumberPicker.NumberPickerEditText.class);
    }

    private static OnInitInflaterListener sListener;
    private final Fragment mChildFragment;
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;
    private List<Factory> mFactories;
    private Filter mFilter;
    private HashMap<String, Boolean> mFilterMap;
    private FragmentActivity mFragmentActivity;
    private LayoutInflater mParentInflater;

    protected LayoutInflater(android.view.LayoutInflater original,
                             Context newContext) {
        this(original, newContext, null);
    }

    protected LayoutInflater(android.view.LayoutInflater original, Context newContext, Fragment childFragment) {
        this(newContext, childFragment);
        setParent(original);
    }

    protected LayoutInflater(Context context) {
        this(context, null);
    }

    protected LayoutInflater(Context context, Fragment childFragment) {
        super(context);
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        mChildFragment = childFragment;
        mContext = context;
        if (LayoutInflater.sListener != null) {
            LayoutInflater.sListener.onInitInflater(this);
        }
    }

    private static void asInternal(Class<?> clazz) {
        register("Internal." + clazz.getSimpleName(), clazz.getName());
    }

    public static LayoutInflater from(android.view.LayoutInflater inflater) {
        if (inflater instanceof LayoutInflater) {
            return (LayoutInflater) inflater;
        }
        return LayoutInflater.from(inflater.getContext()).setParent(inflater);
    }

    public static LayoutInflater from(Context context) {
        LayoutInflater inflater = sInstances.get(context);
        if (inflater == null) {
            sInstances.put(context, inflater = new LayoutInflater(context));
        }
        return inflater;
    }

    public static LayoutInflater from(Context context, int theme) {
        return from(new ContextThemeWrapperPlus(context, ThemeManager.getThemeResource(theme, false)));
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

    /**
     * Iterate over classes and call {@link #register(Class)} for each
     */
    public static void register(Class<? extends View>... classes) {
        for (Class<? extends View> classe : classes) {
            register(classe);
        }
    }

    /**
     * Fast mapping views by name<br />
     * <br />
     * MyView -> com.myapppackage.widget.MyView<br />
     */
    public static void register(Class<? extends View> clazz) {
        if (clazz != null) {
            register(clazz.getSimpleName(), clazz.getName());
        }
    }

    /**
     * Manually register shortcuts for inflating<br />
     * Not recommend to use. You are warned. <br />
     * <br />
     * MyView -> com.myapppackage.widget.SuperPuperViewVeryCustom
     */
    public static void register(String from, String to) {
        LayoutInflater.sRemaps.put(from, to);
    }

    /**
     * Hack for overriding android default layouts for custom.<br />
     * For example, if some parts of android framework try to inflate layout {@link android.R.layout#simple_list_item_1}
     * you can override this behavior and replace system layout by custom<br />
     * Just call register(android.R.layout.simple_list_item_1, R.layout.my_simple_list_item)<br />
     * <br />
     * Be sure that you need for it before using. You are warned.
     */
    public static void register(int fromId, int toId) {
        if (toId == 0) {
            sLayoutRemap.delete(fromId);
        } else {
            sLayoutRemap.put(fromId, toId);
        }
    }

    /**
     * Resolve ids for given name in each package (android and application) and then call {@link #register(int, int)}
     */
    public static void register(Context context, String name) {
        final Resources res = context.getResources();
        int androidId = res.getIdentifier(name, "layout", "android");
        int appId = res.getIdentifier(name, "layout", context.getPackageName());
        if (androidId != 0 && appId != 0) {
            register(androidId, appId);
        } else {
            HoloEverywhere.warn("Failed to register layout remapping:\n" +
                    "  Android ID: 0x%8x\n" +
                    "  Application ID: 0x%8x",
                    androidId, appId);
        }
    }

    /**
     * Resolve ids for given id in each package (android and application) and then call {@link #register(int, int)}
     */
    public static void register(Context context, int id) {
        register(context, context.getResources().getResourceName(id));
    }

    public static void registerPackage(String packageName) {
        packageName = resolveFullPackageName(packageName);

        if (packageName != null && !sPackages.contains(packageName)) {
            sPackages.add(packageName);
        }
    }

    private static String resolveFullPackageName(String packageName) {
        Package resolvedPackage = Package.getPackage(packageName);

        if (resolvedPackage == null) {
            return null;
        }

        return resolvedPackage.getName();
    }

    public static void removeInstance(Context context) {
        sInstances.remove(context);
    }

    public static void setOnInitInflaterListener(OnInitInflaterListener listener) {
        sListener = listener;
    }

    public View _createView(String name, String prefix, AttributeSet attrs)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        Class<? extends View> clazz = null;
        try {
            if (constructor == null) {
                clazz = mContext.getClassLoader().loadClass(
                        prefix != null ? prefix + name : name).asSubclass(View.class);
                if (mFilter != null && clazz != null) {
                    boolean allowed = mFilter.onLoadClass(clazz);
                    if (!allowed) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            } else {
                if (mFilter != null) {
                    Boolean allowedState = mFilterMap.get(name);
                    if (allowedState == null) {
                        clazz = mContext.getClassLoader().loadClass(
                                prefix != null ? prefix + name : name).asSubclass(View.class);
                        boolean allowed = clazz != null && mFilter.onLoadClass(clazz);
                        mFilterMap.put(name, allowed);
                        if (!allowed) {
                            failNotAllowed(name, prefix, attrs);
                        }
                    } else if (allowedState.equals(Boolean.FALSE)) {
                        failNotAllowed(name, prefix, attrs);
                    }
                }
            }
            Object[] args = mConstructorArgs;
            args[1] = attrs;
            constructor.setAccessible(true); 
            final View view = constructor.newInstance(args);
            if (view instanceof ViewStub) {
                final ViewStub viewStub = (ViewStub) view;
                if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                    viewStub.setLayoutInflater(this);
                }
            }
            return view;
        } catch (NoSuchMethodException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class "
                    + (prefix != null ? prefix + name : name));
            ie.initCause(e);
            throw ie;

        } catch (ClassCastException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Class is not a View "
                    + (prefix != null ? prefix + name : name));
            ie.initCause(e);
            throw ie;
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class "
                    + (clazz == null ? "<unknown>" : clazz.getName()));
            ie.initCause(e);
            throw ie;
        }
    }

    public void addFactory(Factory factory) {
        checkFactoryOnNull(factory);
        if (mFactories == null) {
            mFactories = new ArrayList<Factory>();
        }
        mFactories.add(factory);
    }

    public void addFactory(Factory factory, int index) {
        checkFactoryOnNull(factory);
        if (mFactories == null) {
            mFactories = new ArrayList<Factory>();
        }
        mFactories.add(index, factory);
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

    View createViewFromTag(View parent, String name, AttributeSet attrs) {
        if ("fragment".equals(name)) {
            return _HoloFragmentInflater
                    .inflate(LayoutInflater.this, attrs, parent, mChildFragment);
        }
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            View view = null;
            if (mFactories != null) {
                for (int i = 0; i < mFactories.size(); i++) {
                    view = mFactories.get(i).onCreateView(parent, name, mContext, attrs);
                    if (view != null) {
                        break;
                    }
                }
            }
            if (view == null) {
                view = onCreateView(parent, name, attrs);
            }
            return prepareView(view);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        } catch (Exception e) {
            InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        }
    }

    private void failNotAllowed(String name, String prefix, AttributeSet attrs) {
        throw new InflateException(attrs.getPositionDescription()
                + ": Class not allowed to be inflated "
                + (prefix != null ? prefix + name : name));
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public void setFilter(Filter filter) {
        mFilter = filter;
        if (filter != null) {
            mFilterMap = new HashMap<String, Boolean>();
        }
    }

    public FragmentActivity getFragmentActivity() {
        return mFragmentActivity;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        mFragmentActivity = fragmentActivity;
    }

    public View inflate(int resource) {
        return inflate(resource, null, false);
    }

    @Override
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return inflate(getContext().getResources().getLayout(sLayoutRemap.get(resource, resource)), root, attachToRoot);
    }

    public View inflate(XmlPullParser parser) {
        return inflate(parser, null, false);
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root,
                        boolean attachToRoot) {
        synchronized (mConstructorArgs) {
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            mConstructorArgs[0] = mContext;
            View result = root;
            try {
                int type;
                while ((type = parser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    ;
                }
                if (type != XmlPullParser.START_TAG) {
                    throw new InflateException(parser.getPositionDescription()
                            + ": No start tag found!");
                }
                final String name = parser.getName();
                if (TAG_MERGE.equals(name)) {
                    if (root == null || !attachToRoot) {
                        throw new InflateException("<merge /> can be used only with a valid "
                                + "ViewGroup root and attachToRoot=true");
                    }
                    rInflate(parser, root, attrs, false);
                } else {
                    View temp;
                    if (TAG_1995.equals(name)) {
                        temp = new BlinkLayout(mContext, attrs);
                    } else {
                        temp = createViewFromTag(root, name, attrs);
                    }
                    ViewGroup.LayoutParams params = null;
                    if (root != null) {
                        params = root.generateLayoutParams(attrs);
                        if (!attachToRoot) {
                            temp.setLayoutParams(params);
                        }
                    }
                    rInflate(parser, temp, attrs, true);
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
                }
            } catch (XmlPullParserException e) {
                InflateException ex = new InflateException(e.getMessage());
                ex.initCause(e);
                throw ex;
            } catch (IOException e) {
                InflateException ex = new InflateException(
                        parser.getPositionDescription()
                                + ": " + e.getMessage());
                ex.initCause(e);
                throw ex;
            } finally {
                mConstructorArgs[1] = null;
            }
            return result;
        }
    }

    public LayoutInflater obtainFragmentChildInflater(Fragment fragment) {
        if (mParentInflater != null) {
            return mParentInflater.obtainFragmentChildInflater(fragment);
        }
        return new LayoutInflater(this, mContext, fragment);
    }

    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs)
            throws ClassNotFoundException {
        View view;
        String newName = LayoutInflater.sRemaps.get(name);
        if (newName != null) {
            view = _createView(newName, null, attrs);
            if (view != null) {
                return view;
            }
        }
        if (name.indexOf('.') > 0) {
            return _createView(name, null, attrs);
        }
        for (int i = sPackages.size() - 1; i >= 0; i--) {
            try {
                view = _createView(name, sPackages.get(i) + ".", attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new ClassNotFoundException("Could not find class: " + name);
    }

    private void parseInclude(XmlPullParser parser, View parent, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        int type;
        if (parent instanceof ViewGroup) {
            final int layout = attrs.getAttributeResourceValue(null, "layout", 0);
            if (layout == 0) {
                final String value = attrs.getAttributeValue(null, "layout");
                if (value == null) {
                    throw new InflateException("You must specifiy a layout in the"
                            + " include tag: <include layout=\"@layout/layoutID\" />");
                } else {
                    throw new InflateException("You must specifiy a valid layout "
                            + "reference. The layout ID " + value + " is not valid.");
                }
            } else {
                final XmlResourceParser childParser =
                        getContext().getResources().getLayout(layout);
                try {
                    final AttributeSet childAttrs = Xml.asAttributeSet(childParser);
                    while ((type = childParser.next()) != XmlPullParser.START_TAG &&
                            type != XmlPullParser.END_DOCUMENT) {
                        ;
                    }
                    if (type != XmlPullParser.START_TAG) {
                        throw new InflateException(childParser.getPositionDescription() +
                                ": No start tag found!");
                    }
                    final String childName = childParser.getName();
                    if (TAG_MERGE.equals(childName)) {
                        rInflate(childParser, parent, childAttrs, false);
                    } else {
                        final View view = createViewFromTag(parent, childName, childAttrs);
                        final ViewGroup group = (ViewGroup) parent;
                        ViewGroup.LayoutParams params = null;
                        try {
                            params = group.generateLayoutParams(attrs);
                        } catch (RuntimeException e) {
                            params = group.generateLayoutParams(childAttrs);
                        } finally {
                            if (params != null) {
                                view.setLayoutParams(params);
                            }
                        }
                        rInflate(childParser, view, childAttrs, true);
                        TypedArray a = mContext.obtainStyledAttributes(attrs,
                                new int[]{
                                        android.R.attr.id,
                                        android.R.attr.visibility
                                }, 0, 0);
                        int id = a.getResourceId(0, View.NO_ID);
                        int visibility = a.getInt(1, -1);
                        a.recycle();
                        if (id != View.NO_ID) {
                            view.setId(id);
                        }
                        switch (visibility) {
                            case 0:
                                view.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                view.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                view.setVisibility(View.GONE);
                                break;
                        }
                        group.addView(view);
                    }
                } finally {
                    childParser.close();
                }
            }
        } else {
            throw new InflateException("<include /> can only be used inside of a ViewGroup");
        }
        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            ;
        }
    }

    private void parseRequestFocus(XmlPullParser parser, View parent)
            throws XmlPullParserException, IOException {
        int type;
        parent.requestFocus();
        final int currentDepth = parser.getDepth();
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > currentDepth) && type != XmlPullParser.END_DOCUMENT) {
            ;
        }
    }

    @SuppressLint("NewApi")
    private View prepareView(View view) {
        return view;
    }

    void rInflate(XmlPullParser parser, View parent, final AttributeSet attrs,
                  boolean finishInflate) throws XmlPullParserException, IOException {
        final int depth = parser.getDepth();
        int type;
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (TAG_REQUEST_FOCUS.equals(name)) {
                parseRequestFocus(parser, parent);
            } else if (TAG_INCLUDE.equals(name)) {
                if (parser.getDepth() == 0) {
                    throw new InflateException("<include /> cannot be the root element");
                }
                parseInclude(parser, parent, attrs);
            } else if (TAG_MERGE.equals(name)) {
                throw new InflateException("<merge /> must be the root element");
            } else if (TAG_1995.equals(name)) {
                final View view = new BlinkLayout(mContext, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                rInflate(parser, view, attrs, true);
                viewGroup.addView(view, params);
            } else {
                final View view = createViewFromTag(parent, name, attrs);
                final ViewGroup viewGroup = (ViewGroup) parent;
                final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
                rInflate(parser, view, attrs, true);
                viewGroup.addView(view, params);
            }
        }
        if (finishInflate) {
            Class<?> clazz = parent.getClass();
            Method method = sFinishInflateMethods.get(clazz);
            if (method == null) {
                while (clazz != Object.class && method == null) {
                    try {
                        method = clazz.getDeclaredMethod("onFinishInflate", (Class<?>[]) null);
                    } catch (Exception e) {
                        clazz = clazz.getSuperclass();
                    } catch (NoClassDefFoundError e) {
                        // HE#596 - Let's end the search here for those less than API 14
                        clazz = Object.class;
                    }
                }
                if (method != null) {
                    method.setAccessible(true);
                    sFinishInflateMethods.put(parent.getClass(), method);
                }
            }
            if (method != null) {
                try {
                    method.invoke(parent, (Object[]) null);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void setFactory(android.view.LayoutInflater.Factory factory) {
        setFactory(new FactoryWrapper(factory));
    }

    public void setFactory(Factory factory) {
        addFactory(factory, 0);
    }

    @Override
    public void setFactory2(Factory2 factory) {
        setFactory(new Factory2Wrapper(factory));
    }

    protected LayoutInflater setParent(android.view.LayoutInflater original) {
        if (original == this) {
            return this;
        }
        if (original instanceof LayoutInflater) {
            mParentInflater = (LayoutInflater) original;
            mFilter = mParentInflater.mFilter;
            mFilterMap = mParentInflater.mFilterMap;
            mFactories = mParentInflater.mFactories;
        } else {
            mParentInflater = null;
            if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                final Factory2 factory = original.getFactory2();
                if (factory != null) {
                    setFactory2(factory);
                }
            }
            final android.view.LayoutInflater.Factory factory = original.getFactory();
            if (factory != null) {
                setFactory(factory);
            }
            final Filter filter = original.getFilter();
            if (filter != null) {
                setFilter(filter);
            }
        }
        return this;
    }

    public interface Factory {
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs);
    }

    public static interface OnInitInflaterListener {
        public void onInitInflater(LayoutInflater inflater);
    }

    private static class BlinkLayout extends FrameLayout {
        private static final int BLINK_DELAY = 500;
        private static final int MESSAGE_BLINK = 0x42;
        private final Handler mHandler;
        private boolean mBlink;
        private boolean mBlinkState;

        public BlinkLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == MESSAGE_BLINK) {
                        if (mBlink) {
                            mBlinkState = !mBlinkState;
                            makeBlink();
                        }
                        invalidate();
                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }

        private void makeBlink() {
            Message message = mHandler.obtainMessage(MESSAGE_BLINK);
            mHandler.sendMessageDelayed(message, BLINK_DELAY);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            mBlink = true;
            mBlinkState = true;
            makeBlink();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mBlink = false;
            mBlinkState = true;
            mHandler.removeMessages(MESSAGE_BLINK);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static final class Factory2Wrapper implements Factory {
        private Factory2 mFactory;

        public Factory2Wrapper(Factory2 factory) {
            mFactory = factory;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mFactory.onCreateView(parent, name, context, attrs);
        }
    }

    private static final class FactoryWrapper implements Factory {
        private android.view.LayoutInflater.Factory mFactory;

        public FactoryWrapper(android.view.LayoutInflater.Factory factory) {
            mFactory = factory;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mFactory.onCreateView(name, context, attrs);
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
}
