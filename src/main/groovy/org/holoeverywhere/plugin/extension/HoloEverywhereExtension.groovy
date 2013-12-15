package org.holoeverywhere.plugin.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

class HoloEverywhereExtension {
    public static final String HOLO_EVERYWHERE_GROUP = 'org.holoeverywhere'
    public static final String HOLO_EVERYWHERE_NAME = 'library'
    public static final String HOLO_EVERYWHERE_VERSION = '2.1.0'
    public static final String HOLO_EVERYWHERE_REPO = 'http://192.241.191.41/repo'
    public static final String HOLO_EVERYWHERE_SNAPSHOT_REPO = 'http://192.241.191.41/snapshot'

    public static final String SUPPORT_V4_GROUP = 'com.android.support'
    public static final String SUPPORT_V4_NAME = 'support-v4'
    public static final String SUPPORT_V4_VERSION = '18.0.4'

    public static class Addon {
        def String group, name, version

        public Addon(String name) {
            this(HOLO_EVERYWHERE_GROUP, name);
        }

        public Addon(String group, String name) {
            this(group, name, null)
        }

        public Addon(String group, String name, String version) {
            this.group = group
            this.name = name
            this.version = version;
        }

        public int hashCode() {
            return toString().hashCode()
        }

        public String toString() {
            return group + ':' + name + ':' + version
        }
    }

    HoloEverywhereExtension(Project project, Instantiator instantiator) {
        this.project = project
        this.instantiator = instantiator

        this.library = new LibraryContainer(this)
        this.supportV4 = new SupportV4Container(this)
        this.repository = new RepositoryContainer(this)
        this.resbuilder = new ResbuilderContainer(project, instantiator)
        this.upload = new UploadContainer(project)

        this.addons = project.container(Addon, new NamedDomainObjectFactory<Addon>() {
            @Override
            Addon create(String name) {
                String[] parts = name.split(':')
                return parts.length > 1 ? new Addon(parts[0], parts[1], parts.length == 3 ? parts[2] : null) : new Addon("addon-${name}")
            }
        })
    }

    private final Project project
    private final Instantiator instantiator
    def final NamedDomainObjectContainer<Addon> addons
    def final LibraryContainer library
    def final SupportV4Container supportV4
    def final RepositoryContainer repository
    def final ResbuilderContainer resbuilder
    def final UploadContainer upload
    def IncludeContainer.Include include = Include.Yes
    def String configuration = 'compile'

    def void addons(Closure<?> closure) {
        addons.configure(closure)
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

    def ResbuilderContainer resbuilder(Closure<?> closure) {
        return call(closure, resbuilder);
    }

    def UploadContainer upload(Closure<?> closure) {
        return call(closure, upload);
    }

    def void include(String name) {
        this.include = IncludeContainer.Include.find(name, IncludeContainer.Include.Inhert)
    }

    public static <T> T call(Closure<?> closure, T t) {
        if (closure == null) return t
        closure.delegate = t
        closure.call(t)
        return t
    }

    private static final String EXTENSION_NAME = 'holoeverywhere'

    public static HoloEverywhereExtension getOrCreateExtension(Project project, Instantiator instantiator) {
        project.extensions.findByName(EXTENSION_NAME) as HoloEverywhereExtension ?: project.extensions.create(EXTENSION_NAME, HoloEverywhereExtension, project, instantiator)
    }
}
