package org.holoeverywhere.plugin

class HoloEverywhereExtension {
    private static final String HOLO_EVERYWHERE_GROUP = 'org.holoeverywhere'
    private static final String HOLO_EVERYWHERE_NAME = 'library'
    private static final String HOLO_EVERYWHERE_VERSION = '2.1.0'
    private static final String HOLO_EVERYWHERE_ADDON_PREFERENCES = 'addon-preferences'
    private static final String HOLO_EVERYWHERE_ADDON_SLIDER = 'addon-slider'
    private static final String HOLO_EVERYWHERE_REPO = 'https://raw.github.com/Prototik/HoloEverywhere/repo'
    private static final String HOLO_EVERYWHERE_PACKAGE = 'org.holoeverywhere'

    private static final String SUPPORT_V4_GROUP = 'com.android.support'
    private static final String SUPPORT_V4_NAME = 'support-v4'
    private static final String SUPPORT_V4_VERSION = '18.0.4'

    class Addon {
        def String group, name

        public Addon(String name) {
            this(HOLO_EVERYWHERE_PACKAGE, name);
        }

        public Addon(String group, String name) {
            this.group = group
            this.name = name
        }

        public int hashCode() {
            return toString().hashCode()
        }

        public String toString() {
            return group + ":" + name
        }
    }

    class AddonContainer extends HashSet<Addon> {
        def preferences() {
            add(new Addon(HOLO_EVERYWHERE_ADDON_PREFERENCES))
        }

        def slider() {
            add(new Addon(HOLO_EVERYWHERE_ADDON_SLIDER))
        }
    }

    class LibraryContainer {
        def boolean include = true

        def String group = HOLO_EVERYWHERE_GROUP
        def String name = HOLO_EVERYWHERE_NAME
        def String version = HOLO_EVERYWHERE_VERSION
    }

    class SupportV4Container {
        def boolean include = true

        def String group = SUPPORT_V4_GROUP
        def String name = SUPPORT_V4_NAME
        def String version = SUPPORT_V4_VERSION
    }

    class RepositoryContainer {
        def boolean include = true
        def String url = HOLO_EVERYWHERE_REPO
    }

    def AddonContainer addons = new AddonContainer()
    def LibraryContainer library = new LibraryContainer()
    def SupportV4Container supportV4 = new SupportV4Container()
    def RepositoryContainer repository = new RepositoryContainer()

    def String configuration = 'compile'

    def AddonContainer addons(Closure<?> closure) {
        return call(closure, addons);
    }

    def LibraryContainer library(Closure<?> closure) {
        return call(closure, library);
    }

    def SupportV4Container supportV4(Closure<?> closure) {
        return call(closure, supportV4);
    }

    def RepositoryContainer repository(Closure<?> closure) {
        return call(closure, repository);
    }

    private static <T> T call(Closure<?> closure, T t) {
        closure.delegate = t
        closure.call(t)
        return t
    }
}
