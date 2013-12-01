package org.holoeverywhere.resbuilder.tasks

import org.gradle.api.tasks.TaskAction
import org.holoeverywhere.resbuilder.dsl.ResbuilderSourceSet
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class ResbuilderFormatTask extends ResbuilderDefaultTask {
    def boolean readonly = false

    @TaskAction
    def format() {
        if (source == null) return
        DumperOptions options = new DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        Yaml yaml = new Yaml(options)

        source.each { ResbuilderSourceSet set ->
            set.resources.files.each { File source ->
                Object data = yaml.load(new InputStreamReader(new FileInputStream(source), 'utf-8'))
                data = sort(data)
                if (readonly) {
                    project.logger.info("File \"%s\", data:\n%s", source.absolutePath, yaml.dump(data))
                } else {
                    byte[] dataRaw = yaml.dump(data).getBytes('utf-8')

                    OutputStream os = new FileOutputStream(source)
                    os.write(dataRaw)
                    os.close()
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