package org.holoeverywhere.plugin.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.HoloEverywhereCorePlugin

class HoloEverywhereExtension extends IncludeContainer implements Configurable<HoloEverywhereExtension> {
    public static final String HOLO_EVERYWHERE_GROUP = 'org.holoeverywhere'
    public static final String HOLO_EVERYWHERE_NAME = 'library'
    public static final String HOLO_EVERYWHERE_REPO = 'http://192.241.191.41/repo/'
    public static final String HOLO_EVERYWHERE_SNAPSHOT_REPO = 'http://192.241.191.41/snapshot/'

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

    HoloEverywhereExtension(Project project, Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        include 'yes'

        this.project = project
        this.instantiator = instantiator

        this.app = new AppContainer(project)
        this.library = new LibraryContainer(this)
        this.support = new SupportContainer(this)
        this.repository = new RepositoryContainer(this, project, repositoryFactory).defaultRepo()
        this.resbuilder = new ResbuilderContainer(project, instantiator)
        this.publish = new PublishContainer(this, project, repositoryFactory)
        this.signing = new SigningContainer(project)

        this.addons = project.container(Addon, new NamedDomainObjectFactory<Addon>() {
            @Override
            def Addon create(String name) {
                String[] parts = name.split(':')
                return parts.length == 1 ? new Addon("addon-${name}") :
                        new Addon(parts[0], parts[1], parts.length == 3 ? parts[2] : null)
            }
        })
        this.addons.metaClass.propertyMissing = { String name ->
            this.addons.maybeCreate(name)
        }
    }

    private final Project project
    private final Instantiator instantiator

    def final NamedDomainObjectContainer<Addon> addons
    def final AppContainer app
    def final LibraryContainer library
    def final SupportContainer support
    def final RepositoryContainer repository
    def final ResbuilderContainer resbuilder
    def final PublishContainer publish
    def final SigningContainer signing
    def boolean forceJarInsteadAar = false

    def NamedDomainObjectContainer<Addon> addons(Closure<?> closure) {
        return addons.configure(closure)
    }

    def AppContainer app(Closure<?> closure) {
        return app.configure(closure);
    }

    def LibraryContainer library(Closure<?> closure) {
        return library.configure(closure);
    }

    def SupportContainer support(Closure<?> closure) {
        return support.configure(closure);
    }

    def SupportContainer supportV4(Closure<?> closure) {
        project.logger.warn("holoeverywhere.supportV4 is deprecated, use holoeverywhere.support instead")
        return support(closure)
    }

    def RepositoryContainer repository(Closure<?> closure) {
        return repository.configure(closure);
    }

    def ResbuilderContainer resbuilder(Closure<?> closure) {
        return resbuilder.configure(closure)
    }

    def PublishContainer publish(Closure<?> closure) {
        return publish.configure(closure);
    }

    def SigningContainer signing(Closure<?> closure) {
        return signing.configure(closure)
    }

    def Dependency aar(String... libraries) {
        final Project project = this.project;
        Dependency lastDependency = null
        libraries.each { String libraryName ->
            project.dependencies.add(HoloEverywhereCorePlugin.LIBRARIES_CONFIGURATION, libraryName + (forceJarInsteadAar ? '@jar' : '@aar'))
            lastDependency = project.dependencies.add(HoloEverywhereCorePlugin.LIBRARIES_CONFIGURATION, libraryName)
        }
        return lastDependency
    }

    @Override
    HoloEverywhereExtension configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }

    private static final String EXTENSION_NAME = 'holoeverywhere'

    public
    static HoloEverywhereExtension getOrCreateExtension(Project project, Instantiator instantiator, BaseRepositoryFactory repositoryFactory) {
        project.extensions.findByName(EXTENSION_NAME) as HoloEverywhereExtension ?: project.extensions.create(EXTENSION_NAME, HoloEverywhereExtension, project, instantiator, repositoryFactory)
    }

    def void apply(String path) {
        project.apply from: new File(project.rootProject.projectDir, path).absolutePath
    }
}
