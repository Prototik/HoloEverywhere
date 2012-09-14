package com.WazaBe.HoloEverywhere.util;

public interface Poolable<T> {
	T getNextPoolable();

	boolean isPooled();

	void setNextPoolable(T element);

	void setPooled(boolean isPooled);
}