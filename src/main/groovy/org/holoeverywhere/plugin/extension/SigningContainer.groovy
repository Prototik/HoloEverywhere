package org.holoeverywhere.plugin.extension

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.signing.SignConfiguration

class SigningContainer implements Configurable<SigningContainer> {
    SigningContainer(Project project) {
        this.project = project

        this.release = new SignConfiguration(project)
        this.debug = new SignConfiguration(project)
    }

    private final Project project
    def final SignConfiguration release
    def final SignConfiguration debug
    def boolean enable = true

    def SignConfiguration release(Closure<?> closure) {
        return release.configure(closure)
    }

    def SignConfiguration debug(Closure<?> closure) {
        return debug.configure(closure)
    }

    @Override
    SigningContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this)
        return this
    }
}

