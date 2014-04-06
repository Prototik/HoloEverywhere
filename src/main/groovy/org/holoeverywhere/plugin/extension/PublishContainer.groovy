package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.api.publish.maven.internal.publisher.MavenProjectIdentity
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.publish.LicenseContainer
import org.holoeverywhere.plugin.extension.publish.ScmContainer

class PublishContainer implements Configurable<PublishContainer>, MavenProjectIdentity {
    PublishContainer(HoloEverywhereExtension extension, Project project) {
        this.project = project

        this.repository = new RepositoryContainer(extension, project)
        this.license = new LicenseContainer()
        this.scm = new ScmContainer()

        this.artifacts = new LinkedHashSet<>()
    }

    private final Project project
    def final RepositoryContainer repository
    def final LicenseContainer license
    def final ScmContainer scm
    def final Set artifacts
    def String url
    def String groupId
    def String artifactId
    def String version
    def String packaging

    def void artifact(Object object) {
        artifacts.add(object)
    }

    def void setArtifacts(Collection artifacts) {
        this.artifacts.clear();
        this.artifacts.addAll(artifacts)
    }

    def RepositoryContainer repository(Closure<?> closure) {
        return ConfigureUtil.configure(closure, repository)
    }

    def LicenseContainer license(Closure<?> closure) {
        return ConfigureUtil.configure(closure, license)
    }

    def ScmContainer scm(Closure<?> closure) {
        return ConfigureUtil.configure(closure, scm)
    }

    def void github(String username, String repo) {
        url = "https://github.com/${username}/${repo}"
        scm.url = "https://github.com/${username}/${repo}"
        scm.connection = "scm:git@github.com:${username}/${repo}.git"
        scm.developerConnection = "scm:git@github.com:${username}/${repo}.git"
    }

    @Override
    PublishContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}

