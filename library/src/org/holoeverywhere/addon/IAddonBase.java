
package org.holoeverywhere.addon;

public abstract class IAddonBase<T> {
    private T mObject;

    public final void attach(T object) {
        if (mObject != null || object == null) {
            throw new IllegalStateException();
        }
        onAttach(mObject = object);
    }

    public T get() {
        return mObject;
    }

    protected void onAttach(T object) {

    }
}
