package com.WazaBe.HoloEverywhere.util;

public interface Pool<T extends Poolable<T>> {
	public abstract T acquire();

	public abstract void release(T element);
}