package com.WazaBe.HoloEverywhere.widget;

import android.view.MotionEvent;

import com.WazaBe.HoloEverywhere.util.Pool;
import com.WazaBe.HoloEverywhere.util.Poolable;
import com.WazaBe.HoloEverywhere.util.PoolableManager;
import com.WazaBe.HoloEverywhere.util.Pools;

public final class VelocityTracker implements Poolable<VelocityTracker> {
	public static final class Estimator {
		private static final int MAX_DEGREE = 4;
		public float confidence;
		public int degree;
		public final float[] xCoeff = new float[MAX_DEGREE + 1];
		public final float[] yCoeff = new float[MAX_DEGREE + 1];

		private float estimate(float time, float[] c) {
			float a = 0;
			float scale = 1;
			for (int i = 0; i <= degree; i++) {
				a += c[i] * scale;
				scale *= time;
			}
			return a;
		}

		public float estimateX(float time) {
			return estimate(time, xCoeff);
		}

		public float estimateY(float time) {
			return estimate(time, yCoeff);
		}

		public float getXCoeff(int index) {
			return index <= degree ? xCoeff[index] : 0;
		}

		public float getYCoeff(int index) {
			return index <= degree ? yCoeff[index] : 0;
		}
	}

	private static final int ACTIVE_POINTER_ID = -1;
	private static final Pool<VelocityTracker> sPool = Pools
			.synchronizedPool(Pools.finitePool(
					new PoolableManager<VelocityTracker>() {
						@Override
						public VelocityTracker newInstance() {
							return new VelocityTracker(null);
						}

						@Override
						public void onAcquired(VelocityTracker element) {
						}

						@Override
						public void onReleased(VelocityTracker element) {
							element.clear();
						}
					}, 2));

	private static native void nativeAddMovement(int ptr, MotionEvent event);

	private static native void nativeClear(int ptr);

	private static native void nativeComputeCurrentVelocity(int ptr, int units,
			float maxVelocity);

	private static native void nativeDispose(int ptr);

	private static native boolean nativeGetEstimator(int ptr, int id,
			Estimator outEstimator);

	private static native float nativeGetXVelocity(int ptr, int id);

	private static native float nativeGetYVelocity(int ptr, int id);

	private static native int nativeInitialize(String strategy);

	static public VelocityTracker obtain() {
		return sPool.acquire();
	}

	public static VelocityTracker obtain(String strategy) {
		if (strategy == null) {
			return obtain();
		}
		return new VelocityTracker(strategy);
	}

	private boolean mIsPooled;

	private VelocityTracker mNext;

	private int mPtr;

	private final String mStrategy;

	private VelocityTracker(String strategy) {
		mPtr = nativeInitialize(strategy);
		mStrategy = strategy;
	}

	public void addMovement(MotionEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("event must not be null");
		}
		nativeAddMovement(mPtr, event);
	}

	public void clear() {
		nativeClear(mPtr);
	}

	public void computeCurrentVelocity(int units) {
		nativeComputeCurrentVelocity(mPtr, units, Float.MAX_VALUE);
	}

	public void computeCurrentVelocity(int units, float maxVelocity) {
		nativeComputeCurrentVelocity(mPtr, units, maxVelocity);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mPtr != 0) {
				nativeDispose(mPtr);
				mPtr = 0;
			}
		} finally {
			super.finalize();
		}
	}

	public boolean getEstimator(int id, Estimator outEstimator) {
		if (outEstimator == null) {
			throw new IllegalArgumentException("outEstimator must not be null");
		}
		return nativeGetEstimator(mPtr, id, outEstimator);
	}

	@Override
	public VelocityTracker getNextPoolable() {
		return mNext;
	}

	public float getXVelocity() {
		return nativeGetXVelocity(mPtr, ACTIVE_POINTER_ID);
	}

	public float getXVelocity(int id) {
		return nativeGetXVelocity(mPtr, id);
	}

	public float getYVelocity() {
		return nativeGetYVelocity(mPtr, ACTIVE_POINTER_ID);
	}

	public float getYVelocity(int id) {
		return nativeGetYVelocity(mPtr, id);
	}

	@Override
	public boolean isPooled() {
		return mIsPooled;
	}

	public void recycle() {
		if (mStrategy == null) {
			sPool.release(this);
		}
	}

	@Override
	public void setNextPoolable(VelocityTracker element) {
		mNext = element;
	}

	@Override
	public void setPooled(boolean isPooled) {
		mIsPooled = isPooled;
	}
}