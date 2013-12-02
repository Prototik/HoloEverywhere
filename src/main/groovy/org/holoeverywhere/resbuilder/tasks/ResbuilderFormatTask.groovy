package org.holoeverywhere.resbuilder.tasks

import org.gradle.api.tasks.TaskAction
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class ResbuilderFormatTask extends ResbuilderDefaultTask {
    def boolean check = false

    @TaskAction
    def format() {
        if (source == null) return
        DumperOptions options = new DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        Yaml yaml = new Yaml(options)

        source.each { ResbuilderSourceSet set ->
            set.resources.files.each { File source ->
                String sourceData = new String(source.readBytes(), 'utf-8')
                Object data = yaml.load(sourceData)
                data = sort(data)
                String sortedData = yaml.dump(data)
                if (check) {
                    if(!sortedData.equals(sourceData)) {
                        project.logger.error("File \"${source.absolutePath}\" has an incorrect format")
                    }
                } else {
                    source.write(sortedData, 'utf-8')
                }
            }
        }
    }

    def Object sort(Object o) {
        if (o instanceof Map) {
            Map map = o as Map
            map = map.sort()
            map.entrySet().each { def entry -> entry.setValue(sort(entry.value)) }
            return map
        }
        if (o instanceof List) {
            List list = o as List
            list = list.sort()
            list.eachWithIndex { def entry, int i -> list.putAt(i, sort(entry)) }
            return list
        }
        return o;
    }
}