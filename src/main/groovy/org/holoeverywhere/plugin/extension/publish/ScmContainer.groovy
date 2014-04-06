package org.holoeverywhere.plugin.extension.publish

import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class ScmContainer implements Configurable<ScmContainer> {
    def String url
    def String connection
    def String developerConnection

    @Override
    ScmContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}
