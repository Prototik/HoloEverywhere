package org.holoeverywhere.plugin.extension

import org.gradle.api.Project

public class ExProperties extends Properties {
    def final String projectName;

    public ExProperties(Project project) {
        super()
        projectName = project.rootProject.name ?: "default"
        putAll(project.ext.properties as Map<String, String>)
    }

    public String property(String key, String name) {
        return get("${projectName}_${key}${name}".intern()) ?: get("${key}${name}".intern())
    }
}
