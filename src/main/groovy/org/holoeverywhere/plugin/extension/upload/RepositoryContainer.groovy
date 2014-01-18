package org.holoeverywhere.plugin.extension.upload

import org.apache.maven.artifact.ant.Authentication
import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.ExProperties
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

class RepositoryContainer implements Configurable<RepositoryContainer> {
    private final Project project

    RepositoryContainer(Project project) {
        this.project = project
    }

    def String url
    def String snapshotUrl
    def String userName
    def String password
    def String passphrase
    def String privateKey

    def void sonatype(Closure<?> closure = null) {
        url = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
        snapshotUrl = 'https://oss.sonatype.org/content/repositories/snapshots/'
        obtainCredentials('sonatype')
        configure(closure)
    }

    def void holoeverywhere(Closure<?> closure = null) {
        url = HoloEverywhereExtension.HOLO_EVERYWHERE_REPO
        snapshotUrl = HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO
        obtainCredentials('holoeverywhere')
        configure(closure)
    }

    def void local(Closure<?> closure = null) {
        snapshotUrl = url = new File(System.getProperty("user.home"), "/.m2/repository").absoluteFile.toURI().toURL()
        obtainCredentials('')
        configure(closure)
    }

    def void obtainCredentials(String key) {
        ExProperties props = new ExProperties(project)
        userName = props.property(key, 'UserName')
        password = props.property(key, 'Password')
        passphrase = props.property(key, 'Passphrase')
        privateKey = props.property(key, 'PrivateKey')
        url = props.get(key, 'URL') ?: url
        snapshotUrl = props.get(key, 'SnapshotURL') ?: snapshotUrl
    }

    def boolean isSnapshot() {
        return (project.rootProject.version as String).contains("-SNAPSHOT")
    }

    def String resolveUrl() {
        return isSnapshot() && snapshotUrl != null ? snapshotUrl : url
    }

    @Override
    def Object asType(Class clazz) {
        if (clazz == Authentication) {
            Authentication authentication = new Authentication()
            authentication.userName = userName
            authentication.password = password
            authentication.passphrase = passphrase
            authentication.privateKey = privateKey
            return authentication
        }
        return super.asType(clazz)
    }

    @Override
    RepositoryContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}
