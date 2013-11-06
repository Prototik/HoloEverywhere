
package org.holoeverywhere.addon;

/**
 * Basic type-specified addon holder
 *
 * @param <T> class which this addon can handle
 */
public abstract class IAddonBase<T> {
    private T mObject;
    private IAddon mParent;

    /**
     * Only for system usage, don't call it!
     */
    public final void attach(T object, IAddon parent) {
        if (mObject != null || object == null || mParent != null || parent == null) {
            throw new IllegalStateException();
        }
        mParent = parent;
        onAttach(mObject = object);
    }

    /**
     * @return Object associated with this addon instance
     */
    public T get() {
        return mObject;
    }

    /**
     * @return Addon container
     */
    public final IAddon getParent() {
        return mParent;
    }

    /**
     * Called when addon attached to object
     */
    protected void onAttach(T object) {

    }
}
