package org.holoeverywhere.resbuilder.tasks

import org.gradle.api.tasks.TaskAction
import org.holoeverywhere.resbuilder.api.Type
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.yaml.snakeyaml.Yaml

abstract class ResbuilderBaseProcesserTask extends ResbuilderDefaultTask {
    class TypeContainer {
        def Type type
        def Object typeState

        def int hashCode() { return type.hashCode() }
    }

    private class TypeSet extends HashSet<TypeContainer> {
        def boolean add(Type type) {
            TypeContainer container = new TypeContainer()
            container.type = type
            return add(container)
        }
    }

    final TypeSet types = new TypeSet()
    def boolean warnUnknownTypes = false

    @TaskAction
    def process() {
        if (source == null) return

        types*.type*.bind(this)

        Yaml yaml = new Yaml()
        source.each { ResbuilderSourceSet set ->
            final File outputDir = set.output.resourcesDir
            project.fileTree(outputDir) {
                include 'values/rb_*.xml'
                include 'values-*/rb_*.xml'
            }*.delete()
            set.resources.files.each { File source ->
                String processerName = source.parentFile.name
                int i = processerName.lastIndexOf('$')
                if (i > 0) {
                    processerName = processerName.substring(i + 1)
                }
                final TypeContainer type = types.find { TypeContainer type -> type.type.name == processerName }
                if (type != null) {
                    final Map<String, ?> map = yaml.load(new InputStreamReader(new FileInputStream(source), 'utf-8')) as Map<String, ?>
                    if (map != null) {
                        type.typeState = type.type.process(map, outputDir, type.typeState, source)
                    }
                } else if (warnUnknownTypes) {
                    project.logger.warn("Type processer not found: ${processerName}. File: ${source.absolutePath}")
                }
            }
        }
        types.each { TypeContainer type -> type.type.flush(type.typeState) }
    }
}
