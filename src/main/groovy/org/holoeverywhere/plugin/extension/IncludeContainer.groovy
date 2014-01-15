package org.holoeverywhere.plugin.extension

import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

abstract class IncludeContainer implements Configurable<IncludeContainer> {
    public static enum Include {
        Inhert('inhert'), Yes('yes'), No('no');

        String localName;

        Include(String localName) {
            this.localName = localName
        }

        public static Include find(String localName, Include defaultValue) {
            values().find { it.localName == localName } ?: defaultValue
        }
    }

    IncludeContainer(HoloEverywhereExtension extension) {
        this.extension = extension
    }

    private final HoloEverywhereExtension extension
    def Include include = Include.Inhert

    def void include(String name) {
        this.include = Include.find(name, Include.Inhert)
    }

    def boolean include() {
        return include == Include.Yes || (include == Include.Inhert && extension.include == Include.Yes)
    }

    @Override
    IncludeContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}

