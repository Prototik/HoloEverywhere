package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable

class SupportV4Container extends IncludeContainer implements Configurable<SupportV4Container> {
    private static final String INHERT = 'inhert'
    private static final String DEFAULT_GROUP = 'com.android.support'
    private static final String DEFAULT_NAME = 'support-v4'

    SupportV4Container(HoloEverywhereExtension extension) {
        super(extension)
    }

    def String group = INHERT
    def String name = INHERT
    def String version = INHERT

    @Override
    SupportV4Container configure(Closure closure) {
        super.configure(closure) as SupportV4Container
    }

    public boolean artifactOverride() {
        return (group != INHERT && name != INHERT && version != INHERT) || version != INHERT
    }

    public String resolveArtifactName() {
        if (group == INHERT && name == INHERT) {
            group = DEFAULT_GROUP
            name = DEFAULT_NAME
        }
        return "${group}:${name}:${version}"
    }
}
