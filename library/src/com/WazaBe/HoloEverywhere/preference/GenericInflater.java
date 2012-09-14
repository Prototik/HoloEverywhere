package com.WazaBe.HoloEverywhere.preference;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;

abstract class GenericInflater<T, P extends GenericInflater.Parent<T>> {
	public interface Factory<T> {
		public T onCreateItem(String name, Context context, AttributeSet attrs);
	}

	private static class FactoryMerger<T> implements Factory<T> {
		private final Factory<T> mF1, mF2;

		FactoryMerger(Factory<T> f1, Factory<T> f2) {
			mF1 = f1;
			mF2 = f2;
		}

		@Override
		public T onCreateItem(String name, Context context, AttributeSet attrs) {
			T v = mF1.onCreateItem(name, context, attrs);
			if (v != null) {
				return v;
			}
			return mF2.onCreateItem(name, context, attrs);
		}
	}

	public interface Parent<T> {
		public void addItemFromInflater(T child);
	}

	private static final Class<?>[] mConstructorSignature = new Class[] {
			Context.class, AttributeSet.class };

	private static final HashMap<String, Constructor<?>> sConstructorMap = new HashMap<String, Constructor<?>>();

	private final boolean DEBUG = false;

	private final Object[] mConstructorArgs = new Object[2];

	protected final Context mContext;

	private String mDefaultPackage;

	private Factory<T> mFactory;

	// these are optional, set by the caller
	private boolean mFactorySet;

	/**
	 * Create a new inflater instance associated with a particular Context.
	 * 
	 * @param context
	 *            The Context in which this inflater will create its items; most
	 *            importantly, this supplies the theme from which the default
	 *            values for their attributes are retrieved.
	 */
	protected GenericInflater(Context context) {
		mContext = context;
	}

	/**
	 * Create a new inflater instance that is a copy of an existing inflater,
	 * optionally with its Context changed. For use in implementing
	 * {@link #cloneInContext}.
	 * 
	 * @param original
	 *            The original inflater to copy.
	 * @param newContext
	 *            The new Context to use.
	 */
	protected GenericInflater(GenericInflater<T, P> original, Context newContext) {
		mContext = newContext;
		mFactory = original.mFactory;
	}

	public abstract GenericInflater<T, P> cloneInContext(Context newContext);

	@SuppressWarnings("unchecked")
	public final T createItem(String name, String prefix, AttributeSet attrs)
			throws ClassNotFoundException, InflateException {
		Constructor<?> constructor = sConstructorMap.get(name);

		try {
			if (constructor == null) {
				Class<?> clazz = mContext.getClassLoader().loadClass(
						prefix != null ? prefix + name : name);
				constructor = clazz.getConstructor(mConstructorSignature);
				sConstructorMap.put(name, constructor);
			}
			Object[] args = mConstructorArgs;
			args[1] = attrs;
			return (T) constructor.newInstance(args);
		} catch (NoSuchMethodException e) {
			InflateException ie = new InflateException(
					attrs.getPositionDescription() + ": Error inflating class "
							+ (prefix != null ? prefix + name : name));
			ie.initCause(e);
			throw ie;
		} catch (Exception e) {
			InflateException ie = new InflateException(
					attrs.getPositionDescription() + ": Error inflating class "
							+ constructor.toString());
			ie.initCause(e);
			throw ie;
		}
	}

	private final T createItemFromTag(XmlPullParser parser, String name,
			AttributeSet attrs) {
		if (DEBUG) {
			System.out.println("******** Creating item: " + name);
		}

		try {
			T item = mFactory == null ? null : mFactory.onCreateItem(name,
					mContext, attrs);

			if (item == null) {
				if (-1 == name.indexOf('.')) {
					item = onCreateItem(name, attrs);
				} else {
					item = createItem(name, null, attrs);
				}
			}

			if (DEBUG) {
				System.out.println("Created item is: " + item);
			}
			return item;

		} catch (InflateException e) {
			throw e;

		} catch (ClassNotFoundException e) {
			InflateException ie = new InflateException(
					attrs.getPositionDescription() + ": Error inflating class "
							+ name);
			ie.initCause(e);
			throw ie;

		} catch (Exception e) {
			InflateException ie = new InflateException(
					attrs.getPositionDescription() + ": Error inflating class "
							+ name);
			ie.initCause(e);
			throw ie;
		}
	}

	public Context getContext() {
		return mContext;
	}

	public String getDefaultPackage() {
		return mDefaultPackage;
	}

	public final Factory<T> getFactory() {
		return mFactory;
	}

	public T inflate(int resource, P root) {
		return inflate(resource, root, root != null);
	}

	public T inflate(int resource, P root, boolean attachToRoot) {
		if (DEBUG) {
			System.out.println("INFLATING from resource: " + resource);
		}
		XmlResourceParser parser = getContext().getResources().getXml(resource);
		try {
			return inflate(parser, root, attachToRoot);
		} finally {
			parser.close();
		}
	}

	public T inflate(XmlPullParser parser, P root) {
		return inflate(parser, root, root != null);
	}

	@SuppressWarnings("unchecked")
	public T inflate(XmlPullParser parser, P root, boolean attachToRoot) {
		synchronized (mConstructorArgs) {
			final AttributeSet attrs = Xml.asAttributeSet(parser);
			mConstructorArgs[0] = mContext;
			T result = (T) root;

			try {
				// Look for the root node.
				int type;
				while ((type = parser.next()) != XmlPullParser.START_TAG
						&& type != XmlPullParser.END_DOCUMENT) {
					;
				}

				if (type != XmlPullParser.START_TAG) {
					throw new InflateException(parser.getPositionDescription()
							+ ": No start tag found!");
				}

				if (DEBUG) {
					System.out.println("**************************");
					System.out.println("Creating root: " + parser.getName());
					System.out.println("**************************");
				}
				// Temp is the root that was found in the xml
				T xmlRoot = createItemFromTag(parser, parser.getName(), attrs);

				result = (T) onMergeRoots(root, attachToRoot, (P) xmlRoot);

				if (DEBUG) {
					System.out.println("-----> start inflating children");
				}
				// Inflate all children under temp
				rInflate(parser, result, attrs);
				if (DEBUG) {
					System.out.println("-----> done inflating children");
				}

			} catch (InflateException e) {
				throw e;

			} catch (XmlPullParserException e) {
				InflateException ex = new InflateException(e.getMessage());
				ex.initCause(e);
				throw ex;
			} catch (IOException e) {
				InflateException ex = new InflateException(
						parser.getPositionDescription() + ": " + e.getMessage());
				ex.initCause(e);
				throw ex;
			}

			return result;
		}
	}

	protected boolean onCreateCustomFromTag(XmlPullParser parser, T parent,
			final AttributeSet attrs) throws XmlPullParserException {
		return false;
	}

	protected T onCreateItem(String name, AttributeSet attrs)
			throws ClassNotFoundException {
		return createItem(name, mDefaultPackage, attrs);
	}

	protected P onMergeRoots(P givenRoot, boolean attachToGivenRoot, P xmlRoot) {
		return xmlRoot;
	}

	@SuppressWarnings("unchecked")
	private void rInflate(XmlPullParser parser, T parent,
			final AttributeSet attrs) throws XmlPullParserException,
			IOException {
		final int depth = parser.getDepth();

		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser
				.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

			if (type != XmlPullParser.START_TAG) {
				continue;
			}

			if (onCreateCustomFromTag(parser, parent, attrs)) {
				continue;
			}

			if (DEBUG) {
				System.out.println("Now inflating tag: " + parser.getName());
			}
			String name = parser.getName();

			T item = createItemFromTag(parser, name, attrs);

			if (DEBUG) {
				System.out.println("Creating params from parent: " + parent);
			}

			((P) parent).addItemFromInflater(item);

			if (DEBUG) {
				System.out.println("-----> start inflating children");
			}
			rInflate(parser, item, attrs);
			if (DEBUG) {
				System.out.println("-----> done inflating children");
			}
		}

	}

	public void setDefaultPackage(String defaultPackage) {
		mDefaultPackage = defaultPackage;
	}

	public void setFactory(Factory<T> factory) {
		if (mFactorySet) {
			throw new IllegalStateException(""
					+ "A factory has already been set on this inflater");
		}
		if (factory == null) {
			throw new NullPointerException("Given factory can not be null");
		}
		mFactorySet = true;
		if (mFactory == null) {
			mFactory = factory;
		} else {
			mFactory = new FactoryMerger<T>(factory, mFactory);
		}
	}
}