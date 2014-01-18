package org.holoeverywhere.plugin.extension.upload

import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class LicenseContainer implements Configurable<LicenseContainer> {
    def String name
    def String url
    def String comments
    def String distribution = 'repo'

    def void mit(Closure closure = null) {
        name = 'MIT License'
        url = 'http://opensource.org/licenses/MIT'
        configure(closure)
    }

    def void apache(Closure closure = null) {
        name = 'Apache License, Version 2.0'
        url = 'http://opensource.org/licenses/Apache-2.0'
        configure(closure)
    }

    def void bsd3(Closure closure = null) {
        name = 'The BSD 3-Clause License'
        url = 'http://opensource.org/licenses/BSD-3-Clause'
        configure(closure)
    }

    def void bsd2(Closure closure = null) {
        name = 'The BSD 2-Clause License'
        url = 'http://opensource.org/licenses/BSD-2-Clause'
        configure(closure)
    }

    def void gpl2(Closure closure = null) {
        name = 'GNU General Public License, version 2'
        url = 'http://opensource.org/licenses/GPL-2.0'
        configure(closure)
    }

    def void gpl3(Closure closure = null) {
        name = 'GNU General Public License, version 3'
        url = 'http://opensource.org/licenses/GPL-3.0'
        configure(closure)
    }

    def void lgpl2(Closure closure = null) {
        name = 'The GNU Lesser General Public License, version 2.1'
        url = 'http://opensource.org/licenses/LGPL-2.1'
        configure(closure)
    }

    def void lgpl3(Closure closure = null) {
        name = 'The GNU Lesser General Public License, version 3.0'
        url = 'http://opensource.org/licenses/LGPL-3.0'
        configure(closure)
    }

    def propertyMissing(String name) {
        try {
            this."$name"()
        } catch (MissingMethodException e) {
            throw new MissingPropertyException(name, LicenseContainer)
        }
    }

    @Override
    LicenseContainer configure(Closure closure) {
        ConfigureUtil.configure(closure, this, false)
    }
}

