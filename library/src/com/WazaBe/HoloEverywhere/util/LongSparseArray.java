package com.WazaBe.HoloEverywhere.util;

public class LongSparseArray<E> implements Cloneable {
	private static final Object DELETED = new Object();

	private static int binarySearch(long[] a, int start, int len, long key) {
		int high = start + len, low = start - 1, guess;
		while (high - low > 1) {
			guess = (high + low) / 2;
			if (a[guess] < key) {
				low = guess;
			} else {
				high = guess;
			}
		}
		if (high == start + len) {
			return ~(start + len);
		} else if (a[high] == key) {
			return high;
		} else {
			return ~high;
		}
	}

	private boolean mGarbage = false;
	private long[] mKeys;
	private int mSize;

	private Object[] mValues;

	public LongSparseArray() {
		this(10);
	}

	public LongSparseArray(int initialCapacity) {
		initialCapacity = ArrayUtils.idealLongArraySize(initialCapacity);
		mKeys = new long[initialCapacity];
		mValues = new Object[initialCapacity];
		mSize = 0;
	}

	public void append(long key, E value) {
		if (mSize != 0 && key <= mKeys[mSize - 1]) {
			put(key, value);
			return;
		}
		if (mGarbage && mSize >= mKeys.length) {
			gc();
		}
		int pos = mSize;
		if (pos >= mKeys.length) {
			int n = ArrayUtils.idealLongArraySize(pos + 1);
			long[] nkeys = new long[n];
			Object[] nvalues = new Object[n];
			System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
			System.arraycopy(mValues, 0, nvalues, 0, mValues.length);
			mKeys = nkeys;
			mValues = nvalues;
		}
		mKeys[pos] = key;
		mValues[pos] = value;
		mSize = pos + 1;
	}

	public void clear() {
		int n = mSize;
		Object[] values = mValues;
		for (int i = 0; i < n; i++) {
			values[i] = null;
		}
		mSize = 0;
		mGarbage = false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LongSparseArray<E> clone() {
		LongSparseArray<E> clone = null;
		try {
			clone = (LongSparseArray<E>) super.clone();
			clone.mKeys = mKeys.clone();
			clone.mValues = mValues.clone();
		} catch (CloneNotSupportedException cnse) {
		}
		return clone;
	}

	public void delete(long key) {
		int i = binarySearch(mKeys, 0, mSize, key);

		if (i >= 0) {
			if (mValues[i] != DELETED) {
				mValues[i] = DELETED;
				mGarbage = true;
			}
		}
	}

	private void gc() {
		int n = mSize;
		int o = 0;
		long[] keys = mKeys;
		Object[] values = mValues;
		for (int i = 0; i < n; i++) {
			Object val = values[i];
			if (val != DELETED) {
				if (i != o) {
					keys[o] = keys[i];
					values[o] = val;
					values[i] = null;
				}
				o++;
			}
		}
		mGarbage = false;
		mSize = o;
	}

	public E get(long key) {
		return get(key, null);
	}

	@SuppressWarnings("unchecked")
	public E get(long key, E valueIfKeyNotFound) {
		int i = binarySearch(mKeys, 0, mSize, key);

		if (i < 0 || mValues[i] == DELETED) {
			return valueIfKeyNotFound;
		} else {
			return (E) mValues[i];
		}
	}

	public int indexOfKey(long key) {
		if (mGarbage) {
			gc();
		}
		return binarySearch(mKeys, 0, mSize, key);
	}

	public int indexOfValue(E value) {
		if (mGarbage) {
			gc();
		}
		for (int i = 0; i < mSize; i++) {
			if (mValues[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public long keyAt(int index) {
		if (mGarbage) {
			gc();
		}

		return mKeys[index];
	}

	public void put(long key, E value) {
		int i = binarySearch(mKeys, 0, mSize, key);
		if (i >= 0) {
			mValues[i] = value;
		} else {
			i = ~i;
			if (i < mSize && mValues[i] == DELETED) {
				mKeys[i] = key;
				mValues[i] = value;
				return;
			}
			if (mGarbage && mSize >= mKeys.length) {
				gc();
				i = ~binarySearch(mKeys, 0, mSize, key);
			}
			if (mSize >= mKeys.length) {
				int n = ArrayUtils.idealLongArraySize(mSize + 1);
				long[] nkeys = new long[n];
				Object[] nvalues = new Object[n];
				System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
				System.arraycopy(mValues, 0, nvalues, 0, mValues.length);
				mKeys = nkeys;
				mValues = nvalues;
			}
			if (mSize - i != 0) {
				System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
				System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
			}
			mKeys[i] = key;
			mValues[i] = value;
			mSize++;
		}
	}

	public void remove(long key) {
		delete(key);
	}

	public void removeAt(int index) {
		if (mValues[index] != DELETED) {
			mValues[index] = DELETED;
			mGarbage = true;
		}
	}

	public void setValueAt(int index, E value) {
		if (mGarbage) {
			gc();
		}
		mValues[index] = value;
	}

	public int size() {
		if (mGarbage) {
			gc();
		}

		return mSize;
	}

	@SuppressWarnings("unchecked")
	public E valueAt(int index) {
		if (mGarbage) {
			gc();
		}
		return (E) mValues[index];
	}
}