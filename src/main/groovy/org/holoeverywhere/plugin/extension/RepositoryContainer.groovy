package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class RepositoryContainer extends IncludeContainer implements Configurable<RepositoryContainer> {
    private final Project project
    private final BaseRepositoryFactory repositoryFactory

    RepositoryContainer(HoloEverywhereExtension extension, Project project, BaseRepositoryFactory repositoryFactory) {
        super(extension)
        this.project = project
        this.repositoryFactory = repositoryFactory
    }

    def String url
    def String snapshotUrl
    def String userName
    def String password
    def String passphrase
    def String privateKey
    def String name
    def Include snapshot = Include.Inhert

    def void snapshot(String snapshot) {
        this.snapshot = Include.find(snapshot, Include.Yes)
    }

    def boolean snapshot() {
        return snapshot == Include.Yes || snapshot == Include.Inhert && (project.rootProject.version as String).contains("-SNAPSHOT")
    }

    def void sonatype(Closure<?> closure = null) {
        url = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
        snapshotUrl = 'https://oss.sonatype.org/content/repositories/snapshots/'
        name = 'Sonatype OSS'
        obtainCredentials('sonatype')
        configure(closure)
    }

    def void holoeverywhere(Closure<?> closure = null) {
        url = HoloEverywhereExtension.HOLO_EVERYWHERE_REPO
        snapshotUrl = HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO
        name = 'HoloEverywhere'
        obtainCredentials('holoeverywhere')
        configure(closure)
    }

    def void local(Closure<?> closure = null) {
        local(new File(System.getProperty("user.home"), "/.m2/repository").absolutePath, closure)
    }

    def void local(String path, Closure<?> closure = null) {
        snapshotUrl = url = new File(path).toURI().toURL()
        name = 'local'
        obtainCredentials('')
        configure(closure)
    }

    def void mavenLocal(Closure<?> closure = null) {
        snapshotUrl = url = repositoryFactory.createMavenLocalRepository().url as String
        name = 'Maven Local'
        obtainCredentials('mavenLocal')
        configure(closure)
    }

    def void obtainCredentials(String key) {
        ExProperties props = new ExProperties(project)
        userName = props.property(key, 'UserName')
        password = props.property(key, 'Password')
        passphrase = props.property(key, 'Passphrase')
        privateKey = props.property(key, 'PrivateKey')
        url = props.property(key, 'URL') ?: url
        snapshotUrl = props.property(key, 'SnapshotURL') ?: snapshotUrl
    }

    def String resolveUrl() {
        return (snapshot() && snapshotUrl != null) ? snapshotUrl : url
    }

    @Override
    RepositoryContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }

    def MavenArtifactRepository addToHandler(RepositoryHandler handler) {
        addToHandler(handler, resolveUrl())
    }

    def MavenArtifactRepository addToHandler(RepositoryHandler handler, boolean snapshot) {
        addToHandler(handler, snapshot && this.snapshotUrl != null ? this.snapshotUrl : this.url)
    }

    def MavenArtifactRepository addToHandler(RepositoryHandler handler, String url) {
        handler.maven { MavenArtifactRepository repo ->
            repo.name = this.name
            repo.url = this.url
            repo.credentials { PasswordCredentials credentials ->
                credentials.username = this.userName
                credentials.password = this.password
            }
        }
    }

    def RepositoryContainer defaultRepo(Closure<?> closure = null) {
        holoeverywhere(closure)
        return this
    }
}
