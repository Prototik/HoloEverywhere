package org.holoeverywhere.plugin.extension.signing

import com.android.build.gradle.internal.dsl.SigningConfigDsl
import com.android.builder.model.SigningConfig
import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class SignConfiguration implements Configurable {
    SignConfiguration(Project project) {
        this.project = project
    }

    private final Project project
    def String storeFile
    def String keyAlias
    def String storePassword
    def String keyPassword

    @Override
    SignConfiguration configure(Closure closure) {
        ConfigureUtil.configure(closure, this)
        return this
    }

    public boolean valid() {
        return storeFile && keyAlias && storePassword && keyPassword && new File(storeFile).exists()
    }

    public SignConfiguration key(String key) {
        if (key.length() > 0) {
            storeFile = project.properties.get("${key}StoreFile", null)
            keyAlias = project.properties.get("${key}KeyAlias", null)
            storePassword = project.properties.get("${key}StorePassword", null)
            keyPassword = project.properties.get("${key}KeyPassword", null)
        }
        final String name = project.rootProject.name
        if (name.length() > 0) {
            storeFile = project.properties.get("${name}_${key}StoreFile", storeFile)
            keyAlias = project.properties.get("${name}_${key}KeyAlias", keyAlias)
            storePassword = project.properties.get("${name}_${key}StorePassword", storePassword)
            keyPassword = project.properties.get("${name}_${key}KeyPassword", keyPassword)
        }
        return this
    }

    def SigningConfig obtainConfig(String name) {
        SigningConfig config = new SigningConfigDsl(name)
        config.storeFile = new File(storeFile)
        config.keyAlias = keyAlias
        config.storePassword = storePassword
        config.keyPassword = keyPassword
        return config
    }

    def SigningConfig obtainDebugConfig(String name = 'debug') {
        SigningConfig config = new SigningConfigDsl(name)
        config.initDebug()
        return config
    }

    def SigningConfig obtainMaybeDebugConfig(String name = 'debug') {
        return valid() ? obtainConfig(name) : obtainDebugConfig(name)
    }
}
