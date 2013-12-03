package org.holoeverywhere.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSetFactory

class HoloEverywhereExtension {
    private static final String HOLO_EVERYWHERE_GROUP = 'org.holoeverywhere'
    private static final String HOLO_EVERYWHERE_NAME = 'library'
    private static final String HOLO_EVERYWHERE_VERSION = '2.1.0'
    private static final String HOLO_EVERYWHERE_REPO = 'http://192.241.191.41/repo'

    private static final String SUPPORT_V4_GROUP = 'com.android.support'
    private static final String SUPPORT_V4_NAME = 'support-v4'
    private static final String SUPPORT_V4_VERSION = '18.0.4'

    public static enum Include {
        Inhert('inhert'), Yes('yes'), No('no');

        String localName;

        Include(String localName) {
            this.localName = localName
        }

        public static Include find(String localName, Include defaultValue) {
            values().find { it.localName == localName } ?: defaultValue
        }
    }

    private abstract class IncludeContainer {
        IncludeContainer(HoloEverywhereExtension extension) {
            this.extension = extension
        }

        private final HoloEverywhereExtension extension
        def Include include = Include.Inhert

        def void include(String name) {
            this.include = Include.find(name, Include.Inhert)
        }

        def boolean realInclude() {
            return include == Include.Yes || (include == Include.Inhert && extension.include == Include.Yes)
        }
    }

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

    class LibraryContainer extends IncludeContainer {
        LibraryContainer(HoloEverywhereExtension extension) {
            super(extension)
        }

        def String group = HOLO_EVERYWHERE_GROUP
        def String name = HOLO_EVERYWHERE_NAME
        def String version = HOLO_EVERYWHERE_VERSION
    }

    class SupportV4Container extends IncludeContainer {
        SupportV4Container(HoloEverywhereExtension extension) {
            super(extension)
        }

        def String group = SUPPORT_V4_GROUP
        def String name = SUPPORT_V4_NAME
        def String version = SUPPORT_V4_VERSION
    }

    class RepositoryContainer extends IncludeContainer {
        RepositoryContainer(HoloEverywhereExtension extension) {
            super(extension)
        }

        def String url = HOLO_EVERYWHERE_REPO
    }

    class ResbuilderContainer {
        private static final DEFAULT_SOURCE_SET_NAME = 'main'

        ResbuilderContainer(Project project, Instantiator instantiator) {
            BaseExtension androidExtension = project.extensions.getByName('android') as BaseExtension
            Iterator<File> iterator = androidExtension.sourceSets.getByName(DEFAULT_SOURCE_SET_NAME).res.srcDirs.iterator()
            String resourcesDir = iterator.hasNext() ? (project as ProjectInternal).fileResolver.resolveAsRelativePath(iterator.next()) : null

            sourceSets = project.container(ResbuilderSourceSet, ResbuilderSourceSetFactory.fromProject(project, instantiator, resourcesDir))
            sourceSets.create(DEFAULT_SOURCE_SET_NAME)
        }

        final NamedDomainObjectContainer<ResbuilderSourceSet> sourceSets
        def boolean enable = true
        def boolean formatTask = true

        def sourceSets(Closure<?> closure) {
            sourceSets.configure closure
        }
    }

    HoloEverywhereExtension(Project project, Instantiator instantiator) {
        this.project = project
        this.instantiator = instantiator

        this.library = new LibraryContainer(this)
        this.supportV4 = new SupportV4Container(this)
        this.repository = new RepositoryContainer(this)
        this.resbuilder = new ResbuilderContainer(project, instantiator)

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
    def Include include = Include.Yes
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

    def void include(String name) {
        this.include = Include.find(name, Include.Inhert)
    }

    private static <T> T call(Closure<?> closure, T t) {
        closure.delegate = t
        closure.call(t)
        return t
    }
}
