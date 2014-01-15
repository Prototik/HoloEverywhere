package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.upload.LicenseContainer
import org.holoeverywhere.plugin.extension.upload.RepositoryContainer
import org.holoeverywhere.plugin.extension.upload.ScmContainer

class UploadContainer implements Configurable<UploadContainer> {
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

    def String group
    def String artifact
    def String version
    def String packaging
    def String description

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
    UploadContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}

