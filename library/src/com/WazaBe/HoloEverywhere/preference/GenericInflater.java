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

	protected static final HashMap<String, Constructor<?>> constructorMap = new HashMap<String, Constructor<?>>();
	protected static final Class<?>[] constructorSignature = new Class<?>[] {
			Context.class, AttributeSet.class };
	protected final Object[] constructorArgs = new Object[2];
	protected final Context context;
	private String defaultPackage;
	private Factory<T> factory;
	private boolean factorySet;

	protected GenericInflater(Context context) {
		this.context = context;
	}

	protected GenericInflater(GenericInflater<T, P> original, Context newContext) {
		context = newContext;
		factory = original.factory;
	}

	public abstract GenericInflater<T, P> cloneInContext(Context newContext);

	@SuppressWarnings("unchecked")
	public final T createItem(String name, String prefix, AttributeSet attrs)
			throws ClassNotFoundException, InflateException {
		Constructor<?> constructor = constructorMap.get(name);

		try {
			if (constructor == null) {
				Class<?> clazz = context.getClassLoader().loadClass(
						prefix != null ? prefix + name : name);
				constructor = findConstructor(clazz);
				constructorMap.put(name, constructor);
			}
			Object[] args = constructorArgs;
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
		try {
			T item = factory == null ? null : factory.onCreateItem(name,
					context, attrs);
			if (item == null) {
				if (name.indexOf('.') < 0) {
					item = onCreateItem(name, attrs);
				} else {
					item = createItem(name, null, attrs);
				}
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

	protected Constructor<?> findConstructor(Class<?> clazz)
			throws NoSuchMethodException {
		return clazz.getConstructor(constructorSignature);
	}

	public Context getContext() {
		return context;
	}

	public String getDefaultPackage() {
		return defaultPackage;
	}

	public final Factory<T> getFactory() {
		return factory;
	}

	public T inflate(int resource, P root) {
		return inflate(resource, root, root != null);
	}

	public T inflate(int resource, P root, boolean attachToRoot) {
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
		synchronized (constructorArgs) {
			final AttributeSet attrs = Xml.asAttributeSet(parser);
			constructorArgs[0] = context;
			T result = (T) root;
			try {
				int type;
				while ((type = parser.next()) != XmlPullParser.START_TAG
						&& type != XmlPullParser.END_DOCUMENT) {
					;
				}
				if (type != XmlPullParser.START_TAG) {
					throw new InflateException(parser.getPositionDescription()
							+ ": No start tag found!");
				}
				T xmlRoot = createItemFromTag(parser, parser.getName(), attrs);
				result = (T) onMergeRoots(root, attachToRoot, (P) xmlRoot);
				rInflate(parser, result, attrs);
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
		return createItem(name, defaultPackage, attrs);
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
			String name = parser.getName();
			T item = createItemFromTag(parser, name, attrs);
			((P) parent).addItemFromInflater(item);
			rInflate(parser, item, attrs);
		}
	}

	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}

	public void setFactory(Factory<T> factory) {
		if (factorySet) {
			throw new IllegalStateException(""
					+ "A factory has already been set on this inflater");
		}
		if (factory == null) {
			throw new NullPointerException("Given factory can not be null");
		}
		factorySet = true;
		if (this.factory == null) {
			this.factory = factory;
		} else {
			this.factory = new FactoryMerger<T>(factory, this.factory);
		}
	}
}