package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable

class SupportContainer extends IncludeContainer implements Configurable<SupportContainer> {
    private static final String INHERIT = 'inherit'
    private static final String DEFAULT_GROUP = 'com.android.support'
    private static final String DEFAULT_NAME = 'support-v4'

    SupportContainer(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = INHERIT
    def String name = INHERIT
    def String version = INHERIT

    @Override
    SupportContainer configure(Closure closure) {
        super.configure(closure) as SupportContainer
    }

    public boolean artifactOverride() {
        return (group != INHERIT && name != INHERIT && version != INHERIT) || version != INHERIT
    }

    public String resolveArtifactName() {
        if (group == INHERIT && name == INHERIT) {
            group = DEFAULT_GROUP
            name = DEFAULT_NAME
        }
        return "${group}:${name}:${version}"
    }
}
