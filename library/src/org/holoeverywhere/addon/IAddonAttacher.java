
package org.holoeverywhere.addon;

import java.util.Collection;

public interface IAddonAttacher<V extends IAddonBase<?>> {
    public abstract static class AddonCallback<V> {
        public boolean action(V addon) {
            justAction(addon);
            return false;
        }

        public void justAction(V addon) {

        }

        public void justPost() {

        }

        public boolean post() {
            justPost();
            return false;
        }
    }

    public static class AttachException extends RuntimeException {
        private static final long serialVersionUID = 4007240742116340485L;

        public static AttachException afterInit(Object object, Class<? extends IAddon> clazz) {
            return new AttachException("Couldn't attach addon " + clazz.getName()
                    + " after init of object " + object);
        }

        public static AttachException conflict(String message) {
            return new AttachException("Couldn't attach some addons because conflicts is found: \n"
                    + message);
        }

        private AttachException(String message) {
            super(message);
        }
    }

    public <T extends V> T addon(Class<? extends IAddon> clazz);

    public void addon(Collection<Class<? extends IAddon>> classes);

    public <T extends V> T addon(String classname);

    public boolean isAddonAttached(Class<? extends IAddon> clazz);

    public void lockAttaching();

    public Collection<Class<? extends IAddon>> obtainAddonsList();

    public boolean performAddonAction(AddonCallback<V> callback);
}
