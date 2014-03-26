package org.holoeverywhere.slider;

import android.os.Bundle;

import org.holoeverywhere.app.Fragment;

interface IMenuAdder<T extends BaseSliderItem<T>> {
    public T add(CharSequence label);

    public T add(CharSequence label, int[] colors);

    public T add(CharSequence label, Class<? extends Fragment> fragmentClass);

    public T add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments);

    public T add(CharSequence label, Class<? extends Fragment> fragmentClass, int[] colors);

    public T add(CharSequence label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors);

    public T add(int label);

    public T add(int label, int[] colors);

    public T add(int label, Class<? extends Fragment> fragmentClass);

    public T add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments);

    public T add(int label, Class<? extends Fragment> fragmentClass, int[] colors);

    public T add(int label, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments, int[] colors);

    public T add(T item);

    public T add(T item, int position);
}
