package org.holoeverywhere.plugin.extension

import org.apache.maven.artifact.ant.Authentication
import org.gradle.api.Project

class UploadContainer {
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
            url = ''
        }

        def void obtainCredentials(String key) {
            userName = project.properties.get("${key}UserName", null)
            password = project.properties.get("${key}Password", null)
            passphrase = project.properties.get("${key}Passphrase", null)
            privateKey = project.properties.get("${key}PrivateKey", null)

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

    class LicenseContainer {
        def String name
        def String url
        def String comments
        def String distribution = 'repo'

        def void mit(Closure closure = null) {
            name = 'MIT License'
            url = 'http://opensource.org/licenses/MIT'
            HoloEverywhereExtension.call(closure, this)
        }

        def void apache(Closure closure = null) {
            name = 'Apache License, Version 2.0'
            url = 'http://opensource.org/licenses/Apache-2.0'
            HoloEverywhereExtension.call(closure, this)
        }

        def void bsd3(Closure closure = null) {
            name = 'The BSD 3-Clause License'
            url = 'http://opensource.org/licenses/BSD-3-Clause'
            HoloEverywhereExtension.call(closure, this)
        }

        def void bsd2(Closure closure = null) {
            name = 'The BSD 2-Clause License'
            url = 'http://opensource.org/licenses/BSD-2-Clause'
            HoloEverywhereExtension.call(closure, this)
        }

        def void gpl2(Closure closure = null) {
            name = 'GNU General Public License, version 2'
            url = 'http://opensource.org/licenses/GPL-2.0'
            HoloEverywhereExtension.call(closure, this)
        }

        def void gpl3(Closure closure = null) {
            name = 'GNU General Public License, version 3'
            url = 'http://opensource.org/licenses/GPL-3.0'
            HoloEverywhereExtension.call(closure, this)
        }

        def void lgpl2(Closure closure = null) {
            name = 'The GNU Lesser General Public License, version 2.1'
            url = 'http://opensource.org/licenses/LGPL-2.1'
            HoloEverywhereExtension.call(closure, this)
        }

        def void lgpl3(Closure closure = null) {
            name = 'The GNU Lesser General Public License, version 3.0'
            url = 'http://opensource.org/licenses/LGPL-3.0'
            HoloEverywhereExtension.call(closure, this)
        }
    }

    class ScmContainer {
        def String url
        def String connection
        def String developerConnection
    }

    UploadContainer(Project project) {
        this.project = project

        this.repository = new RepositoryContainer(project)
        this.license = new LicenseContainer()
        this.scm = new ScmContainer()
    }

    private final Project project
    def final RepositoryContainer repository
    def final LicenseContainer license
    def final ScmContainer scm
    def String url

    def RepositoryContainer repository(Closure<?> closure) {
        return HoloEverywhereExtension.call(closure, repository)
    }

    def LicenseContainer license(Closure<?> closure) {
        return HoloEverywhereExtension.call(closure, license)
    }

    def ScmContainer scm(Closure<?> closure) {
        return HoloEverywhereExtension.call(closure, scm)
    }

    def void github(String username, String repo) {
        url = "https://github.com/${username}/${repo}"
        scm.url = "https://github.com/${username}/${repo}"
        scm.connection = "scm:git@github.com:${username}/${repo}.git"
        scm.developerConnection = "scm:git@github.com:${username}/${repo}.git"
    }
}

