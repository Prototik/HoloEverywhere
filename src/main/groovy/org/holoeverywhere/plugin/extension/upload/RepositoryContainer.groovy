package org.holoeverywhere.plugin.extension.upload

import org.apache.maven.artifact.ant.Authentication
import org.gradle.api.Project
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

class RepositoryContainer {
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

    def void sonatype() {
        url = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
        snapshotUrl = 'https://oss.sonatype.org/content/repositories/snapshots/'
        obtainCredentials('sonatype')
    }

    def void holoeverywhere() {
        url = HoloEverywhereExtension.HOLO_EVERYWHERE_REPO
        snapshotUrl = HoloEverywhereExtension.HOLO_EVERYWHERE_SNAPSHOT_REPO
        obtainCredentials('holoeverywhere')
    }

    def void local() {
        snapshotUrl = url = "file://${new File(System.getProperty("user.home"), "/.m2/repository").absolutePath}"
        obtainCredentials('')
    }

    def void obtainCredentials(String key) {
        if (key.length() > 0) {
            userName = project.properties.get("${key}UserName", null)
            password = project.properties.get("${key}Password", null)
            passphrase = project.properties.get("${key}Passphrase", null)
            privateKey = project.properties.get("${key}PrivateKey", null)
        }

        final String name = project.rootProject.name
        if (name != null) {
            userName = project.properties.get("${name}_${key}UserName", userName)
            password = project.properties.get("${name}_${key}Password", password)
            passphrase = project.properties.get("${name}_${key}Passphrase", passphrase)
            privateKey = project.properties.get("${name}_${key}PrivateKey", privateKey)
        }
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
}
