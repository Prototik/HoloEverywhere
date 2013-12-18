package org.holoeverywhere.plugin.extension.upload

import org.gradle.util.ConfigureUtil
import org.holoeverywhere.plugin.extension.HoloEverywhereExtension

class LicenseContainer {
    def String name
    def String url
    def String comments
    def String distribution = 'repo'

    def void mit(Closure closure = null) {
        name = 'MIT License'
        url = 'http://opensource.org/licenses/MIT'
        ConfigureUtil.configure(closure, this)
    }

    def void apache(Closure closure = null) {
        name = 'Apache License, Version 2.0'
        url = 'http://opensource.org/licenses/Apache-2.0'
        ConfigureUtil.configure(closure, this)
    }

    def void bsd3(Closure closure = null) {
        name = 'The BSD 3-Clause License'
        url = 'http://opensource.org/licenses/BSD-3-Clause'
        ConfigureUtil.configure(closure, this)
    }

    def void bsd2(Closure closure = null) {
        name = 'The BSD 2-Clause License'
        url = 'http://opensource.org/licenses/BSD-2-Clause'
        ConfigureUtil.configure(closure, this)
    }

    def void gpl2(Closure closure = null) {
        name = 'GNU General Public License, version 2'
        url = 'http://opensource.org/licenses/GPL-2.0'
        ConfigureUtil.configure(closure, this)
    }

    def void gpl3(Closure closure = null) {
        name = 'GNU General Public License, version 3'
        url = 'http://opensource.org/licenses/GPL-3.0'
        ConfigureUtil.configure(closure, this)
    }

    def void lgpl2(Closure closure = null) {
        name = 'The GNU Lesser General Public License, version 2.1'
        url = 'http://opensource.org/licenses/LGPL-2.1'
        ConfigureUtil.configure(closure, this)
    }

    def void lgpl3(Closure closure = null) {
        name = 'The GNU Lesser General Public License, version 3.0'
        url = 'http://opensource.org/licenses/LGPL-3.0'
        ConfigureUtil.configure(closure, this)
    }
}

