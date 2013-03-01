
package org.holoeverywhere.addon;

public interface IAddonAttacher<V extends IAddonBase<?>> {
    public abstract static class AddonCallback<V> {
        public boolean mStopped = false;

        public boolean action(V addon) {
            justAction(addon);
            return false;
        }

        public void justAction(V addon) {

        }

        public void justPost() {

        }

        public boolean performAction(V addon) {
            if (action(addon)) {
                stop();
                return true;
            }
            return false;
        }

        public boolean post() {
            justPost();
            return false;
        }

        public void pre() {

        }

        public void stop() {
            mStopped = true;
        }
    }

    public <T extends V> T addon(Class<? extends IAddon> clazz);

    public <T extends V> T addon(String classname);

    public boolean isAddonAttached(Class<? extends IAddon> clazz);

    public boolean performAddonAction(AddonCallback<V> callback);
}
